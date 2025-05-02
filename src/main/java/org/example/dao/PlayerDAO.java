package org.example.dao;

import org.example.model.Player;
import org.example.utils.DatabaseConnection;
import org.example.utils.SessionManager;
import org.example.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO {
    private static PlayerDAO instance;
    private GameRoomDAO gameRoomDAO;

    private PlayerDAO() {}

    public static PlayerDAO getInstance() {
        if (instance == null) {
            instance = new PlayerDAO();
        }
        return instance;
    }

    public void setGameRoomDAO(GameRoomDAO gameRoomDAO) {
        this.gameRoomDAO = gameRoomDAO;
    }

    public void save(Player player) {
        if (!SessionManager.isLoggedIn()) {
            System.err.println("Erreur : Aucun utilisateur connecté");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get game_id from gameroom
            String gameQuery = "SELECT game_id FROM gameroom WHERE id = ?";
            try (PreparedStatement gameStmt = conn.prepareStatement(gameQuery)) {
                gameStmt.setLong(1, player.getGameRoomId());
                ResultSet rs = gameStmt.executeQuery();
                if (rs.next()) {
                    Long gameId = rs.getLong("game_id");
                    User currentUser = SessionManager.getUser();
                    
                    // Save in user_scores table
                    String userScoresQuery = "INSERT INTO user_scores (user_id, game_id, score) VALUES (?, ?, ?) " +
                                          "ON DUPLICATE KEY UPDATE score = ?";
                    try (PreparedStatement userScoresStmt = conn.prepareStatement(userScoresQuery)) {
                        userScoresStmt.setLong(1, currentUser.getId());
                        userScoresStmt.setLong(2, gameId);
                        userScoresStmt.setInt(3, player.getScore());
                        userScoresStmt.setInt(4, player.getScore());
                        userScoresStmt.executeUpdate();
                    }
                    
                    // Save in player table
                    String playerQuery = "INSERT INTO player (id, gameroom_id, score, created) VALUES (?, ?, ?, NOW()) " +
                                       "ON DUPLICATE KEY UPDATE score = ?";
                    try (PreparedStatement playerStmt = conn.prepareStatement(playerQuery)) {
                        playerStmt.setLong(1, currentUser.getId());
                        playerStmt.setLong(2, player.getGameRoomId());
                        playerStmt.setInt(3, player.getScore());
                        playerStmt.setInt(4, player.getScore());
                        playerStmt.executeUpdate();
                    }
                    
                    DatabaseConnection.commit();
                    System.out.println("Score enregistré avec succès - User: " + currentUser.getUsername() + 
                                     ", Game ID: " + gameId + 
                                     ", Score: " + player.getScore());
                } else {
                    System.err.println("Game ID non trouvé pour GameRoom ID: " + player.getGameRoomId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            DatabaseConnection.rollback();
            System.err.println("Erreur lors de l'enregistrement du score - " + e.getMessage());
        }
    }

    public List<Player> getPlayersByGameRoom(Long gameRoomId) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT u.userID, u.username, COALESCE(MAX(us.score), 0) as best_score, gr.id as gameroom_id " +
                    "FROM user1 u " +
                    "CROSS JOIN (SELECT id FROM gameroom WHERE id = ?) gr " +
                    "LEFT JOIN user_scores us ON us.user_id = u.userID " +
                    "LEFT JOIN gameroom g ON us.game_id = g.game_id AND g.id = gr.id " +
                    "GROUP BY u.userID, u.username, gr.id " +
                    "ORDER BY best_score DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, gameRoomId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Player player = new Player();
                player.setId(rs.getLong("userID"));
                player.setUsername(rs.getString("username"));
                player.setScore(rs.getInt("best_score"));
                player.setGameRoomId(rs.getLong("gameroom_id"));
                players.add(player);
            }
        } catch (SQLException e) {
            System.err.println("Error getting players by game room: " + e.getMessage());
            e.printStackTrace();
        }
        return players;
    }

    public void updateScore(Long playerId, Long gameRoomId, int newScore) {
        String query = "UPDATE player SET score = ? WHERE id = ? AND gameroom_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, newScore);
            stmt.setLong(2, playerId);
            stmt.setLong(3, gameRoomId);
            
            stmt.executeUpdate();
            DatabaseConnection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            DatabaseConnection.rollback();
        }
    }

    public Player getPlayer(Long playerId, Long gameRoomId) {
        String query = "SELECT p.*, u.username FROM player p " +
                      "JOIN user1 u ON p.id = u.userID " +
                      "WHERE p.id = ? AND p.gameroom_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, playerId);
            stmt.setLong(2, gameRoomId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Player player = new Player();
                player.setId(rs.getLong("id"));
                player.setGameRoomId(rs.getLong("gameroom_id"));
                player.setScore(rs.getInt("score"));
                player.setUsername(rs.getString("username"));
                if (gameRoomDAO != null) {
                    player.setGameRoomName(gameRoomDAO.getGameRoomName(gameRoomId));
                }
                return player;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void initializeTestData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Vérifier si la table est vide
            String countQuery = "SELECT COUNT(*) FROM player";
            try (PreparedStatement countStmt = conn.prepareStatement(countQuery)) {
                ResultSet rs = countStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    // Insérer des données de test
                    String insertQuery = "INSERT INTO player (id, gameroom_id, score, created) VALUES " +
                                      "(1, 3, 100, NOW()), " +
                                      "(2, 3, 150, NOW()), " +
                                      "(3, 3, 75, NOW()), " +
                                      "(1, 4, 200, NOW()), " +
                                      "(2, 4, 180, NOW()), " +
                                      "(3, 4, 160, NOW()), " +
                                      "(1, 9, 300, NOW()), " +
                                      "(2, 9, 250, NOW()), " +
                                      "(3, 9, 275, NOW()), " +
                                      "(1, 11, 400, NOW()), " +
                                      "(2, 11, 350, NOW()), " +
                                      "(3, 11, 375, NOW()) " +
                                      "ON DUPLICATE KEY UPDATE score = VALUES(score), created = VALUES(created)";
                    
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.executeUpdate();
                    }
                    DatabaseConnection.commit();
                    System.out.println("Données de test insérées avec succès dans la table player");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            DatabaseConnection.rollback();
            System.err.println("Erreur lors de l'initialisation des données de test - " + e.getMessage());
        }
    }
}