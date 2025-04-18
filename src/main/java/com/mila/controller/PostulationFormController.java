package com.mila.controller;

import com.mila.model.Postulation;
import com.mila.model.ProgrammeEchange;
import com.mila.service.PostulationService;
import com.mila.service.ProgrammeEchangeService;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class PostulationFormController implements Initializable {

    @FXML private VBox formContainer;
    @FXML private Label titleLabel;
    @FXML private ComboBox<ProgrammeEchange> programmeComboBox;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField ageField;
    @FXML private TextField emailField;
    @FXML private TextArea lettreMotivationField;
    @FXML private TextField cvField;
    @FXML private Label statusLabel;
    
    private byte[] cvData;
    
    private PostulationService postulationService;
    private ProgrammeEchangeService programmeService;
    private Postulation postulation;
    private boolean editMode = false;
    private MainController mainController;
    
    public PostulationFormController() {
        try {
            postulationService = new PostulationService();
            programmeService = new ProgrammeEchangeService();
            postulation = new Postulation();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'initialisation", "Impossible d'initialiser le formulaire de postulation.");
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            // Animation d'entrée du formulaire avec fade et translation
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), formContainer);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            
            TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.5), formContainer);
            slideIn.setFromY(20);
            slideIn.setToY(0);
            
            ParallelTransition parallelTransition = new ParallelTransition(fadeIn, slideIn);
            parallelTransition.play();

            // Animation du titre avec fade et scale
            FadeTransition titleFade = new FadeTransition(Duration.seconds(0.8), titleLabel);
            titleFade.setFromValue(0);
            titleFade.setToValue(1);
            
            ScaleTransition titleScale = new ScaleTransition(Duration.seconds(0.8), titleLabel);
            titleScale.setFromX(0.8);
            titleScale.setFromY(0.8);
            titleScale.setToX(1);
            titleScale.setToY(1);
            
            ParallelTransition titleAnimation = new ParallelTransition(titleFade, titleScale);
            titleAnimation.play();

            // Charger les programmes d'échange dans la combobox
            loadProgrammes();
            
            // Validation en temps réel des champs
            setupFieldValidation();

            // Initialiser les styles des champs
            initializeFieldStyles();
        });
    }
    
    private void setupFieldValidation() {
        // Validation de l'âge
        ageField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                ageField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            validateField(ageField, newValue.length() > 0 && Integer.parseInt(newValue.isEmpty() ? "0" : newValue) > 0);
        });

        // Validation de l'email
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateField(emailField, newValue.matches("^[A-Za-z0-9+_.-]+@(.+)$"));
        });

        // Validation du nom
        nomField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateField(nomField, !newValue.trim().isEmpty());
        });

        // Validation du prénom
        prenomField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateField(prenomField, !newValue.trim().isEmpty());
        });
    }

    private void validateField(TextField field, boolean isValid) {
        if (!isValid) {
            if (!field.getStyleClass().contains("error")) {
                field.getStyleClass().add("error");
                
                // Animation de shake pour indiquer l'erreur
                TranslateTransition shake = new TranslateTransition(Duration.millis(100), field);
                shake.setFromX(0);
                shake.setByX(10);
                shake.setCycleCount(3);
                shake.setAutoReverse(true);
                shake.play();
            }
        } else {
            field.getStyleClass().remove("error");
        }
    }
    
    /**
     * Charge les programmes d'échange dans la combobox
     */
    /**
     * Initialise les styles et les validateurs des champs du formulaire
     */
    private void initializeFieldStyles() {
        // Ajouter les classes de style CSS
        nomField.getStyleClass().add("form-field");
        prenomField.getStyleClass().add("form-field");
        ageField.getStyleClass().add("form-field");
        emailField.getStyleClass().add("form-field");
        lettreMotivationField.getStyleClass().add("form-field");
        cvField.getStyleClass().add("form-field");
        // Ajouter des validateurs
        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // Quand le champ perd le focus
                if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    emailField.getStyleClass().add("error");
                } else {
                    emailField.getStyleClass().remove("error");
                }
            }
        });
        
        // Ajouter des tooltips
        emailField.setTooltip(new Tooltip("Entrez une adresse email valide"));
        ageField.setTooltip(new Tooltip("Entrez votre âge (nombre entier)"));
    }
    
    /**
     * Définit le programme sélectionné dans le formulaire
     * @param programme Le programme d'échange sélectionné
     */
    public void setProgramme(ProgrammeEchange programme) {
        if (programme != null) {
            programmeComboBox.setValue(programme);
            programmeComboBox.setDisable(true); // Désactiver la modification
        }
    }
    
    private void loadProgrammes() {
        try {
            List<ProgrammeEchange> programmes = programmeService.getAll();
            ObservableList<ProgrammeEchange> programmesList = FXCollections.observableArrayList(programmes);
            programmeComboBox.setItems(programmesList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des programmes", e.getMessage());
        }
    }
    
    /**
     * Initialise le formulaire avec les données d'une postulation existante
     * @param postulation La postulation à modifier
     */
    public void initForEdit(Postulation postulation) {
        this.postulation = postulation;
        this.editMode = true;
        titleLabel.setText("Modifier une Postulation");
        
        // Remplir les champs avec les données de la postulation
        nomField.setText(postulation.getNom());
        prenomField.setText(postulation.getPrenom());
        ageField.setText(String.valueOf(postulation.getAge()));
        emailField.setText(postulation.getEmail());
        lettreMotivationField.setText(postulation.getLettreMotivation());
        // Le CV est en bytes[], on ne peut pas l'afficher directement
        // On pourrait ajouter un bouton pour télécharger/visualiser le CV
        
        // Sélectionner le programme d'échange correspondant
        for (ProgrammeEchange programme : programmeComboBox.getItems()) {
            if (programme.getId() == postulation.getPrgEchangeId()) {
                programmeComboBox.setValue(programme);
                break;
            }
        }
    }
    
    /**
     * Définit le contrôleur principal pour permettre la mise à jour de la vue principale
     * @param mainController Le contrôleur principal
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    /**
     * Gère l'action du bouton Enregistrer
     */
    @FXML
    private void handleSave() {
        if (validateInputs()) {
            try {
                // Récupérer les valeurs des champs
                ProgrammeEchange selectedProgramme = programmeComboBox.getValue();
                postulation.setPrgEchangeId(selectedProgramme.getId());
                postulation.setProgrammeEchange(selectedProgramme);
                postulation.setNom(nomField.getText());
                postulation.setPrenom(prenomField.getText());
                postulation.setAge(Integer.parseInt(ageField.getText()));
                postulation.setEmail(emailField.getText());
                postulation.setLettreMotivation(lettreMotivationField.getText());
                postulation.setProgrammeEchange(programmeComboBox.getValue());
                postulation.setCv(cvData);
                
                // Enregistrer la postulation
                boolean success = postulationService.ajouter(postulation);
                
                if (success) {
                    // Mettre à jour la vue principale si nécessaire
                    if (mainController != null) {
                        mainController.refreshData();
                    }
                    
                    // Fermer la fenêtre
                    closeWindow();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement", 
                            "L'opération a échoué. Veuillez réessayer.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de l'enregistrement", e.getMessage());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de format", "Format incorrect", 
                        "Veuillez entrer un nombre valide pour l'âge.");
            }
        }
    }
    
    /**
     * Gère l'action du bouton Annuler
     */
    @FXML
    private void handleCancel() {
        closeWindow();
    }

    @FXML
    private void handleSubmit() {
        if (validateInputs()) {
            try {
                // Récupérer les valeurs des champs
                ProgrammeEchange selectedProgramme = programmeComboBox.getValue();
                postulation.setPrgEchangeId(selectedProgramme.getId());
                postulation.setProgrammeEchange(selectedProgramme);
                postulation.setNom(nomField.getText());
                postulation.setPrenom(prenomField.getText());
                postulation.setAge(Integer.parseInt(ageField.getText()));
                postulation.setEmail(emailField.getText());
                postulation.setLettreMotivation(lettreMotivationField.getText());
                postulation.setCv(cvData);
                
                // Enregistrer la postulation
                boolean success = postulationService.ajouter(postulation);
                
                if (success) {
                    // Animation de succès avec fade out du formulaire
                    FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), formContainer);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    
                    // Animation de scale pour un effet de zoom out
                    ScaleTransition scaleOut = new ScaleTransition(Duration.seconds(0.5), formContainer);
                    scaleOut.setFromX(1.0);
                    scaleOut.setFromY(1.0);
                    scaleOut.setToX(0.8);
                    scaleOut.setToY(0.8);
                    
                    // Jouer les animations en parallèle
                    ParallelTransition transition = new ParallelTransition(fadeOut, scaleOut);
                    
                    transition.setOnFinished(event -> {
                        // Afficher un message de succès
                        showAlert(Alert.AlertType.INFORMATION, "Succès", "Postulation enregistrée", 
                                "Votre postulation a été enregistrée avec succès.");
                        
                        // Mettre à jour la vue principale pour rafraîchir le TableView
                        if (mainController != null) {
                            mainController.refreshData();
                        }
                        
                        // Fermer la fenêtre du formulaire
                        closeWindow();
                    });
                    
                    transition.play();
                } else {
                    // Animation de shake en cas d'erreur
                    TranslateTransition shake = new TranslateTransition(Duration.millis(100), formContainer);
                    shake.setFromX(0);
                    shake.setByX(10);
                    shake.setCycleCount(6);
                    shake.setAutoReverse(true);
                    shake.play();
                    
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement", 
                            "L'opération a échoué. Veuillez réessayer.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de l'enregistrement", e.getMessage());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de format", "Format incorrect", 
                        "Veuillez entrer un nombre valide pour l'âge.");
            }
        }
    }
    
    @FXML
    private void handleBrowseCV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner votre CV");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PDF", "*.pdf"),
            new FileChooser.ExtensionFilter("Documents", "*.doc", "*.docx")
        );
        
        File selectedFile = fileChooser.showOpenDialog(cvField.getScene().getWindow());
        if (selectedFile != null) {
            try {
                cvData = Files.readAllBytes(selectedFile.toPath());
                cvField.setText(selectedFile.getName());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de lecture du fichier", 
                         "Impossible de lire le fichier sélectionné.");
            }
        }
    }
    
    /**
     * Valide les entrées du formulaire
     * @return true si toutes les entrées sont valides, false sinon
     */
    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        
        if (programmeComboBox.getValue() == null) {
            errors.append("Veuillez sélectionner un programme\n");
        }
        if (nomField.getText().trim().isEmpty()) {
            errors.append("Veuillez entrer votre nom\n");
        }
        if (prenomField.getText().trim().isEmpty()) {
            errors.append("Veuillez entrer votre prénom\n");
        }
        if (ageField.getText().trim().isEmpty()) {
            errors.append("Veuillez entrer votre âge\n");
        } else {
            try {
                int age = Integer.parseInt(ageField.getText());
                if (age <= 0 || age > 120) {
                    errors.append("L'âge doit être compris entre 1 et 120\n");
                }
            } catch (NumberFormatException e) {
                errors.append("L'âge doit être un nombre entier\n");
            }
        }
        if (emailField.getText().trim().isEmpty()) {
            errors.append("Veuillez entrer votre email\n");
        } else if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.append("Veuillez entrer une adresse email valide\n");
        }
        if (lettreMotivationField.getText().trim().isEmpty()) {
            errors.append("Veuillez rédiger une lettre de motivation\n");
        }
        if (cvData == null) {
            errors.append("Le CV est requis\n");
        }
        
        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Veuillez corriger les erreurs suivantes :", errors.toString());
            return false;
        }
        return true;
    }
    
    /**
     * Ferme la fenêtre courante
     */
    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Affiche une boîte de dialogue d'alerte
     * @param type Le type d'alerte
     * @param title Le titre de l'alerte
     * @param header L'en-tête de l'alerte
     * @param content Le contenu de l'alerte
     */
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}