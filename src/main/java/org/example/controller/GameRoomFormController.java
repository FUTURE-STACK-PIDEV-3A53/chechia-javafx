package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.dao.GameDAO;
import org.example.dao.GameRoomDAO;
import org.example.model.Game;
import org.example.model.GameRoom;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class GameRoomFormController {

    @FXML
    private Label titleLabel;
    @FXML
    private ComboBox<Game> gameComboBox;
    @FXML
    private TextField locationField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField hourField;
    @FXML
    private TextField minuteField;
    @FXML
    private CheckBox botEnabledCheckBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private GameRoomDAO gameRoomDAO;
    private GameDAO gameDAO;
    private GameRoom currentGameRoom; // Holds the room being edited, or null if adding
    private boolean isEditMode = false;
    private ObservableList<Game> availableGames;

    public void initialize() {
        gameRoomDAO = GameRoomDAO.getInstance(); // Handle potential init errors
        gameDAO = new GameDAO();
        loadAvailableGames();
        setupGameComboBox();
        addNumericListeners();
    }

    private void loadAvailableGames() {
        try {
            List<Game> games = gameDAO.findAll();
            availableGames = FXCollections.observableArrayList(games);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load available games.");
            availableGames = FXCollections.observableArrayList(); // Initialize empty
        }
    }

    private void setupGameComboBox() {
        gameComboBox.setItems(availableGames);
        // How the Game object is displayed in the ComboBox dropdown
        gameComboBox.setConverter(new StringConverter<Game>() {
            @Override
            public String toString(Game game) {
                return game == null ? "" : game.getId() + " - " + game.getName();
            }

            @Override
            public Game fromString(String string) {
                // Not needed for selection only
                return null;
            }
        });
    }
    
    private void addNumericListeners() {
        // Allow only digits for hour and minute fields
        hourField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                hourField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        minuteField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                minuteField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    // Called by GameRoomListController
    public void setGameRoom(GameRoom gameRoom) {
        this.currentGameRoom = gameRoom;
        if (gameRoom != null) {
            isEditMode = true;
            titleLabel.setText("Edit Game Room");
            locationField.setText(gameRoom.getLocation());
            botEnabledCheckBox.setSelected(gameRoom.isBotEnabled());

            if (gameRoom.getDateTime() != null) {
                datePicker.setValue(gameRoom.getDateTime().toLocalDate());
                hourField.setText(String.format("%02d", gameRoom.getDateTime().getHour()));
                minuteField.setText(String.format("%02d", gameRoom.getDateTime().getMinute()));
            }
            
            // Select the correct game in the ComboBox
             if (gameRoom.getGameId() != null) {
                 availableGames.stream()
                     .filter(g -> g.getId().equals(gameRoom.getGameId()))
                     .findFirst()
                     .ifPresent(gameComboBox::setValue);
             } else {
                 gameComboBox.getSelectionModel().clearSelection();
             }

        } else {
            isEditMode = false;
            titleLabel.setText("Add New Game Room");
            clearForm();
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        // Validation
        if (locationField.getText().isEmpty() || datePicker.getValue() == null || hourField.getText().isEmpty() || minuteField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Location and Date/Time fields cannot be empty.");
            return;
        }
        
        Game selectedGame = gameComboBox.getSelectionModel().getSelectedItem();
        Long gameId = (selectedGame != null) ? selectedGame.getId() : null;

        LocalDateTime dateTime;
        try {
            int hour = Integer.parseInt(hourField.getText());
            int minute = Integer.parseInt(minuteField.getText());
            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                 throw new DateTimeParseException("Invalid hour or minute", "", 0);
            }
            LocalDate date = datePicker.getValue();
            LocalTime time = LocalTime.of(hour, minute);
            dateTime = LocalDateTime.of(date, time);
        } catch (NumberFormatException | DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid time (HH between 00-23, MM between 00-59).");
            return;
        }

        // Create or update GameRoom object
        GameRoom roomToSave;
        if (isEditMode) {
            roomToSave = currentGameRoom;
        } else {
            roomToSave = new GameRoom();
        }

        roomToSave.setGameId(gameId); // Set the ID
        roomToSave.setGame(selectedGame); // Set the object too, if needed elsewhere
        roomToSave.setLocation(locationField.getText());
        roomToSave.setDateTime(dateTime);
        roomToSave.setBotEnabled(botEnabledCheckBox.isSelected());

        boolean success;
        try {
            if (isEditMode) {
                success = gameRoomDAO.update(roomToSave);
            } else {
                GameRoom savedRoom = gameRoomDAO.save(roomToSave);
                success = savedRoom != null && savedRoom.getId() != null;
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Game Room saved successfully.");
                closeForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save the Game Room.");
            }
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while saving: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeForm();
    }

    private void clearForm() {
        gameComboBox.getSelectionModel().clearSelection();
        locationField.clear();
        datePicker.setValue(null);
        hourField.clear();
        minuteField.clear();
        botEnabledCheckBox.setSelected(false);
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