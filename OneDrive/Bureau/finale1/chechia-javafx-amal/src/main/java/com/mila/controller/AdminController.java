package com.mila.controller;

import com.mila.service.TranslationService;
import com.mila.model.ProgrammeEchange;
import com.mila.model.TypeProgramme;
import com.mila.service.ProgrammeEchangeService;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent; // Ajout de l'importation manquante
import com.mila.utils.PDFGenerator;

public class AdminController implements Initializable {

    @FXML private FlowPane programContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterType;
    @FXML private ComboBox<String> filterNationality;
    @FXML private PieChart programTypeChart;
    @FXML private Label adminTitleLabel; // Assuming you add fx:id="adminTitleLabel" to the Label in FXML
    @FXML private Button backButton; // Assuming fx:id="backButton"
    @FXML private Button translateHeaderButton; // Assuming fx:id="translateHeaderButton"
    @FXML private Button addProgramHeaderButton; // Assuming fx:id="addProgramHeaderButton"
    @FXML private Button searchButton; // Assuming fx:id="searchButton"
    @FXML private Button addProgramButton2; // Assuming fx:id="addProgramButton2"

    private ProgrammeEchangeService programmeService;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        programmeService = new ProgrammeEchangeService();
        
        // Initialiser les filtres
        filterType.getItems().addAll("Académique", "Culturel", "Professionnel", "Stage", "Volontariat");
        filterNationality.getItems().addAll("Tunisien", "Étranger");
        
        // Charger les programmes
        refreshPrograms();
        
        // Initialiser le graphique en camembert
        updateProgramTypeChart();
    }
    
    private void refreshPrograms() {
        try {
            programContainer.getChildren().clear();
            List<ProgrammeEchange> programmes = programmeService.getAll();
            
            for (ProgrammeEchange programme : programmes) {
                VBox card = createProgramCard(programme);
                programContainer.getChildren().add(card);
            }
            
            // Mettre à jour le graphique en camembert
            updateProgramTypeChart();
        } catch (SQLException e) {
            showError("Erreur de chargement", "Impossible de charger les programmes");
        }
    }
    
    /**
     * Met à jour le graphique en camembert avec la répartition des types de programmes
     */
    private void updateProgramTypeChart() {
        try {
            List<ProgrammeEchange> programmes = programmeService.getAll();
            
            // Compter le nombre de programmes par type
            Map<String, Integer> typeCounts = new HashMap<>();
            for (TypeProgramme type : TypeProgramme.values()) {
                typeCounts.put(type.getLibelle(), 0);
            }
            
            for (ProgrammeEchange programme : programmes) {
                String typeLibelle = programme.getType().getLibelle();
                typeCounts.put(typeLibelle, typeCounts.getOrDefault(typeLibelle, 0) + 1);
            }
            
            // Calculer le nombre total de programmes
            int totalProgrammes = programmes.size();
            
            // Créer les données pour le graphique
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
                if (entry.getValue() > 0) {
                    // Calculer le pourcentage
                    double percentage = (entry.getValue() * 100.0) / totalProgrammes;
                    // Formater le pourcentage avec 1 décimale
                    String formattedPercentage = String.format("%.1f%%", percentage);
                    // Ajouter au graphique avec le nombre et le pourcentage
                    pieChartData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + " - " + formattedPercentage + ")", entry.getValue()));
                }
            }
            
            // Mettre à jour le graphique
            programTypeChart.setData(pieChartData);
            programTypeChart.setTitle("Répartition par Type de Programme");
            
            // Ajouter un style CSS pour améliorer la visibilité des étiquettes
            programTypeChart.setLabelsVisible(true);
            programTypeChart.setLabelLineLength(20);
            programTypeChart.setLegendVisible(true);
            programTypeChart.setPrefSize(150, 150);
        } catch (SQLException e) {
            showError("Erreur de chargement", "Impossible de charger les données pour le graphique");
        }
    }
    
    private VBox createProgramCard(ProgrammeEchange programme) {
        VBox card = new VBox(10);
        card.getStyleClass().add("program-card");
        
        Label title = new Label(programme.getNomProgramme());
        title.getStyleClass().add("card-title");
        
        Label type = new Label("Type: " + programme.getType());
        Label nationalite = new Label("Nationalité: " + programme.getNationalite());
        Label duree = new Label("Durée: " + programme.getDuree() + " mois");
        
        HBox buttons = new HBox(10);
        Button editBtn = new Button("Modifier");
        Button deleteBtn = new Button("Supprimer");
        Button translateBtn = new Button("Traduire en Anglais");
        
        editBtn.setOnAction(e -> handleEditProgram(programme));
        deleteBtn.setOnAction(e -> handleDeleteProgram(programme));
        translateBtn.setOnAction(e -> translateProgramCard(title, type, nationalite, duree)); // Renommé ici
        
        buttons.getChildren().addAll(editBtn, deleteBtn, translateBtn);
        
        card.getChildren().addAll(title, type, nationalite, duree, buttons);
        return card;
    }
    
    /**
     * Gère la traduction des éléments de l'interface utilisateur principale.
     * TODO: Implémenter la logique de traduction pour les éléments pertinents (titre, boutons, etc.)
     * TODO: Assurez-vous que les informations d'identification de l'API Google Translate sont correctement configurées.
     */
    @FXML
    public void handleTranslate() {
        TranslationService translationService = TranslationService.getInstance();
        
        // Toggle between French and English
        if (translateHeaderButton.getText().contains("Anglais")) {
            // Translate to English
            adminTitleLabel.setText(translationService.translateToEnglish(adminTitleLabel.getText()));
            translateHeaderButton.setText(translationService.translateToEnglish("Traduire en Français"));
            addProgramHeaderButton.setText(translationService.translateToEnglish("Ajouter un Programme"));
            searchField.setPromptText(translationService.translateToEnglish("Rechercher un programme..."));
            backButton.setText(translationService.translateToEnglish("Retour"));
        } else {
            // Translate back to French
            adminTitleLabel.setText(translationService.translateToFrench(adminTitleLabel.getText()));
            translateHeaderButton.setText("Traduire en Anglais");
            addProgramHeaderButton.setText("Ajouter un Programme");
            searchField.setPromptText("Rechercher un programme...");
            backButton.setText("Retour");
        }
    }
    
    // Suppression de la méthode createProgramCard dupliquée
    
    /**
     * Gère la traduction des détails d'une carte de programme spécifique.
     * TODO: Implémenter la logique de traduction en utilisant TranslateService.
     */
    private void translateProgramCard(Label titleLabel, Label typeLabel, Label nationalityLabel, Label durationLabel) {
        try {
            TranslationService translationService = TranslationService.getInstance();
            
            // Traduire le titre
            String translatedTitle = translationService.translateToEnglish(titleLabel.getText());
            titleLabel.setText(translatedTitle);
            
            // Traduire le type
            String translatedType = translationService.translateToEnglish(typeLabel.getText());
            typeLabel.setText(translatedType);
            
            // Traduire la nationalité
            String translatedNationality = translationService.translateToEnglish(nationalityLabel.getText());
            nationalityLabel.setText(translatedNationality);
            
            // Traduire la durée
            String translatedDuration = translationService.translateToEnglish(durationLabel.getText());
            durationLabel.setText(translatedDuration);
            
        } catch (Exception e) {
            showError("Erreur de traduction", "Impossible de traduire le texte de la carte. Vérifiez la configuration de l'API. Détails: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleAddProgram() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProgrammeForm.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Programme");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            refreshPrograms();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire d'ajout");
        }
    }
    
    private void handleEditProgram(ProgrammeEchange programme) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProgrammeForm.fxml"));
            Parent root = loader.load();
            
            ProgrammeFormController controller = loader.getController();
            controller.initForEdit(programme);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier le Programme");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            refreshPrograms();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire de modification");
        }
    }
    
    private void handleDeleteProgram(ProgrammeEchange programme) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le programme");
        alert.setContentText("Voulez-vous vraiment supprimer ce programme ?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                programmeService.supprimer(programme.getId());
                refreshPrograms();
            } catch (SQLException e) {
                showError("Erreur", "Impossible de supprimer le programme");
            }
        }
    }
    
    @FXML
    private void handleTranslate(ActionEvent event) {
        // Logique pour traduire le texte en anglais
        String currentText = adminTitleLabel.getText();
        String translatedText = translateToEnglish(currentText);
        adminTitleLabel.setText(translatedText);
    }
    
    private String translateToEnglish(String text) {
        // Implémentation de la traduction
        // Retourne le texte traduit
        return "Exchange Programs Management";
    }



    @FXML
    private void handleSearch() {
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
            showError("Erreur", "Impossible d'effectuer la recherche");
        }
    }
    
    private boolean matchesSearch(ProgrammeEchange programme, String searchText, String type, String nationality) {
        boolean matchesSearch = programme.getNomProgramme().toLowerCase().contains(searchText);
        boolean matchesType = type == null || type.equals(programme.getType());
        boolean matchesNationality = nationality == null || nationality.equals(programme.getNationalite());
        
        return matchesSearch && matchesType && matchesNationality;
    }
    
    @FXML
    private void handleBack() {
        try {
            // Charger la vue précédente (par exemple, WelcomeView.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WelcomeView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) programContainer.getScene().getWindow(); // Obtenir la scène actuelle
            stage.setScene(new Scene(root));
            stage.setTitle("Bienvenue"); // Mettre à jour le titre si nécessaire
            stage.show();
        } catch (IOException e) {
            showError("Erreur de navigation", "Impossible de revenir à l'écran précédent.");
            e.printStackTrace(); // Pour le débogage
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleExportPDF() {
        try {
            List<ProgrammeEchange> programmes = programmeService.getAll();
            String outputPath = "programmes_echange.pdf";
            PDFGenerator.generateProgramsPDF(programmes, outputPath);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export PDF");
            alert.setHeaderText(null);
            alert.setContentText("Le PDF a été généré avec succès : " + outputPath);
            alert.showAndWait();
        } catch (Exception e) {
            showError("Erreur d'export", "Impossible de générer le PDF : " + e.getMessage());
        }
    }
}