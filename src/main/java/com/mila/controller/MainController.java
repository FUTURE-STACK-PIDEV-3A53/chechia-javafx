package com.mila.controller;

import com.mila.model.ProgrammeEchange;
import com.mila.model.Postulation;
import com.mila.service.ProgrammeEchangeService;
import com.mila.service.PostulationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private TableView<ProgrammeEchange> tableProgrammes;
    @FXML private TableColumn<ProgrammeEchange, String> colNomProgramme;
    @FXML private TableColumn<ProgrammeEchange, String> colType;
    @FXML private TableColumn<ProgrammeEchange, String> colNationalite;
    @FXML private TableColumn<ProgrammeEchange, Integer> colDuree;
    
    @FXML private TableView<Postulation> tablePostulations;
    @FXML private TableColumn<Postulation, String> colNom;
    @FXML private TableColumn<Postulation, String> colPrenom;
    @FXML private TableColumn<Postulation, Integer> colAge;
    @FXML private TableColumn<Postulation, String> colEmail;
    
    private ObservableList<ProgrammeEchange> programmesList = FXCollections.observableArrayList();
    private ObservableList<Postulation> postulationsList = FXCollections.observableArrayList();
    
    private ProgrammeEchangeService programmeService;
    private PostulationService postulationService;

    public MainController() {
        programmeService = new ProgrammeEchangeService();
        postulationService = new PostulationService();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser les colonnes des tableaux
        colNomProgramme.setCellValueFactory(new PropertyValueFactory<>("nomProgramme"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colNationalite.setCellValueFactory(new PropertyValueFactory<>("nationalite"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("duree"));
        
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        // Charger les données depuis la base de données
        refreshData();
        
        // Ajouter un listener pour afficher les postulations lorsqu'un programme est sélectionné
        tableProgrammes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                postulationsList.clear();
                postulationsList.addAll(newSelection.getPostulations());
            }
        });
        
        // Définir les données des tableaux
        tableProgrammes.setItems(programmesList);
        tablePostulations.setItems(postulationsList);
    }
    
    /**
     * Rafraîchit les données depuis la base de données
     */
    public void refreshData() {
        try {
            // Vider les listes
            programmesList.clear();
            postulationsList.clear();
            
            // Charger les programmes depuis la base de données
            programmesList.addAll(programmeService.getAll());
            
            // Sélectionner le premier programme s'il y en a
            if (!programmesList.isEmpty()) {
                tableProgrammes.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des données", e.getMessage());
        }
    }
    
    @FXML
    private void handleAddProgramme() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProgrammeForm.fxml"));
            Parent root = loader.load();
            
            ProgrammeFormController controller = loader.getController();
            controller.setMainController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Programme d'Échange");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tableProgrammes.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire", e.getMessage());
        }
    }
    
    @FXML
    private void handleEditProgramme() {
        ProgrammeEchange selectedProgramme = tableProgrammes.getSelectionModel().getSelectedItem();
        if (selectedProgramme != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProgrammeForm.fxml"));
                Parent root = loader.load();
                
                ProgrammeFormController controller = loader.getController();
                controller.setMainController(this);
                controller.initForEdit(selectedProgramme);
                
                Stage stage = new Stage();
                stage.setTitle("Modifier un Programme d'Échange");
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(tableProgrammes.getScene().getWindow());
                stage.setScene(new Scene(root));
                stage.showAndWait();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire", e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Aucun programme sélectionné", 
                    "Veuillez sélectionner un programme à modifier.");
        }
    }
    
    @FXML
    private void handleDeleteProgramme() {
        ProgrammeEchange selectedProgramme = tableProgrammes.getSelectionModel().getSelectedItem();
        if (selectedProgramme != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Supprimer le programme");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer le programme " + 
                    selectedProgramme.getNomProgramme() + " ? Toutes les postulations associées seront également supprimées.");
            
            if (confirmation.showAndWait().get() == ButtonType.OK) {
                try {
                    if (programmeService.supprimer(selectedProgramme.getId())) {
                        refreshData();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression", 
                                "La suppression a échoué. Veuillez réessayer.");
                    }
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de la suppression", e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Aucun programme sélectionné", 
                    "Veuillez sélectionner un programme à supprimer.");
        }
    }
    
    @FXML
    private void handleAddPostulation() {
        ProgrammeEchange selectedProgramme = tableProgrammes.getSelectionModel().getSelectedItem();
        if (selectedProgramme != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostulationForm.fxml"));
                Parent root = loader.load();
                
                PostulationFormController controller = loader.getController();
                controller.setMainController(this);
                
                Stage stage = new Stage();
                stage.setTitle("Ajouter une Postulation");
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(tablePostulations.getScene().getWindow());
                stage.setScene(new Scene(root));
                stage.showAndWait();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire", e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Aucun programme sélectionné", 
                    "Veuillez d'abord sélectionner un programme d'échange.");
        }
    }
    
    @FXML
    private void handleEditPostulation() {
        Postulation selectedPostulation = tablePostulations.getSelectionModel().getSelectedItem();
        if (selectedPostulation != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostulationForm.fxml"));
                Parent root = loader.load();
                
                PostulationFormController controller = loader.getController();
                controller.setMainController(this);
                controller.initForEdit(selectedPostulation);
                
                Stage stage = new Stage();
                stage.setTitle("Modifier une Postulation");
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(tablePostulations.getScene().getWindow());
                stage.setScene(new Scene(root));
                stage.showAndWait();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire", e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Aucune postulation sélectionnée", 
                    "Veuillez sélectionner une postulation à modifier.");
        }
    }
    
    @FXML
    private void handleDeletePostulation() {
        Postulation selectedPostulation = tablePostulations.getSelectionModel().getSelectedItem();
        if (selectedPostulation != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Supprimer la postulation");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer la postulation de " + 
                    selectedPostulation.getPrenom() + " " + selectedPostulation.getNom() + " ?");
            
            if (confirmation.showAndWait().get() == ButtonType.OK) {
                try {
                    if (postulationService.supprimer(selectedPostulation.getId())) {
                        refreshData();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression", 
                                "La suppression a échoué. Veuillez réessayer.");
                    }
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de la suppression", e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Aucune postulation sélectionnée", 
                    "Veuillez sélectionner une postulation à supprimer.");
        }
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