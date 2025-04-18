package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.dao.GameDAO;
import org.example.model.Game;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class GameListController {

    @FXML
    private TableView<Game> gameTableView;
    @FXML
    private TableColumn<Game, Long> idColumn;
    @FXML
    private TableColumn<Game, String> pictureColumn;
    @FXML
    private TableColumn<Game, String> nameColumn;
    @FXML
    private TableColumn<Game, String> descriptionColumn;
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

    private GameDAO gameDAO;
    private ObservableList<Game> gameList;
    private FilteredList<Game> filteredGames;
    private boolean sortAscending = true;

    public void initialize() {
        try {
            gameDAO = new GameDAO();
            setupTableColumns();
            setupSearch();
            loadGames();
            setAdminButtonsVisible(false);
        } catch (Exception e) {
            System.err.println("Error initializing GameListController: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize the game list.");
        }
    }

    private void setupTableColumns() {
        try {
            System.out.println("Setting up table columns...");
            
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            
            // Setup image column
            pictureColumn.setCellValueFactory(new PropertyValueFactory<>("picture"));
            pictureColumn.setCellFactory(column -> new TableCell<Game, String>() {
                private final ImageView imageView = new ImageView();
                private final StackPane pane = new StackPane();

                {
                    imageView.setFitHeight(50);
                    imageView.setFitWidth(70);
                    imageView.setPreserveRatio(true);
                    pane.setAlignment(Pos.CENTER);
                    pane.getChildren().add(imageView);
                }

                @Override
                protected void updateItem(String imagePath, boolean empty) {
                    super.updateItem(imagePath, empty);
                    if (empty || imagePath == null || imagePath.isEmpty()) {
                        setGraphic(null);
                    } else {
                        try {
                            File file = new File(imagePath);
                            if (file.exists() && file.isFile()) {
                                Image image = new Image(file.toURI().toString(), 70, 50, true, true);
                                imageView.setImage(image);
                                setGraphic(pane);
                            } else {
                                setGraphic(null);
                            }
                        } catch (Exception e) {
                            System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
                            setGraphic(null);
                        }
                    }
                }
            });
            
            System.out.println("Table columns setup completed");
        } catch (Exception e) {
            System.err.println("Error setting up table columns: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Setup Error", "Failed to setup table columns.");
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (filteredGames != null) {
                filteredGames.setPredicate(game -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    
                    String lowerCaseFilter = newValue.toLowerCase();
                    
                    if (game.getName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    if (game.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    if (String.valueOf(game.getId()).contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });
            }
        });
    }

    @FXML
    private void handleSortById() {
        if (gameList != null) {
            sortAscending = !sortAscending;
            FXCollections.sort(gameList, (g1, g2) -> {
                if (sortAscending) {
                    return g1.getId().compareTo(g2.getId());
                } else {
                    return g2.getId().compareTo(g1.getId());
                }
            });
            sortByIdButton.setText("Sort by ID " + (sortAscending ? "↑" : "↓"));
        }
    }

    private void loadGames() {
        try {
            System.out.println("Loading games...");
            List<Game> gamesFromDB = gameDAO.findAll();
            
            if (gamesFromDB == null) {
                System.err.println("Failed to retrieve games from database");
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve games from database");
                return;
            }
            
            System.out.println("Found " + gamesFromDB.size() + " games");
            gameList = FXCollections.observableArrayList(gamesFromDB);
            filteredGames = new FilteredList<>(gameList, p -> true);
            gameTableView.setItems(filteredGames);
            
            if (gamesFromDB.isEmpty()) {
                System.out.println("No games found in database");
            }
            
        } catch (Exception e) {
            System.err.println("Error loading games: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load games: " + e.getMessage());
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
    private void handleAddGame(ActionEvent event) {
        showGameForm(null);
    }

    @FXML
    private void handleEditGame(ActionEvent event) {
        Game selectedGame = gameTableView.getSelectionModel().getSelectedItem();
        if (selectedGame != null) {
            showGameForm(selectedGame);
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a game to edit.");
        }
    }

    @FXML
    private void handleDeleteGame(ActionEvent event) {
        Game selectedGame = gameTableView.getSelectionModel().getSelectedItem();
        if (selectedGame != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText("Delete Game: " + selectedGame.getName());
            confirmAlert.setContentText("Are you sure you want to delete this game? This action cannot be undone.");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    boolean deleted = gameDAO.delete(selectedGame.getId());
                    if (deleted) {
                        gameList.remove(selectedGame);
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Game deleted successfully.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete the game from the database.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while deleting the game: " + e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a game to delete.");
        }
    }

    private void showGameForm(Game game) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameForm.fxml"));
            Parent root = loader.load();

            GameFormController controller = loader.getController();
            controller.setGame(game);

            Stage stage = new Stage();
            stage.setTitle(game == null ? "Add New Game" : "Edit Game");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> loadGames());
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load the game form.");
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