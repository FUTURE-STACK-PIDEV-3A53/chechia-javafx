package com.mila.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import com.mila.service.TranslateService;

import java.io.IOException;
import java.net.URL;

public class WelcomeController {

    @FXML
    private Label welcomeTitleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private Text descriptionText; // Changed from TextFlow to Text to match fx:id

    @FXML
    private Button translateButton;

    @FXML
    private void handleAdminLogin(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            
            // Vérifier si la ressource existe
            URL resourceUrl = getClass().getResource("/fxml/AdminView.fxml");
            if (resourceUrl == null) {
                throw new IOException("Le fichier FXML de l'interface administrateur est introuvable");
            }
            
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            
            // Vérifier si le contrôleur a été chargé correctement
            if (loader.getController() == null) {
                throw new IOException("Le contrôleur de l'interface administrateur n'a pas pu être initialisé");
            }
            
            Scene scene = new Scene(root, 1000, 700);
            
            // Ajouter les styles
            URL cssUrl = getClass().getResource("/styles/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Fichier CSS non trouvé");
            }
            
            Stage stage = new Stage();
            stage.setTitle("Espace Administrateur - Chechia");
            stage.setScene(scene);
            stage.show();
            
            // Fermer la fenêtre d'accueil
            if (currentStage != null) {
                currentStage.close();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'interface administrateur: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors du chargement de l'interface administrateur", e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur inattendue", "Une erreur est survenue lors du chargement de l'interface administrateur: " + e.getMessage());
        }
    }

    @FXML
    private void handleUserAccess(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            
            // Vérifier si la ressource existe
            URL resourceUrl = getClass().getResource("/fxml/user.fxml");
            if (resourceUrl == null) {
                throw new IOException("Le fichier FXML de l'interface utilisateur est introuvable");
            }
            
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            
            // Vérifier si le contrôleur a été chargé correctement
            if (loader.getController() == null) {
                throw new IOException("Le contrôleur de l'interface utilisateur n'a pas pu être initialisé");
            }
            
            Scene scene = new Scene(root, 1000, 700);
            
            // Ajouter les styles
            URL cssUrl = getClass().getResource("/styles/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Fichier CSS non trouvé");
            }
            
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
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur inattendue", "Une erreur est survenue lors du chargement de l'interface utilisateur: " + e.getMessage());
        }
    }

    @FXML
    private void handleManagePrograms(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            
            URL resourceUrl = getClass().getResource("/fxml/MainView.fxml");
            if (resourceUrl == null) {
                throw new IOException("Le fichier FXML de gestion des programmes est introuvable");
            }
            
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1000, 700);
            URL cssUrl = getClass().getResource("/styles/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            
            Stage stage = new Stage();
            stage.setTitle("Gestion des Programmes - Chechia");
            stage.setScene(scene);
            stage.show();
            
            if (currentStage != null) {
                currentStage.close();
            }
        } catch (IOException e) {
            showError("Erreur lors du chargement de la gestion des programmes", e.getMessage());
        }
    }

    @FXML
    private void handleViewPostulations(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            
            URL resourceUrl = getClass().getResource("/fxml/MyPostulationsView.fxml");
            if (resourceUrl == null) {
                throw new IOException("Le fichier FXML des postulations est introuvable");
            }
            
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1000, 700);
            URL cssUrl = getClass().getResource("/styles/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            
            Stage stage = new Stage();
            stage.setTitle("Mes Postulations - Chechia");
            stage.setScene(scene);
            stage.show();
            
            if (currentStage != null) {
                currentStage.close();
            }
        } catch (IOException e) {
            showError("Erreur lors du chargement des postulations", e.getMessage());
        }
    }
    
    @FXML
    private void handleTranslate() {
        try {
            // Traduire le titre principal
            Translation titleTranslation = TranslateService.getInstance().translate(
                welcomeTitleLabel.getText(),
                Translate.TranslateOption.sourceLanguage("fr"),
                Translate.TranslateOption.targetLanguage("en")
            );
            welcomeTitleLabel.setText(titleTranslation.getTranslatedText());
    
            // Traduire le sous-titre
            Translation subtitleTranslation = TranslateService.getInstance().translate(
                subtitleLabel.getText(),
                Translate.TranslateOption.sourceLanguage("fr"),
                Translate.TranslateOption.targetLanguage("en")
            );
            subtitleLabel.setText(subtitleTranslation.getTranslatedText());
    
            // Traduire la description
            Translation descTranslation = TranslateService.getInstance().translate(
                descriptionText.getText(),
                Translate.TranslateOption.sourceLanguage("fr"),
                Translate.TranslateOption.targetLanguage("en")
            );
            descriptionText.setText(descTranslation.getTranslatedText());
    
            // Traduire tous les boutons de la scène
            Scene currentScene = translateButton.getScene();
            translateAllButtons(currentScene.getRoot());
    
            // Traduire le titre de la fenêtre
            Stage stage = (Stage) translateButton.getScene().getWindow();
            Translation stageTranslation = TranslateService.getInstance().translate(
                stage.getTitle(),
                Translate.TranslateOption.sourceLanguage("fr"),
                Translate.TranslateOption.targetLanguage("en")
            );
            stage.setTitle(stageTranslation.getTranslatedText());
    
        } catch (Exception e) {
            showTranslationError(e);
        }
    }

    private void translateAllButtons(Parent parent) {
        parent.lookupAll(".button").forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;
                try {
                    Translation btnTranslation = TranslateService.getInstance().translate(
                        button.getText(),
                        Translate.TranslateOption.sourceLanguage("fr"),
                        Translate.TranslateOption.targetLanguage("en")
                    );
                    button.setText(btnTranslation.getTranslatedText());
                } catch (Exception e) {
                    System.err.println("Erreur lors de la traduction du bouton: " + button.getText());
                }
            }
        });
    }

    private void showTranslationError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        try {
            Translation errorTitleTranslation = TranslateService.getInstance().translate(
                "Erreur de traduction",
                Translate.TranslateOption.sourceLanguage("fr"),
                Translate.TranslateOption.targetLanguage("en")
            );
            Translation errorHeaderTranslation = TranslateService.getInstance().translate(
                "Impossible de traduire le texte",
                Translate.TranslateOption.sourceLanguage("fr"),
                Translate.TranslateOption.targetLanguage("en")
            );
            alert.setTitle(errorTitleTranslation.getTranslatedText());
            alert.setHeaderText(errorHeaderTranslation.getTranslatedText());
            alert.setContentText(e.getMessage());
        } catch (Exception ex) {
            alert.setTitle("Translation Error");
            alert.setHeaderText("Unable to translate text");
            alert.setContentText(e.getMessage());
        }
        alert.showAndWait();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    private void handleTestChatbot() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Test du Chatbot");
        alert.setHeaderText("Chatbot de démonstration");
        alert.setContentText("Fonctionnalité de chatbot à intégrer ici.\n\nCette boîte de dialogue simule l'accès au chatbot.");
        alert.showAndWait();
    }
}