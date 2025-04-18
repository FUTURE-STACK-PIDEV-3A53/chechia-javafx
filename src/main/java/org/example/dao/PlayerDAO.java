package org.example.dao;

import org.example.db.DBConnection;
import org.example.model.GameRoom;
import org.example.model.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO {
    private static PlayerDAO instance;
    private GameRoomDAO gameRoomDAO;

    public static PlayerDAO getInstance() {
        if (instance == null) {
            instance = new PlayerDAO();
        }
        return instance;
    }

    private PlayerDAO() {
        // Le constructeur est privé pour le pattern Singleton
    }

    public void setGameRoomDAO(GameRoomDAO gameRoomDAO) {
        this.gameRoomDAO = gameRoomDAO;
    }
    
    public Player save(Player player) {
        String sql = "INSERT INTO player (gameroom_id, score, created) VALUES (?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setLong(1, player.getGameRoomId());
                pstmt.setInt(2, player.getScore());
                pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            player.setId(rs.getLong(1));
                        }
                    }
                }
                conn.commit();
                System.out.println("Joueur enregistré avec succès - ID: " + player.getId());
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback - " + ex.getMessage());
                }
            }
            System.err.println("Erreur lors de l'enregistrement du joueur - " + e.getMessage());
            throw new RuntimeException("Échec de l'enregistrement du joueur", e);
        }
        return player;
    }
    
    public Player findById(Long id) {
        String sql = "SELECT * FROM player WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPlayer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du joueur - " + e.getMessage());
        }
        return null;
    }
    
    public List<Player> findByGameRoomId(Long gameRoomId) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM player WHERE gameroom_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, gameRoomId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    players.add(mapResultSetToPlayer(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des joueurs - " + e.getMessage());
        }
        return players;
    }
    
    public boolean update(Player player) {
        String sql = "UPDATE player SET gameroom_id = ?, score = ? WHERE id = ?";
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            if (player.getGameRoomId() != null && player.getGameRoomId() > 0) {
                 pstmt.setLong(1, player.getGameRoomId());
             } else {
                 pstmt.setNull(1, Types.BIGINT);
             }
            pstmt.setInt(2, player.getScore());
            pstmt.setLong(3, player.getId());

            success = pstmt.executeUpdate() > 0;
            if (success) {
                conn.commit();
                System.out.println("Joueur mis à jour avec succès - ID: " + player.getId());
            } else {
                conn.rollback();
            }
             pstmt.close();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback - " + ex.getMessage());
                }
            }
            System.err.println("Erreur lors de la mise à jour du joueur - " + e.getMessage());
        }
         return success;
    }
    
    public boolean delete(Long id) {
        String sql = "DELETE FROM player WHERE id = ?";
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setLong(1, id);

            success = pstmt.executeUpdate() > 0;
            if (success) {
                conn.commit();
                System.out.println("Joueur supprimé avec succès - ID: " + id);
            } else {
                conn.rollback();
            }
            pstmt.close();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback - " + ex.getMessage());
                }
            }
            System.err.println("Erreur lors de la suppression du joueur - " + e.getMessage());
        }
         return success;
    }
    
    private Player mapResultSetToPlayer(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.setId(rs.getLong("id"));
        
        long gameRoomId = rs.getLong("gameroom_id");
        if (rs.wasNull()) {
            player.setGameRoomId(null);
        } else {
            player.setGameRoomId(gameRoomId);
            if (gameRoomDAO != null) { 
                GameRoom gameRoom = gameRoomDAO.findById(gameRoomId);
                if (gameRoom != null) {
                    player.setGameRoom(gameRoom);
                }
            } else {
                System.err.println("Avertissement: GameRoomDAO non initialisé dans PlayerDAO lors du chargement de la salle pour le joueur " + player.getId());
            }
        }
        player.setScore(rs.getInt("score"));
        player.setCreated(rs.getTimestamp("created").toLocalDateTime());

        return player;
    }
}