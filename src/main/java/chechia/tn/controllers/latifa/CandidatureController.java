package chechia.tn.controllers.latifa;

import chechia.tn.entities.Candidature;
import chechia.tn.entities.Candidature.Type;
import chechia.tn.entities.Opportunite;
import chechia.tn.service.latifa.ServiceCandidature;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CandidatureController implements Initializable {
    // Constante pour le dossier de téléchargement des CVs
    private static final String UPLOAD_DIR = "uploads/cv/";

    @FXML private TextField userIdField;
    @FXML private ComboBox<Type> typeComboBox;
    @FXML private TextArea experienceArea;
    @FXML private Spinner<Integer> anneeXpSpinner;
    @FXML private Button uploadCvButton;
    @FXML private Label cvLabel;
    @FXML private Button submitButton;

    private File selectedCvFile;
    private Opportunite opportunite;
    private final ServiceCandidature serviceCandidature = new ServiceCandidature();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTypeComboBox();
        setupAnneeXpSpinner();
        setupValidation();

        // Ajuster la taille de la fenêtre après un court délai pour s'assurer que tous les éléments sont chargés
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) submitButton.getScene().getWindow();
            if (stage != null) {
                stage.setMinWidth(600);
                stage.setMinHeight(800);
                stage.setWidth(600);
                stage.setHeight(800);

                // Centrer la fenêtre sur l'écran
                stage.centerOnScreen();
            }
        });
    }

    private void setupTypeComboBox() {
        // Initialiser le ComboBox des types
        typeComboBox.getItems().addAll(Type.values());
        typeComboBox.setConverter(new StringConverter<Type>() {
            @Override
            public String toString(Type type) {
                if (type == null) return "";
                return type.name();
            }

            @Override
            public Type fromString(String string) {
                return string == null ? null : Type.valueOf(string.toUpperCase());
            }
        });
    }

    private void setupAnneeXpSpinner() {
        // Configuration du Spinner pour les années d'expérience
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, 0);
        anneeXpSpinner.setValueFactory(valueFactory);
    }

    private void setupValidation() {
        // Valider les champs en temps réel
        userIdField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        typeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        experienceArea.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }

    private void validateForm() {
        boolean isValid = userIdField.getText() != null && !userIdField.getText().trim().isEmpty()
                && typeComboBox.getValue() != null
                && experienceArea.getText() != null && !experienceArea.getText().trim().isEmpty()
                && selectedCvFile != null;
        submitButton.setDisable(!isValid);
    }

    @FXML
    private void handleCvUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");

        // Ne montrer que les images
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Images", ".jpg", ".jpeg", "*.png");
        fileChooser.getExtensionFilters().add(imageFilter);
        fileChooser.setSelectedExtensionFilter(imageFilter);

        // Ouvrir dans le dossier Images
        String userHome = System.getProperty("user.home");
        File picturesDir = new File(userHome + "/Pictures");
        if (picturesDir.exists()) {
            fileChooser.setInitialDirectory(picturesDir);
        }

        File file = fileChooser.showOpenDialog(uploadCvButton.getScene().getWindow());
        if (file != null) {
            try {
                // Vérifier que c'est bien une image
                String fileName = file.getName().toLowerCase();
                if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") && !fileName.endsWith(".png")) {
                    showError("Type de fichier non valide", "Veuillez sélectionner une image (JPG, JPEG ou PNG)");
                    return;
                }

                System.out.println("Image sélectionnée: " + file.getAbsolutePath());

                // Créer le répertoire de destination s'il n'existe pas
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    boolean created = uploadDir.mkdirs();
                    if (!created) {
                        throw new IOException("Impossible de créer le dossier d'upload");
                    }
                }

                // Générer un nom de fichier unique
                String originalFileName = file.getName();
                String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
                String uniqueFileName = System.currentTimeMillis() + extension;
                System.out.println("Nouveau nom de fichier: " + uniqueFileName);

                // Créer le fichier de destination
                File destinationFile = new File(uploadDir, uniqueFileName);
                System.out.println("Chemin de destination: " + destinationFile.getAbsolutePath());

                // Copier le fichier
                Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Mettre à jour l'interface utilisateur
                selectedCvFile = destinationFile;
                cvLabel.setText(originalFileName);

                // Afficher une prévisualisation de l'image
                Image image = new Image(file.toURI().toString());
                ImageView preview = new ImageView(image);
                preview.setFitHeight(100);
                preview.setFitWidth(100);
                preview.setPreserveRatio(true);
                cvLabel.setGraphic(preview);

                validateForm();

                // Afficher un message de succès
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("L'image a été uploadée avec succès !");
                alert.showAndWait();

            } catch (IOException e) {
                System.err.println("Erreur détaillée: " + e.getMessage());
                e.printStackTrace();
                showError("Erreur d'upload", "Impossible d'uploader l'image. " + e.getMessage());
            }
        }
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleSubmit() {
        try {
            if (opportunite == null) {
                showError("Erreur", "Aucune opportunité n'est sélectionnée");
                return;
            }

            // On ne stocke que le nom du fichier, pas le chemin complet
            String cvFileName = selectedCvFile.getName();

            // Créer une nouvelle candidature avec l'opportunité directement dans le constructeur
            Candidature candidature = new Candidature(
                    0, // L'ID sera généré par la base de données
                    Integer.parseInt(userIdField.getText()),
                    cvFileName, // Stocker uniquement le nom du fichier
                    anneeXpSpinner.getValue(),
                    experienceArea.getText(),
                    Candidature.Etat.EN_ATTENTE,
                    typeComboBox.getValue(),
                    opportunite
            );

            // Sauvegarder la candidature dans la base de données
            serviceCandidature.add(candidature);

            // Afficher un message de succès
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Votre candidature a été envoyée avec succès !");
            successAlert.showAndWait();

            // Fermer la fenêtre
            ((Stage) submitButton.getScene().getWindow()).close();
        } catch (NumberFormatException e) {
            showError("Erreur", "L'ID utilisateur doit être un nombre valide");
        } catch (Exception e) {
            showError("Erreur", "Une erreur est survenue lors de l'envoi de la candidature : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) submitButton.getScene().getWindow()).close();
    }

    public void setOpportunite(Opportunite opportunite) {
        this.opportunite = opportunite;
        System.out.println("Opportunité définie avec ID: " + (opportunite != null ? opportunite.getId() : "null"));
    }
}