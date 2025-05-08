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
import org.example.dao.GameDAO;
import org.example.dao.GameRoomDAO;
import org.example.dao.PlayerDAO;
import org.example.model.Game;
import org.example.model.GameRoom;
import org.example.model.Player;
import org.example.model.User;
import org.example.utils.SessionManager;
import org.example.utils.DatabaseConnection;
import org.example.utils.PDFGenerator;
import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.awt.Desktop;

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
    @FXML
    private Button playButton;
    @FXML
    private Button chatBotButton;
    @FXML
    private Button downloadPDFButton;

    private GameRoomDAO gameRoomDAO;
    private GameDAO gameDAO;
    private PlayerDAO playerDAO;
    private ObservableList<GameRoom> gameRoomList;
    private FilteredList<GameRoom> filteredRooms;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private boolean sortAscending = true;

    @FXML
    public void initialize() {
        gameRoomDAO = GameRoomDAO.getInstance();
        gameDAO = GameDAO.getInstance();
        playerDAO = PlayerDAO.getInstance();
        
        // Initialiser les données de test
        playerDAO.initializeTestData();
        
        // Afficher la structure de la base de données
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables("chechia", null, "%", new String[]{"TABLE"});
            
            System.out.println("\nStructure de la base de données :");
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("\nTable: " + tableName);
                
                ResultSet columns = metaData.getColumns("chechia", null, tableName, "%");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    System.out.println("  - " + columnName + " (" + columnType + ")");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setupTableColumns();
        loadGameRooms();
        setupSearch();
        
        // Ensure play button is visible and properly configured
        if (playButton != null) {
            playButton.setVisible(true);
            playButton.setManaged(true);
            playButton.setDisable(true); // Initially disabled until a game room is selected
        }
        
        // Add selection listener to enable/disable play button
        gameRoomTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (playButton != null) {
                playButton.setDisable(newSelection == null || newSelection.getGame() == null);
            }
        });
        
        // Ensure admin button is visible
        if (adminButton != null) {
            adminButton.setVisible(true);
            adminButton.setManaged(true);
        }
        
        // Hide CRUD buttons initially
        setAdminButtonsVisible(false);
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
        User currentUser = SessionManager.getUser();
        if (currentUser != null && currentUser.isAdmin()) {
            setAdminButtonsVisible(true);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Admin access granted!");
        } else {
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
                    setAdminButtonsVisible(false);
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid admin code!");
                }
            });
        }
    }

    private void setAdminButtonsVisible(boolean visible) {
        if (addButton != null) {
            addButton.setVisible(visible);
            addButton.setManaged(visible);
        }
        if (editButton != null) {
            editButton.setVisible(visible);
            editButton.setManaged(visible);
        }
        if (deleteButton != null) {
            deleteButton.setVisible(visible);
            deleteButton.setManaged(visible);
        }
    }

    @FXML
    private void handleAddGameRoom(ActionEvent event) {
        showGameRoomForm(null);
    }

    @FXML
    private void handleEditRoom() {
        GameRoom selectedRoom = gameRoomTableView.getSelectionModel().getSelectedItem();
        if (selectedRoom != null) {
            showGameRoomForm(selectedRoom);
        }
    }

    @FXML
    private void handleDeleteRoom() {
        GameRoom selectedRoom = gameRoomTableView.getSelectionModel().getSelectedItem();
        if (selectedRoom != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Game Room");
            alert.setHeaderText("Delete Game Room");
            alert.setContentText("Are you sure you want to delete this game room?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    gameRoomDAO.delete(selectedRoom.getId());
                    loadGameRooms();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete game room: " + e.getMessage());
                }
            }
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

    @FXML
    private void handlePlayGame() {
        GameRoom selectedRoom = gameRoomTableView.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a game room to play.");
            return;
        }

        Game game = selectedRoom.getGame();
        if (game == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No game associated with this room.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/riddleGame.fxml"));
            Parent root = loader.load();
            
            RiddleGameController controller = loader.getController();
            
            // Créer un nouveau joueur pour cette partie
            Player player = new Player();
            if (SessionManager.isLoggedIn()) {
                User currentUser = SessionManager.getUser();
                player.setId(currentUser.getId());
                player.setUsername(currentUser.getUsername());
                player.setScore(0);
                player.setGameRoomId(selectedRoom.getId());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "You must be logged in to play.");
                return;
            }
            
            // Initialiser le jeu et le joueur
            controller.setGameAndPlayer(game, player);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Playing " + game.getName());
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load the game.");
        }
    }

    @FXML
    private void handleShowRankings() {
        GameRoom selectedRoom = gameRoomTableView.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a game room to view rankings.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/rankingView.fxml"));
            Parent root = loader.load();

            RankingController controller = loader.getController();
            controller.setGameRoom(selectedRoom);

            Stage stage = new Stage();
            stage.setTitle("Game Rankings - " + selectedRoom.getGame().getName());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load the rankings view.");
        }
    }

    @FXML
    private void handleShowSuggestions() {
        GameRoom selectedRoom = gameRoomTableView.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a game room to view opponent suggestions.");
            return;
        }

        try {
            if (!SessionManager.isLoggedIn()) {
                showAlert(Alert.AlertType.WARNING, "Non connecté", "Vous devez être connecté pour voir les suggestions d'adversaires.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/opponentSuggestions.fxml"));
            Parent root = loader.load();

            OpponentSuggestionsController controller = loader.getController();
            controller.setGameRoom(selectedRoom);

            Stage stage = new Stage();
            stage.setTitle("Suggestions d'adversaires - " + selectedRoom.getGame().getName());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load the suggestions view.");
        }
    }

    @FXML
    private void handleChatBot() {
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

    @FXML
    private void handleDownloadPDF() {
        GameRoom selectedRoom = gameRoomTableView.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune salle sélectionnée");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une salle de jeu pour télécharger le classement.");
            alert.showAndWait();
            return;
        }

        try {
            List<Player> players = playerDAO.getPlayersByGameRoom(selectedRoom.getId());
            String filePath = PDFGenerator.generateRankingPDF(selectedRoom, players);
            
            // Ouvrir le PDF automatiquement
            File pdfFile = new File(filePath);
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(pdfFile);
            }
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Téléchargement réussi");
            alert.setHeaderText(null);
            alert.setContentText("Le classement a été téléchargé et ouvert avec succès.");
            alert.showAndWait();
        } catch (IOException | DocumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de la génération ou de l'ouverture du PDF : " + e.getMessage());
            alert.showAndWait();
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