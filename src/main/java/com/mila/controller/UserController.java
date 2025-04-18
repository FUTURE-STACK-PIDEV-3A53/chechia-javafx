package com.mila.controller;

import com.mila.model.ProgrammeEchange;
import com.mila.service.ProgrammeEchangeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML private FlowPane programContainer;
    @FXML private TextField searchField;
    

    @FXML private ComboBox<String> filterType;
    @FXML private ComboBox<String> filterNationality;


        


    
    private ProgrammeEchangeService programmeService;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Vérifier les composants FXML
            if (programContainer == null) {
                throw new IllegalStateException("Le conteneur de programmes n'a pas été injecté correctement");
            }
            if (filterType == null || filterNationality == null) {
                throw new IllegalStateException("Les filtres n'ont pas été injectés correctement");
            }
            if (searchField == null) {
                throw new IllegalStateException("Le champ de recherche n'a pas été injecté correctement");
            }

            // Initialiser le service
            programmeService = new ProgrammeEchangeService();
            if (programmeService == null) {
                throw new IllegalStateException("Impossible d'initialiser le service des programmes");
            }
            
            // Initialiser les filtres
            filterType.getItems().addAll("Académique", "Culturel", "Professionnel");
            filterNationality.getItems().addAll("Tunisie", "France", "Allemagne", "Italie", "Espagne");
            
            // Charger les programmes
            refreshPrograms();
            
        } catch (IllegalStateException e) {
            String message = "Erreur d'initialisation des composants : " + e.getMessage();
            System.err.println(message);
            showError("Erreur", "Erreur d'initialisation", message);
        } catch (Exception e) {
            String message = "Erreur inattendue lors de l'initialisation : " + e.getMessage();
            System.err.println(message);
            e.printStackTrace();
            showError("Erreur", "Erreur d'initialisation", message);
        }
    }
    
    private void refreshPrograms() {
        try {
            programContainer.getChildren().clear();
            List<ProgrammeEchange> programmes = programmeService.getAll();
            
            for (ProgrammeEchange programme : programmes) {
                VBox card = createProgramCard(programme);
                programContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            showError("Erreur", "Erreur de chargement", "Impossible de charger les programmes");
        }
    }
    
    private VBox createProgramCard(ProgrammeEchange programme) {
        VBox card = new VBox(10);
        card.getStyleClass().addAll("program-card");
        card.setPrefWidth(300);
        card.setMaxWidth(300);
        card.setPadding(new Insets(15));
        
        Label title = new Label(programme.getNomProgramme());
        title.getStyleClass().add("title");
        title.setWrapText(true);
        
        VBox detailsBox = new VBox(5);
        detailsBox.getStyleClass().add("details");
        
        Label type = new Label("Type: " + programme.getType());
        Label nationalite = new Label("Nationalité: " + programme.getNationalite());
        Label duree = new Label("Durée: " + programme.getDuree() + " mois");
        Label description = new Label(programme.getDescription());
        description.setWrapText(true);
        
        detailsBox.getChildren().addAll(type, nationalite, duree, description);
        
        Button postulerBtn = new Button("Postuler");
        postulerBtn.getStyleClass().add("primary-button");
        postulerBtn.setMaxWidth(Double.MAX_VALUE);
        postulerBtn.setOnAction(e -> handlePostulation(programme));
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        card.getChildren().addAll(title, detailsBox, spacer, postulerBtn);
        return card;
    }
    
    private void handlePostulation(ProgrammeEchange programme) {
        if (programme == null) {
            showError("Erreur", "Programme invalide", "Impossible de postuler à un programme non valide.");
            return;
        }

        try {
            // Utiliser directement le chemin absolu pour éviter les problèmes de ressources
            String fxmlPath = "/fxml/ApplicationForm.fxml";
            System.out.println("Tentative de chargement du fichier: " + fxmlPath);
            
            // Charger le FXML avec un nouveau ClassLoader pour s'assurer qu'il est trouvé
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            
            // Vérifier si le fichier a été trouvé
            if (loader.getLocation() == null) {
                System.err.println("Fichier FXML non trouvé: " + fxmlPath);
                // Essayer avec l'autre formulaire
                fxmlPath = "/fxml/PostulationForm.fxml";
                System.out.println("Tentative avec le fichier alternatif: " + fxmlPath);
                loader = new FXMLLoader(getClass().getResource(fxmlPath));
                
                if (loader.getLocation() == null) {
                    showError("Erreur de ressource", 
                             "Fichier FXML manquant", 
                             "Le fichier du formulaire de postulation est introuvable. Veuillez contacter l'administrateur.");
                    return;
                }
            }

            // Charger le FXML
            Parent root;
            try {
                System.out.println("Chargement du fichier FXML: " + loader.getLocation());
                root = loader.load();
                System.out.println("Fichier FXML chargé avec succès");
            } catch (IOException e) {
                e.printStackTrace();
                showError("Erreur de chargement", 
                         "Erreur FXML", 
                         "Impossible de charger le formulaire de postulation : " + e.getMessage());
                return;
            }

            // Vérifier et configurer le contrôleur
            PostulationFormController controller = loader.getController();
            if (controller == null) {
                System.err.println("Contrôleur non trouvé après chargement du FXML");
                showError("Erreur d'initialisation", 
                         "Contrôleur manquant", 
                         "Impossible d'initialiser le formulaire de postulation. Veuillez contacter l'administrateur.");
                return;
            }
            System.out.println("Contrôleur récupéré avec succès");

            // Configurer le contrôleur
            controller.setProgramme(programme);
            System.out.println("Programme défini dans le contrôleur");

            // Créer et configurer la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Postuler au Programme : " + programme.getNomProgramme());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(programContainer.getScene().getWindow());

            // Configurer la scène
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Ajouter les styles
            URL cssUrl = getClass().getResource("/styles/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("Styles CSS ajoutés");
            } else {
                System.err.println("Fichier CSS non trouvé");
            }

            // Centrer la fenêtre
            stage.setOnShown(e -> {
                Stage owner = (Stage) programContainer.getScene().getWindow();
                stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2);
                stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);
                System.out.println("Fenêtre affichée et centrée");
            });

            // Afficher la fenêtre
            System.out.println("Affichage de la fenêtre de postulation");
            stage.showAndWait();
            System.out.println("Fenêtre de postulation fermée");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur système", 
                     "Erreur inattendue", 
                     "Une erreur est survenue lors de l'ouverture du formulaire : " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        String selectedType = filterType.getValue();
        String selectedNationality = filterNationality.getValue();
        
        try {
            programContainer.getChildren().clear();
            List<ProgrammeEchange> programmes = programmeService.getAll();
            
            for (ProgrammeEchange programme : programmes) {
                if (matchesSearch(programme, searchText, selectedType, selectedNationality)) {
                    VBox card = createProgramCard(programme);
                    programContainer.getChildren().add(card);
                }
            }
        } catch (SQLException e) {
            showError("Erreur", "Erreur de recherche", "Impossible d'effectuer la recherche");
        }
    }

    private boolean matchesSearch(ProgrammeEchange programme, String searchText, String type, String nationality) {
        boolean matchesSearch = programme.getNomProgramme().toLowerCase().contains(searchText);
        boolean matchesType = type == null || type.equals(programme.getType());
        boolean matchesNationality = nationality == null || nationality.equals(programme.getNationalite());
        
        return matchesSearch && matchesType && matchesNationality;
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}