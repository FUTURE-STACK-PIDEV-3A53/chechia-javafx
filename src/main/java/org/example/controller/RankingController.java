package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.dao.PlayerDAO;
import org.example.model.Player;
import org.example.model.GameRoom;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RankingController implements Initializable {
    @FXML
    private TableView<Player> rankingTable;
    @FXML
    private TableColumn<Player, String> usernameColumn;
    @FXML
    private TableColumn<Player, Integer> scoreColumn;

    private PlayerDAO playerDAO;
    private GameRoom selectedGameRoom;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playerDAO = PlayerDAO.getInstance();
        setupTableColumns();
    }

    private void setupTableColumns() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        
        // Ajouter un style pour les colonnes
        usernameColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        scoreColumn.setStyle("-fx-alignment: CENTER;");
    }

    public void setGameRoom(GameRoom gameRoom) {
        this.selectedGameRoom = gameRoom;
        loadRankings();
    }

    private void loadRankings() {
        try {
            if (selectedGameRoom == null) {
                System.err.println("No game room selected");
                return;
            }

            // Récupérer tous les joueurs avec leurs scores
            List<Player> players = playerDAO.getPlayersByGameRoom(selectedGameRoom.getId());
            
            // Trier les joueurs par score décroissant
            players.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));
            
            // Mettre à jour la table
            ObservableList<Player> observablePlayers = FXCollections.observableArrayList(players);
            rankingTable.setItems(observablePlayers);
            
            // Ajouter un style alterné aux lignes
            rankingTable.setStyle("-fx-background-color: white;");
            rankingTable.setRowFactory(tv -> {
                javafx.scene.control.TableRow<Player> row = new javafx.scene.control.TableRow<>();
                row.setStyle("-fx-background-color: transparent;");
                return row;
            });
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des classements : " + e.getMessage());
            e.printStackTrace();
        }
    }
} 