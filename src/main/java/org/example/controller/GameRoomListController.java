package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.dao.GameRoomDAO;
import org.example.model.Game;
import org.example.model.GameRoom;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class GameRoomListController {

    @FXML
    private TableView<GameRoom> gameRoomTableView;
    @FXML
    private TableColumn<GameRoom, Long> idColumn;
    @FXML
    private TableColumn<GameRoom, String> gameColumn;
    @FXML
    private TableColumn<GameRoom, String> locationColumn;
    @FXML
    private TableColumn<GameRoom, String> dateTimeColumn;
    @FXML
    private TableColumn<GameRoom, Boolean> botEnabledColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Button sortByIdButton;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button adminButton;

    private GameRoomDAO gameRoomDAO;
    private ObservableList<GameRoom> gameRoomList;
    private FilteredList<GameRoom> filteredRooms;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private boolean sortAscending = true;

    public void initialize() {
        try {
            gameRoomDAO = GameRoomDAO.getInstance();
            setupTableColumns();
            setupSearch();
            loadGameRooms();
            setAdminButtonsVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize data access objects.");
            return;
        }
    }

    private void setupTableColumns() {
        try {
            System.out.println("Setting up table columns...");
            
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
            botEnabledColumn.setCellValueFactory(new PropertyValueFactory<>("botEnabled"));

            gameColumn.setCellValueFactory(cellData -> {
                GameRoom room = cellData.getValue();
                if (room == null) {
                    return new SimpleStringProperty("");
                }
                Game game = room.getGame();
                return new SimpleStringProperty(game != null ? game.getName() : "No Game");
            });

            dateTimeColumn.setCellValueFactory(cellData -> {
                GameRoom room = cellData.getValue();
                if (room == null || room.getDateTime() == null) {
                    return new SimpleStringProperty("");
                }
                return new SimpleStringProperty(room.getDateTime().format(formatter));
            });
            
            System.out.println("Table columns setup completed");
            
        } catch (Exception e) {
            System.err.println("Error setting up table columns: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Setup Error", "Failed to setup table columns: " + e.getMessage());
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (filteredRooms != null) {
                filteredRooms.setPredicate(room -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    
                    String lowerCaseFilter = newValue.toLowerCase();
                    
                    if (room.getLocation().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    if (String.valueOf(room.getId()).contains(lowerCaseFilter)) {
                        return true;
                    }
                    Game game = room.getGame();
                    if (game != null && game.getName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });
            }
        });
    }

    @FXML
    private void handleSortById() {
        if (gameRoomList != null) {
            sortAscending = !sortAscending;
            FXCollections.sort(gameRoomList, (r1, r2) -> {
                if (sortAscending) {
                    return r1.getId().compareTo(r2.getId());
                } else {
                    return r2.getId().compareTo(r1.getId());
                }
            });
            sortByIdButton.setText("Sort by ID " + (sortAscending ? "↑" : "↓"));
        }
    }

    private void loadGameRooms() {
        try {
            System.out.println("Starting to load game rooms...");
            List<GameRoom> roomsFromDB = gameRoomDAO.findAll();
            
            if (roomsFromDB == null) {
                System.err.println("Failed to retrieve game rooms from database");
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve game rooms from database");
                return;
            }
            
            System.out.println("Found " + roomsFromDB.size() + " game rooms");
            gameRoomList = FXCollections.observableArrayList(roomsFromDB);
            filteredRooms = new FilteredList<>(gameRoomList, p -> true);
            gameRoomTableView.setItems(filteredRooms);
            
            if (roomsFromDB.isEmpty()) {
                System.out.println("No game rooms found in database");
            } else {
                for (GameRoom room : roomsFromDB) {
                    System.out.println("Room: ID=" + room.getId() + 
                                    ", Location=" + room.getLocation() + 
                                    ", Game=" + (room.getGame() != null ? room.getGame().getName() : "No Game"));
                }
            }
            
            System.out.println("Table items count: " + gameRoomTableView.getItems().size());
            
        } catch (Exception e) {
            System.err.println("Error loading game rooms: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load game rooms: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdminAccess() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Admin Access");
        dialog.setHeaderText("Enter Admin Code");
        dialog.setContentText("Code:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(code -> {
            if (code.equals("0000")) {
                setAdminButtonsVisible(true);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Admin access granted!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid admin code!");
            }
        });
    }

    private void setAdminButtonsVisible(boolean visible) {
        addButton.setVisible(visible);
        editButton.setVisible(visible);
        deleteButton.setVisible(visible);
    }

    @FXML
    private void handleAddGameRoom(ActionEvent event) {
        showGameRoomForm(null);
    }

    @FXML
    private void handleEditGameRoom(ActionEvent event) {
        GameRoom selectedGameRoom = gameRoomTableView.getSelectionModel().getSelectedItem();
        if (selectedGameRoom != null) {
            showGameRoomForm(selectedGameRoom);
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a game room to edit.");
        }
    }

    @FXML
    private void handleDeleteGameRoom(ActionEvent event) {
        GameRoom selectedGameRoom = gameRoomTableView.getSelectionModel().getSelectedItem();
        if (selectedGameRoom != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText("Delete Game Room: ID " + selectedGameRoom.getId() + " at " + selectedGameRoom.getLocation());
            confirmAlert.setContentText("Are you sure you want to delete this game room? This action cannot be undone.");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    boolean deleted = gameRoomDAO.delete(selectedGameRoom.getId());
                    if (deleted) {
                        gameRoomList.remove(selectedGameRoom);
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Game Room deleted successfully.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete the game room from the database.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while deleting the game room: " + e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a game room to delete.");
        }
    }

    private void showGameRoomForm(GameRoom gameRoom) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameRoomForm.fxml"));
            Parent root = loader.load();

            GameRoomFormController controller = loader.getController();
            controller.setGameRoom(gameRoom);

            Stage stage = new Stage();
            stage.setTitle(gameRoom == null ? "Add New Game Room" : "Edit Game Room");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> loadGameRooms());
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load the game room form.");
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