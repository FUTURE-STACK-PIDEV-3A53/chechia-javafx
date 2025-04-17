package chechia.tn.controllers;

import java.io.File;
import java.util.List;

import chechia.tn.entities.Candidature;
import chechia.tn.entities.Opportunite;
import chechia.tn.service.latifa.ServiceCandidature;
import chechia.tn.service.latifa.ServiceOpportunite;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OpportuniteController {

    @FXML
    private Button candidature_btn;
    @FXML
    private Button close;
    @FXML
    private Button deletebtn;
    @FXML
    private TableColumn<Candidature,String> descriptioncol;

    @FXML
    private AnchorPane formcandidature;
    @FXML
    private AnchorPane formopportunite;

    @FXML private Button addbtn;
    @FXML private TextField addopportunite_anneeexp;
    @FXML private TextField addopportunite_description;
    @FXML private TextField addopportunite_lieu;
    @FXML private TextField addopportunite_titre;
    @FXML private ComboBox<String> addopportunite_type;
    @FXML private TableColumn<Opportunite, Integer> annneeexpcol;
    @FXML private TableColumn<Opportunite, String> lieucol;
    @FXML private TableView<Opportunite> opportunite_tabeview;
    @FXML private TextField search;
    @FXML private TableColumn<Opportunite, String> titrecol;
    @FXML private TableColumn<Opportunite, String> typecol;
    @FXML private Button updatebtn;
    @FXML private Label username;
    @FXML
    private Button opportunite_btn;

    @FXML
    private TableView<Candidature> candidature_tableview;
    @FXML
    private TableColumn<Candidature, Integer> tid;
    @FXML
    private TableColumn<Candidature, Integer> tuser;
    @FXML
    private TableColumn<Candidature, String> tcv;
    @FXML
    private TableColumn<Candidature, String> texper;
    @FXML
    private TableColumn<Candidature, Integer> tannee;
    @FXML
    private TableColumn<Candidature, Candidature.Type> ttype;
    @FXML
    private TableColumn<Candidature, Candidature.Etat> tetat;
    @FXML
    private TableColumn<Candidature, Void> tactions;

    @FXML
    private TableColumn<Opportunite, Void> actionscol;

    private ObservableList<Opportunite> opportuniteList;
    private ServiceOpportunite serviceOpportunite = new ServiceOpportunite();
    private ServiceCandidature serviceCandidature = new ServiceCandidature();
    private ObservableList<Candidature> candidatureList;

    private void loadOpportunites() {
        List<Opportunite> opportunites = serviceOpportunite.afficher();
        opportuniteList = FXCollections.observableArrayList(opportunites);

        titrecol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptioncol.setCellValueFactory(new PropertyValueFactory<>("description"));
        lieucol.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        typecol.setCellValueFactory(new PropertyValueFactory<>("type"));
        annneeexpcol.setCellValueFactory(new PropertyValueFactory<>("exp_years"));

        opportunite_tabeview.setItems(opportuniteList);
    }

    private void afficherDetailsOpportunite(Opportunite opportunite) {
        // Créer une nouvelle fenêtre pour afficher les détails
        Stage detailsStage = new Stage();
        VBox detailsBox = new VBox(10);
        detailsBox.setStyle("-fx-padding: 20;");

        // Créer les labels pour chaque détail
        Label titreLabel = new Label("Titre: " + opportunite.getTitre());
        Label descriptionLabel = new Label("Description: " + opportunite.getDescription());
        Label lieuLabel = new Label("Lieu: " + opportunite.getLieu());
        Label typeLabel = new Label("Type: " + opportunite.getType());
        Label expLabel = new Label("Années d'expérience requises: " + opportunite.getExp_years());

        // Ajouter les labels à la VBox
        detailsBox.getChildren().addAll(
                titreLabel, descriptionLabel, lieuLabel, typeLabel, expLabel
        );

        // Créer la scène
        Scene scene = new Scene(detailsBox);
        detailsStage.setTitle("Détails de l'opportunité");
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    private void remplirFormulaireModification(Opportunite opportunite) {
        // Remplir les champs du formulaire avec les données de l'opportunité
        addopportunite_titre.setText(opportunite.getTitre());
        addopportunite_description.setText(opportunite.getDescription());
        addopportunite_lieu.setText(opportunite.getLieu());
        addopportunite_type.setValue(opportunite.getType().name().toLowerCase());
        addopportunite_anneeexp.setText(String.valueOf(opportunite.getExp_years()));
    }

    private void loadCandidatures() {
        List<Candidature> candidatures = serviceCandidature.afficher();
        candidatureList = FXCollections.observableArrayList(candidatures);

        tid.setCellValueFactory(new PropertyValueFactory<>("id"));
        tuser.setCellValueFactory(new PropertyValueFactory<>("userid"));

        // Configuration de la colonne CV avec un bouton Voir
        tcv.setCellFactory(param -> new TableCell<>() {
            private final Button voirButton = new Button("Voir");

            {
                voirButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                voirButton.setOnAction(event -> {
                    Candidature candidature = getTableView().getItems().get(getIndex());
                    if (candidature != null && candidature.getCv() != null) {
                        try {
                            File file = new File(candidature.getCv());
                            if (file.exists()) {
                                Image image = new Image(file.toURI().toString());
                                ImageView imageView = new ImageView(image);

                                // Ajuster la taille de l'image
                                imageView.setFitWidth(600);
                                imageView.setFitHeight(800);
                                imageView.setPreserveRatio(true);

                                // Créer une nouvelle fenêtre pour afficher l'image
                                Stage imageStage = new Stage();
                                VBox vbox = new VBox(imageView);
                                vbox.setStyle("-fx-background-color: white; -fx-padding: 10;");
                                Scene scene = new Scene(vbox);

                                imageStage.setTitle("CV - Candidature #" + candidature.getId());
                                imageStage.setScene(scene);
                                imageStage.show();
                            } else {
                                showErrorAlert("Erreur", "Le fichier CV n'existe pas: " + candidature.getCv());
                            }
                        } catch (Exception e) {
                            showErrorAlert("Erreur", "Impossible d'ouvrir l'image: " + e.getMessage());
                        }
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(voirButton);
                }
            }
        });

        texper.setCellValueFactory(new PropertyValueFactory<>("experience"));
        tannee.setCellValueFactory(new PropertyValueFactory<>("anneeXp"));
        ttype.setCellValueFactory(new PropertyValueFactory<>("type"));
        tetat.setCellValueFactory(new PropertyValueFactory<>("etat"));

        // Configuration de la colonne des actions
        tactions.setCellFactory(param -> new TableCell<>() {
            private final Button accepterBtn = new Button("Accepter");
            private final Button refuserBtn = new Button("Refuser");
            private final Button supprimerBtn = new Button("Supprimer");

            {
                // Style des boutons
                accepterBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
                refuserBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                supprimerBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");

                // Actions des boutons
                accepterBtn.setOnAction(event -> {
                    Candidature candidature = getTableView().getItems().get(getIndex());
                    updateCandidatureEtat(candidature, Candidature.Etat.ACCEPTEE);
                });

                refuserBtn.setOnAction(event -> {
                    Candidature candidature = getTableView().getItems().get(getIndex());
                    updateCandidatureEtat(candidature, Candidature.Etat.REFUSE);
                });

                supprimerBtn.setOnAction(event -> {
                    Candidature candidature = getTableView().getItems().get(getIndex());
                    supprimerCandidature(candidature);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5); // 5 est l'espacement entre les boutons
                    buttons.getChildren().addAll(accepterBtn, refuserBtn, supprimerBtn);
                    setGraphic(buttons);
                }
            }
        });

        candidature_tableview.setItems(candidatureList);
    }

    private void afficherImage(String imagePath) {
        try {
            File file = new File(imagePath);
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);

            // Ajuster la taille de l'image
            imageView.setFitWidth(600);
            imageView.setFitHeight(800);
            imageView.setPreserveRatio(true);

            // Créer une nouvelle fenêtre pour afficher l'image
            Stage imageStage = new Stage();
            VBox vbox = new VBox(imageView);
            vbox.setStyle("-fx-background-color: white; -fx-padding: 10;");
            Scene scene = new Scene(vbox);

            imageStage.setTitle("CV");
            imageStage.setScene(scene);
            imageStage.show();
        } catch (Exception e) {
            showErrorAlert("Erreur", "Impossible d'ouvrir l'image: " + e.getMessage());
        }
    }

    private void updateCandidatureEtat(Candidature candidature, Candidature.Etat nouvelEtat) {
        serviceCandidature.updateEtat(candidature.getId(), nouvelEtat);
        loadCandidatures(); // Recharger la table pour afficher les changements
    }

    @FXML
    void handleAdd(ActionEvent event) {
        String titre = addopportunite_titre.getText().trim();
        String description = addopportunite_description.getText().trim();
        String lieu = addopportunite_lieu.getText().trim();
        int localExp_years = 0;

        try {
            localExp_years = Integer.parseInt(addopportunite_anneeexp.getText().trim()); // Vérification de l'année d'expérience
        } catch (NumberFormatException e) {
            showErrorAlert("Erreur", "L'année d'expérience doit être un nombre entier.");
            return;
        }

        String typeStr = addopportunite_type.getValue();

        // Validation des entrées
        if (!validateInputs(titre, description, lieu, localExp_years, typeStr)) return;

        // Conversion du String en Enum Type
        Opportunite.Type type = convertToEnum(typeStr); // Utiliser la méthode convertToEnum

        // Créer l'objet Opportunite
        Opportunite opp = new Opportunite(titre, description, localExp_years, lieu, type);

        // Ajouter l'opportunité à la base de données
        serviceOpportunite.add(opp);
        showInfoAlert("Succès", "L'opportunité a été ajoutée avec succès.");
        loadOpportunites();  // Rafraîchir la liste des opportunités
    }

    private boolean validateInputs(String titre, String description, String lieu, int localExp_years, String typeStr) {
        // Vérifier si les champs sont vides
        if (titre.isEmpty() || description.isEmpty() || lieu.isEmpty() || typeStr == null || typeStr.isEmpty()) {
            showErrorAlert("Erreur", "Veuillez remplir tous les champs.");
            return false;
        }

        // Vérifier que le titre contient uniquement des lettres (pas de chiffres)
        if (!titre.matches("[a-zA-Z\\s]+")) {
            showErrorAlert("Erreur", "Le titre ne doit contenir que des lettres.");
            return false;
        }

        // Vérifier que le lieu est en Tunisie
        List<String> regionsTunisie = List.of("Ariana", "Ghazela", "Tunis", "Sousse", "Sfax", "Bizerte", "Kairouan", "Kasserine", "Nabeul", "Mahdia", "Tozeur", "Monastir", "Jendouba", "Beja", "Zaghouan", "Kebili", "Siliana", "Sidi Bouzid", "Medenine", "Gafsa", "Kef", "Manouba");

        // Vérifier que le lieu est une région tunisienne valide
        boolean validLieu = regionsTunisie.stream()
                .anyMatch(region -> lieu.toLowerCase().contains(region.toLowerCase()));

        if (!validLieu) {
            showErrorAlert("Erreur", "Le lieu doit être une région en Tunisie.");
            return false;
        }
        // Vérifier que la description ne dépasse pas 20 caractères
        if (description.length() > 20) {
            showErrorAlert("Erreur", "La description ne doit pas dépasser 20 caractères.");
            return false;
        }

        // Vérification de la validité des années d'expérience
        if (localExp_years < 10) {
            showErrorAlert("Erreur", "L'année d'expérience doit être au minimum dans les 10 dernières années.");
            return false;
        }

        // Vérification de la validité du type
        try {
            convertToEnum(typeStr);
        } catch (IllegalArgumentException e) {
            showErrorAlert("Erreur", "Le type sélectionné est invalide.");
            return false;
        }

        return true;
    }


    private Opportunite.Type convertToEnum(String typeStr) {
        switch (typeStr.toLowerCase()) {  // Assurez-vous que tout est en minuscules pour correspondre à la liste
            case "benevolat":
                return Opportunite.Type.BENEVOLAT;
            case "stage":
                return Opportunite.Type.STAGE;
            case "emploi":
                return Opportunite.Type.EMPLOI;
            default:
                throw new IllegalArgumentException("Type invalide");
        }
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Opportunite selectedOpportunite = opportunite_tabeview.getSelectionModel().getSelectedItem();
        if (selectedOpportunite == null) {
            showErrorAlert("Erreur", "Veuillez sélectionner une opportunité.");
            return;
        }

        serviceOpportunite.delete(selectedOpportunite); // Supprimer l'opportunité
        loadOpportunites();  // Rafraîchir la liste
        showInfoAlert("Succès", "L'opportunité a été supprimée.");
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        Opportunite selectedOpportunite = opportunite_tabeview.getSelectionModel().getSelectedItem();
        if (selectedOpportunite == null) {
            showErrorAlert("Erreur", "Veuillez sélectionner une opportunité.");
            return;
        }

        // Récupérer les valeurs des champs
        String titre = addopportunite_titre.getText().trim();
        String description = addopportunite_description.getText().trim();
        String lieu = addopportunite_lieu.getText().trim();
        String type = addopportunite_type.getValue(); // Enum sous forme de String
        String anneeExpText = addopportunite_anneeexp.getText().trim();

        // Vérification des entrées
        if (titre.isEmpty() || description.isEmpty() || lieu.isEmpty() || type == null || type.isEmpty() || anneeExpText.isEmpty()) {
            showErrorAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        // Conversion de l'année d'expérience en entier
        int anneeExp;
        try {
            anneeExp = Integer.parseInt(anneeExpText);
        } catch (NumberFormatException e) {
            showErrorAlert("Erreur", "L'année d'expérience doit être un nombre entier.");
            return;
        }

        // Mise à jour des propriétés de l'opportunité sélectionnée
        selectedOpportunite.setTitre(titre);
        selectedOpportunite.setDescription(description);
        selectedOpportunite.setLieu(lieu);

        try {
            // Conversion de la chaîne en type enum
            selectedOpportunite.setType(Opportunite.Type.valueOf(type.toUpperCase())); // Assurez-vous que le type est en majuscule
        } catch (IllegalArgumentException e) {
            showErrorAlert("Erreur", "Le type sélectionné est invalide.");
            return;
        }

        selectedOpportunite.setExp_years(anneeExp);

        // Appel au service pour mettre à jour l'opportunité dans la base de données
        serviceOpportunite.update(selectedOpportunite);  // Pas besoin de try-catch SQLException si non lancé
        loadOpportunites();  // Rafraîchir les opportunités affichées
        showInfoAlert("Succès", "L'opportunité a été mise à jour.");
    }
    public void switchForm(ActionEvent event) {

        if (event.getSource() == candidature_btn) {
            formcandidature.setVisible(true);
            formopportunite.setVisible(false);

            candidature_btn.setStyle("-fx-background-color:linear-gradient(to bottom right, #7d222d, #d95466);");
            opportunite_btn.setStyle("-fx-background-color:transparent");

            loadCandidatures();

        } else if (event.getSource() == opportunite_btn) {
            formcandidature.setVisible(false);
            formopportunite.setVisible(true);

            opportunite_btn.setStyle("-fx-background-color:linear-gradient(to bottom right,#7d222d, #d95466);");
            candidature_btn.setStyle("-fx-background-color:transparent");



        }
    }

    public void close() {
        System.exit(0);
    }

    public void minimize() {
        Stage stage = (Stage) formopportunite.getScene().getWindow();
        stage.setIconified(true);
    }

    public void initialize() {
        ObservableList<String> items = FXCollections.observableArrayList("benevolat", "stage", "emploi");
        addopportunite_type.setItems(items);  // Remplir la ComboBox
        loadOpportunites();  // Charger les opportunités à l'initialisation

        // Initialiser le formulaire de candidature comme invisible
        formcandidature.setVisible(false);
        formopportunite.setVisible(true);
    }

    private void supprimerCandidature(Candidature candidature) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette candidature ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                serviceCandidature.delete(candidature);
                loadCandidatures(); // Recharger la table pour afficher les changements
            }
        });
    }
}