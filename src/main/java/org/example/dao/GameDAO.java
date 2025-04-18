package org.example.dao;

import org.example.db.DBConnection;
import org.example.model.Game;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {
    public Game save(Game game) {
        String sql = "INSERT INTO game (name, picture, description, number_of_players, file_path, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, game.getName());
                pstmt.setString(2, game.getPicture());
                pstmt.setString(3, game.getDescription());
                pstmt.setInt(4, game.getNumber_of_players());
                pstmt.setString(5, game.getFile_path());
                pstmt.setTimestamp(6, Timestamp.valueOf(game.getCreated_at()));
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            game.setId(rs.getLong(1));
                        }
                    }
                }
                conn.commit();
                System.out.println("Jeu enregistré avec succès - ID: " + game.getId());
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback - " + ex.getMessage());
                }
            }
            System.err.println("Erreur lors de l'enregistrement du jeu - " + e.getMessage());
            throw new RuntimeException("Échec de l'enregistrement du jeu", e);
        }
        return game;
    }
    
    public Game findById(Long id) {
        String sql = "SELECT * FROM game WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGame(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du jeu - " + e.getMessage());
        }
        return null;
    }
    
    public List<Game> findAll() {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT * FROM game";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                games.add(mapResultSetToGame(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des jeux - " + e.getMessage());
        }
        return games;
    }
    
    public boolean update(Game game) {
        String sql = "UPDATE game SET name = ?, picture = ?, description = ?, number_of_players = ?, file_path = ?, created_at = ? WHERE id = ?";
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, game.getName());
            pstmt.setString(2, game.getPicture());
            pstmt.setString(3, game.getDescription());
            pstmt.setInt(4, game.getNumber_of_players());
            pstmt.setString(5, game.getFile_path());
            pstmt.setTimestamp(6, Timestamp.valueOf(game.getCreated_at()));
            pstmt.setLong(7, game.getId());

            success = pstmt.executeUpdate() > 0;
            if (success) {
                conn.commit();
                System.out.println("Jeu mis à jour avec succès - ID: " + game.getId());
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
            System.err.println("Erreur lors de la mise à jour du jeu - " + e.getMessage());
        }
         return success;
    }
    
    public boolean delete(Long id) {
        String sql = "DELETE FROM game WHERE id = ?";
         Connection conn = null;
         boolean success = false;

        try {
             conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setLong(1, id);

             success = pstmt.executeUpdate() > 0;
             if (success) {
                 conn.commit();
                 System.out.println("Jeu supprimé avec succès - ID: " + id);
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
            System.err.println("Erreur lors de la suppression du jeu - " + e.getMessage());
        }
         return success;
    }
    
    private Game mapResultSetToGame(ResultSet rs) throws SQLException {
        Game game = new Game();
        game.setId(rs.getLong("id"));
        game.setName(rs.getString("name"));
        game.setPicture(rs.getString("picture"));
        game.setDescription(rs.getString("description"));
        game.setNumber_of_players(rs.getInt("number_of_players"));
        game.setFile_path(rs.getString("file_path"));
        game.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
        return game;
    }
}