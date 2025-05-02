package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.dao.GameDAO;
import org.example.dao.RiddleDAO;
import org.example.model.Game;
import org.example.model.Riddle;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

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
    
    // Riddles components
    @FXML private TableView<Riddle> riddlesTable;
    @FXML private TableColumn<Riddle, String> questionColumn;
    @FXML private TableColumn<Riddle, String> answerColumn;
    @FXML private TextField riddleQuestionField;
    @FXML private TextField riddleAnswerField;
    @FXML private Button addRiddleButton;
    @FXML private Button removeRiddleButton;

    private GameDAO gameDAO;
    private RiddleDAO riddleDAO;
    private Game currentGame;
    private boolean isEditMode = false;
    private ObservableList<Riddle> riddles = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        gameDAO = GameDAO.getInstance();
        riddleDAO = RiddleDAO.getInstance();
        setupValidation();
        setupImagePreview();
        setupRiddlesTable();
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

    private void setupRiddlesTable() {
        questionColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        answerColumn.setCellValueFactory(new PropertyValueFactory<>("answer"));
        riddlesTable.setItems(riddles);
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
                
                // Load riddles
                List<Riddle> gameRiddles = riddleDAO.findByGameId(game.getId());
                riddles.setAll(gameRiddles);
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
    private void handleAddRiddle() {
        String question = riddleQuestionField.getText().trim();
        String answer = riddleAnswerField.getText().trim();

        if (question.isEmpty() || answer.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Both question and answer are required.");
            return;
        }

        Riddle riddle = new Riddle(question, answer);
        riddles.add(riddle);
        riddleQuestionField.clear();
        riddleAnswerField.clear();
        updateRiddlesTable();
    }

    @FXML
    private void handleRemoveRiddle() {
        Riddle selectedRiddle = riddlesTable.getSelectionModel().getSelectedItem();
        if (selectedRiddle != null) {
            riddles.remove(selectedRiddle);
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a riddle to remove.");
        }
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
                if (success) {
                    gameToSave.setId(savedGame.getId());
                }
            }

            if (success) {
                // Save riddles
                saveRiddles(gameToSave);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Game and riddles saved successfully.");
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

        if (riddles.isEmpty()) {
            errors.append("At least one riddle is required.\n");
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

    private void saveRiddles(Game game) {
        try {
            // Supprimer les anciennes énigmes si c'est une mise à jour
            if (game.getId() != null) {
                riddleDAO.deleteByGameId(game.getId());
            }

            // Sauvegarder les nouvelles énigmes
            for (Riddle riddle : riddles) {
                riddle.setGameId(game.getId());
                riddleDAO.save(riddle);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save riddles: " + e.getMessage());
            e.printStackTrace();
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
        riddles.clear();
        riddleQuestionField.clear();
        riddleAnswerField.clear();
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
        
        // Try to get the window from the cancel button first
        if (cancelButton != null && cancelButton.getScene() != null && cancelButton.getScene().getWindow() != null) {
            alert.initOwner(cancelButton.getScene().getWindow());
        }
        
        alert.showAndWait();
    }

    private void updateRiddlesTable() {
        riddlesTable.setItems(riddles);
    }
} 