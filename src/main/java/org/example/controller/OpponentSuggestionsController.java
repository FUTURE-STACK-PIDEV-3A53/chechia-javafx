package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;
import org.example.dao.PlayerDAO;
import org.example.model.Player;
import org.example.model.GameRoom;
import org.example.model.User;
import org.example.utils.PlayerMatcher;
import org.example.utils.SessionManager;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class OpponentSuggestionsController implements Initializable {
    @FXML
    private TableView<Player> suggestionsTable;
    @FXML
    private TableColumn<Player, String> usernameColumn;
    @FXML
    private TableColumn<Player, Integer> scoreColumn;
    @FXML
    private TableColumn<Player, String> matchQualityColumn;
    @FXML
    private TableColumn<Player, Void> actionColumn;

    private PlayerDAO playerDAO;
    private Player currentPlayer;
    private GameRoom selectedGameRoom;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playerDAO = PlayerDAO.getInstance();
        setupTableColumns();
    }

    public void setGameRoom(GameRoom gameRoom) {
        this.selectedGameRoom = gameRoom;
        if (SessionManager.isLoggedIn()) {
            User currentUser = SessionManager.getUser();
            this.currentPlayer = new Player();
            this.currentPlayer.setId(currentUser.getId());
            this.currentPlayer.setUsername(currentUser.getUsername());
            this.currentPlayer.setGameRoomId(gameRoom.getId());
            this.currentPlayer.setScore(0); // Initialiser le score à 0 par défaut
            
            // Récupérer le score du joueur actuel
            List<Player> players = playerDAO.getPlayersByGameRoom(gameRoom.getId());
            for (Player p : players) {
                if (p.getId().equals(currentUser.getId())) {
                    this.currentPlayer.setScore(p.getScore());
                    break;
                }
            }
        }
        loadSuggestions();
    }

    private void setupTableColumns() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        
        // Colonne pour la qualité du match
        matchQualityColumn.setCellValueFactory(cellData -> {
            Player suggestedPlayer = cellData.getValue();
            if (currentPlayer != null && suggestedPlayer != null) {
                return new SimpleStringProperty(
                    PlayerMatcher.getMatchQuality(currentPlayer, suggestedPlayer)
                );
            }
            return new SimpleStringProperty("");
        });

        // Colonne pour le bouton d'action
        actionColumn.setCellFactory(createButtonCellFactory());
    }

    private void loadSuggestions() {
        try {
            if (selectedGameRoom == null) {
                System.err.println("No game room selected");
                return;
            }

            List<Player> players = playerDAO.getPlayersByGameRoom(selectedGameRoom.getId());
            if (players == null || players.isEmpty()) {
                System.out.println("No players found for game room: " + selectedGameRoom.getId());
                suggestionsTable.setItems(FXCollections.observableArrayList());
                return;
            }

            // Remove current player if exists
            if (currentPlayer != null) {
                players.removeIf(p -> p.getId().equals(currentPlayer.getId()));
            }
            
            // Sort by score difference
            if (currentPlayer != null) {
                players.sort((p1, p2) -> {
                    int diff1 = Math.abs(p1.getScore() - currentPlayer.getScore());
                    int diff2 = Math.abs(p2.getScore() - currentPlayer.getScore());
                    return Integer.compare(diff1, diff2);
                });
            }
            
            // Take top 5 closest matches
            if (players.size() > 5) {
                players = players.subList(0, 5);
            }
            
            // Add a note for players with score 0
            for (Player p : players) {
                if (p.getScore() == 0) {
                    p.setUsername(p.getUsername() + " (N'a pas encore joué)");
                }
            }
            
            suggestionsTable.setItems(FXCollections.observableArrayList(players));
            System.out.println("Loaded " + players.size() + " suggestions");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des suggestions : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Callback<TableColumn<Player, Void>, TableCell<Player, Void>> createButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Player, Void> call(TableColumn<Player, Void> param) {
                return new TableCell<>() {
                    private final Button playButton = new Button("Jouer");

                    {
                        playButton.setOnAction(event -> {
                            Player player = getTableView().getItems().get(getIndex());
                            handlePlayWithOpponent(player);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : playButton);
                    }
                };
            }
        };
    }

    private void handlePlayWithOpponent(Player opponent) {
        // TODO: Implémenter la logique pour démarrer une partie contre l'adversaire
        System.out.println("Starting game with opponent: " + opponent.getUsername());
    }
} 