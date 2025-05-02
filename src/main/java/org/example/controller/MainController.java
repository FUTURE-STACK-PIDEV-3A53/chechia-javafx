package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import org.example.model.User;
import org.example.utils.SessionManager;

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
}