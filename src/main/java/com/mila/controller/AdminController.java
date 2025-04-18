package com.mila.controller;

import com.mila.model.ProgrammeEchange;
import com.mila.service.ProgrammeEchangeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML private FlowPane programContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterType;
    @FXML private ComboBox<String> filterNationality;
    
    private ProgrammeEchangeService programmeService;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        programmeService = new ProgrammeEchangeService();
        
        // Initialiser les filtres
        filterType.getItems().addAll("Académique", "Culturel", "Professionnel");
        filterNationality.getItems().addAll("Tunisie", "France", "Allemagne", "Italie", "Espagne");
        
        // Charger les programmes
        refreshPrograms();
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
            showError("Erreur de chargement", "Impossible de charger les programmes");
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
        
        editBtn.setOnAction(e -> handleEditProgram(programme));
        deleteBtn.setOnAction(e -> handleDeleteProgram(programme));
        
        buttons.getChildren().addAll(editBtn, deleteBtn);
        
        card.getChildren().addAll(title, type, nationalite, duree, buttons);
        return card;
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
    
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}