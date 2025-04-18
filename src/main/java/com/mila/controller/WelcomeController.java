package com.mila.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {

    @FXML
    private void handleAdminLogin(javafx.event.ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            
            Stage stage = new Stage();
            stage.setTitle("Espace Administrateur - Chechia");
            stage.setScene(scene);
            stage.show();
            
            // Fermer la fenêtre d'accueil
            if (currentStage != null) {
                currentStage.close();
            }
        } catch (IOException e) {
            showError("Erreur lors du chargement de l'interface administrateur", e.getMessage());
        }
    }

    @FXML
    private void handleUserAccess(javafx.event.ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            
            Stage stage = new Stage();
            stage.setTitle("Espace Utilisateur - Chechia");
            stage.setScene(scene);
            stage.show();
            
            // Fermer la fenêtre d'accueil
            if (currentStage != null) {
                currentStage.close();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'interface utilisateur: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors du chargement de l'interface utilisateur", e.getMessage());
        }
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}