package org.example.dao;

import org.example.db.DBConnection;
import org.example.model.Game;
import org.example.model.GameRoom;
import org.example.model.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameRoomDAO {
    private static GameRoomDAO instance;
    private final GameDAO gameDAO = new GameDAO();
    private PlayerDAO playerDAO;

    public static GameRoomDAO getInstance() {
        if (instance == null) {
            instance = new GameRoomDAO();
            PlayerDAO.getInstance().setGameRoomDAO(instance);
        }
        return instance;
    }

    private GameRoomDAO() {
        // Le constructeur est privé pour le pattern Singleton
    }
    
    public GameRoom save(GameRoom gameRoom) {
        String sql = "INSERT INTO gameroom (game_id, location, date_time, bot_enabled) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setLong(1, gameRoom.getGameId());
                pstmt.setString(2, gameRoom.getLocation());
                pstmt.setTimestamp(3, Timestamp.valueOf(gameRoom.getDateTime()));
                pstmt.setBoolean(4, gameRoom.isBotEnabled());
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            gameRoom.setId(rs.getLong(1));
                        }
                    }
                }
                conn.commit();
                System.out.println("Salle de jeu enregistrée avec succès - ID: " + gameRoom.getId());
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback - " + ex.getMessage());
                }
            }
            System.err.println("Erreur lors de l'enregistrement de la salle de jeu - " + e.getMessage());
            throw new RuntimeException("Échec de l'enregistrement de la salle de jeu", e);
        }
        return gameRoom;
    }
    
    public GameRoom findById(Long id) {
        String sql = "SELECT * FROM gameroom WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGameRoom(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la salle de jeu - " + e.getMessage());
        }
        return null;
    }
    
    public List<GameRoom> findAll() {
        List<GameRoom> gameRooms = new ArrayList<>();
        String sql = "SELECT gr.*, g.name as game_name FROM gameroom gr LEFT JOIN game g ON gr.game_id = g.id";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            System.out.println("Executing query: " + sql);
            
            while (rs.next()) {
                GameRoom room = new GameRoom();
                room.setId(rs.getLong("id"));
                
                // Handle game relationship
                Long gameId = rs.getLong("game_id");
                if (!rs.wasNull()) {
                    room.setGameId(gameId);
                    Game game = new Game();
                    game.setId(gameId);
                    game.setName(rs.getString("game_name"));
                    room.setGame(game);
                }
                
                room.setLocation(rs.getString("location"));
                Timestamp timestamp = rs.getTimestamp("date_time");
                if (timestamp != null) {
                    room.setDateTime(timestamp.toLocalDateTime());
                }
                room.setBotEnabled(rs.getBoolean("bot_enabled"));
                
                System.out.println("Mapped GameRoom: ID=" + room.getId() + 
                                ", Location=" + room.getLocation() + 
                                ", GameID=" + room.getGameId() +
                                ", Game=" + (room.getGame() != null ? room.getGame().getName() : "null"));
                
                gameRooms.add(room);
            }
            
            System.out.println("Total GameRooms found: " + gameRooms.size());
            
        } catch (SQLException e) {
            System.err.println("Error in findAll(): " + e.getMessage());
            e.printStackTrace();
        }
        return gameRooms;
    }
    
    public List<GameRoom> findByGameId(Long gameId) {
        List<GameRoom> gameRooms = new ArrayList<>();
        String sql = "SELECT * FROM gameroom WHERE game_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, gameId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    gameRooms.add(mapResultSetToGameRoom(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des salles de jeu - " + e.getMessage());
        }
        return gameRooms;
    }
    
    public boolean update(GameRoom gameRoom) {
        String sql = "UPDATE gameroom SET game_id = ?, location = ?, date_time = ?, bot_enabled = ? WHERE id = ?";
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            if (gameRoom.getGameId() != null && gameRoom.getGameId() > 0) {
                 pstmt.setLong(1, gameRoom.getGameId());
             } else {
                 pstmt.setNull(1, Types.BIGINT);
             }
            pstmt.setString(2, gameRoom.getLocation());
            pstmt.setTimestamp(3, Timestamp.valueOf(gameRoom.getDateTime()));
            pstmt.setBoolean(4, gameRoom.isBotEnabled());
            pstmt.setLong(5, gameRoom.getId());

            success = pstmt.executeUpdate() > 0;
            if (success) {
                conn.commit();
                 System.out.println("Salle de jeu mise à jour avec succès - ID: " + gameRoom.getId());
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
            System.err.println("Erreur lors de la mise à jour de la salle de jeu - " + e.getMessage());
        }
         return success;
    }
    
    public boolean delete(Long id) {
        String sql = "DELETE FROM gameroom WHERE id = ?";
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setLong(1, id);

            success = pstmt.executeUpdate() > 0;
            if (success) {
                conn.commit();
                System.out.println("Salle de jeu supprimée avec succès - ID: " + id);
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
            System.err.println("Erreur lors de la suppression de la salle de jeu - " + e.getMessage());
        }
         return success;
    }
    
    private GameRoom mapResultSetToGameRoom(ResultSet rs) throws SQLException {
        GameRoom gameRoom = new GameRoom();
        gameRoom.setId(rs.getLong("id"));
        
        Long gameId = rs.getLong("game_id");
        if (!rs.wasNull()) {
            gameRoom.setGameId(gameId);
            Game game = new Game();
            game.setId(gameId);
            game.setName(rs.getString("game_name"));
            gameRoom.setGame(game);
        }
        
        gameRoom.setLocation(rs.getString("location"));
        Timestamp timestamp = rs.getTimestamp("date_time");
        if (timestamp != null) {
            gameRoom.setDateTime(timestamp.toLocalDateTime());
        }
        gameRoom.setBotEnabled(rs.getBoolean("bot_enabled"));
        
        return gameRoom;
    }
}