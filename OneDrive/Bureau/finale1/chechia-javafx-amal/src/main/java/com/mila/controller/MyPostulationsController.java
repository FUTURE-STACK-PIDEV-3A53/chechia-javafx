package com.mila.controller;

import com.mila.model.Postulation;
import com.mila.service.PostulationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class MyPostulationsController implements Initializable {

    @FXML
    private VBox postulationsContainer;

    private PostulationService postulationService;

    public MyPostulationsController() {
        postulationService = new PostulationService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadPostulations();
    }

    private void loadPostulations() {
        try {
            List<Postulation> postulations = postulationService.getAll();
            postulationsContainer.getChildren().clear();

            for (Postulation postulation : postulations) {
                postulationsContainer.getChildren().add(createPostulationCard(postulation));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                    "Impossible de charger les postulations: " + e.getMessage());
        }
    }

    private VBox createPostulationCard(Postulation postulation) {
        VBox card = new VBox(10);
        card.getStyleClass().add("postulation-card");
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // En-tête avec le nom du programme
        Label programLabel = new Label(postulation.getProgrammeEchange().getNomProgramme());
        programLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Informations de la postulation
        Label nameLabel = new Label(postulation.getNom() + " " + postulation.getPrenom());
        Label ageLabel = new Label("Age: " + postulation.getAge());
        Label emailLabel = new Label("Email: " + postulation.getEmail());

        // Boutons d'action
        HBox actions = new HBox(10);
        actions.setStyle("-fx-padding: 10 0 0 0;");

        Button editButton = new Button("Modifier");
        editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        editButton.setOnAction(e -> handleEdit(postulation));

        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> handleDelete(postulation));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        actions.getChildren().addAll(spacer, editButton, deleteButton);

        card.getChildren().addAll(programLabel, nameLabel, ageLabel, emailLabel, actions);
        return card;
    }

    private void handleEdit(Postulation postulation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostulationForm.fxml"));
            Parent root = loader.load();

            PostulationFormController controller = loader.getController();
            controller.initForEdit(postulation);

            Stage stage = new Stage();
            stage.setTitle("Modifier la postulation");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(postulationsContainer.getScene().getWindow());
            stage.setScene(new Scene(root));
            
            stage.showAndWait();
            
            // Recharger les postulations après la modification
            loadPostulations();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                    "Impossible de charger le formulaire de modification: " + e.getMessage());
        }
    }

    private void handleDelete(Postulation postulation) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer la postulation");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette postulation ?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                if (postulationService.supprimer(postulation.getId())) {
                    loadPostulations(); // Recharger la liste après suppression
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de suppression", 
                            "Impossible de supprimer la postulation.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de suppression", 
                        "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 