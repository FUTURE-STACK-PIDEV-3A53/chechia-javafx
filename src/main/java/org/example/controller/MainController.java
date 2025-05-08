package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.IOException;
import org.example.model.User;
import org.example.utils.SessionManager;
import javafx.scene.control.Alert;

public class MainController {
    @FXML
    private BorderPane contentArea;
    
    @FXML
    private Button manageGamesBtn;
    
    @FXML
    private Button manageRoomsBtn;

    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<Object> gameRoomTable;

    private User currentUser;

    @FXML
    public void initialize() {
        // Initialisation des composants
        if (welcomeLabel != null) {
            User currentUser = SessionManager.getUser();
            if (currentUser != null) {
                welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
            }
        }
        // Show game rooms by default
        showGameRoomList();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUI();
    }

    public void updateUI() {
        if (welcomeLabel != null) {
            User currentUser = SessionManager.getUser();
            if (currentUser != null) {
                welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
            }
        }
    }

    @FXML
    private void showGameList() {
        try {
            // Update button styles
            manageGamesBtn.getStyleClass().add("active");
            manageRoomsBtn.getStyleClass().remove("active");
            
            // Load the game list view
            Parent gameList = FXMLLoader.load(getClass().getResource("/gameList.fxml"));
            contentArea.setCenter(gameList);
        } catch (IOException e) {
            e.printStackTrace();
            // Show error alert
        }
    }

    @FXML
    private void showGameRoomList() {
        try {
            // Update button styles
            manageRoomsBtn.getStyleClass().add("active");
            manageGamesBtn.getStyleClass().remove("active");
            
            // Load the game room list view
            Parent gameRoomList = FXMLLoader.load(getClass().getResource("/org/example/gameRoomList.fxml"));
            contentArea.setCenter(gameRoomList);
        } catch (IOException e) {
            e.printStackTrace();
            // Show error alert
        }
    }

    @FXML
    private void showChatBot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/chatBot.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Assistant de Jeu");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'interface du chatbot.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}