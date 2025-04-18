package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.dao.GameDAO;
import org.example.model.Game;

import java.io.File;
import java.time.LocalDateTime;

public class GameFormController {

    @FXML
    private Label titleLabel;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField playersField;
    @FXML
    private TextField picturePathField;
    @FXML
    private Button browseButton;
    @FXML
    private TextField filePathField;
    @FXML
    private ImageView imagePreview;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private GameDAO gameDAO;
    private Game currentGame;
    private boolean isEditMode = false;

    public void initialize() {
        try {
            gameDAO = new GameDAO();
            setupValidation();
            setupImagePreview();
        } catch (Exception e) {
            System.err.println("Error initializing GameFormController: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize the game form.");
        }
    }

    private void setupValidation() {
        // Only allow numbers in the players field
        playersField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                playersField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void setupImagePreview() {
        picturePathField.textProperty().addListener((obs, oldVal, newVal) -> updateImagePreview(newVal));
    }

    public void setGame(Game game) {
        try {
            this.currentGame = game;
            if (game != null) {
                isEditMode = true;
                titleLabel.setText("Edit Game");
                nameField.setText(game.getName());
                descriptionArea.setText(game.getDescription());
                playersField.setText(String.valueOf(game.getNumber_of_players()));
                picturePathField.setText(game.getPicture());
                filePathField.setText(game.getFile_path());
                updateImagePreview(game.getPicture());
            } else {
                isEditMode = false;
                titleLabel.setText("Add New Game");
                clearForm();
            }
        } catch (Exception e) {
            System.err.println("Error setting game data: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load game data.");
        }
    }
    
    @FXML
    private void handleBrowsePicture(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Game Picture");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );
        
        Stage stage = (Stage) browseButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String path = selectedFile.getAbsolutePath();
            picturePathField.setText(path);
            updateImagePreview(path);
        }
    }
    
    private void updateImagePreview(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File file = new File(imagePath);
                if (file.exists() && file.isFile()) {
                    Image image = new Image(file.toURI().toString());
                    imagePreview.setImage(image);
                    return;
                }
            } catch (Exception e) {
                System.err.println("Error loading image preview: " + e.getMessage());
            }
        }
        // If we get here, either the path is empty or the file doesn't exist
        imagePreview.setImage(null);
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        try {
            Game gameToSave = isEditMode ? currentGame : new Game();
            updateGameFromForm(gameToSave);

            boolean success;
            if (isEditMode) {
                success = gameDAO.update(gameToSave);
            } else {
                Game savedGame = gameDAO.save(gameToSave);
                success = savedGame != null && savedGame.getId() != null;
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Game saved successfully.");
                closeForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save the game.");
            }
        } catch (Exception e) {
            System.err.println("Error saving game: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save the game: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (nameField.getText().trim().isEmpty()) {
            errors.append("Name is required.\n");
        }

        if (playersField.getText().trim().isEmpty()) {
            errors.append("Number of players is required.\n");
        } else {
            try {
                int players = Integer.parseInt(playersField.getText().trim());
                if (players <= 0) {
                    errors.append("Number of players must be greater than 0.\n");
                }
            } catch (NumberFormatException e) {
                errors.append("Number of players must be a valid number.\n");
            }
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", errors.toString());
            return false;
        }
        return true;
    }

    private void updateGameFromForm(Game game) {
        game.setName(nameField.getText().trim());
        game.setDescription(descriptionArea.getText().trim());
        game.setNumber_of_players(Integer.parseInt(playersField.getText().trim()));
        game.setPicture(picturePathField.getText().trim());
        game.setFile_path(filePathField.getText().trim());
        
        if (!isEditMode) {
            game.setCreated_at(LocalDateTime.now());
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeForm();
    }
    
    private void clearForm() {
        nameField.clear();
        descriptionArea.clear();
        playersField.clear();
        picturePathField.clear();
        filePathField.clear();
        imagePreview.setImage(null);
    }

    private void closeForm() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(cancelButton.getScene().getWindow());
        alert.showAndWait();
    }
} 