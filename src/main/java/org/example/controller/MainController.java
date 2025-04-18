package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class MainController {
    @FXML
    private BorderPane contentArea;
    
    @FXML
    private Button manageGamesBtn;
    
    @FXML
    private Button manageRoomsBtn;

    @FXML
    public void initialize() {
        // Show game rooms by default
        showGameRoomList();
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
            Parent gameRoomList = FXMLLoader.load(getClass().getResource("/gameRoomList.fxml"));
            contentArea.setCenter(gameRoomList);
        } catch (IOException e) {
            e.printStackTrace();
            // Show error alert
        }
    }
}