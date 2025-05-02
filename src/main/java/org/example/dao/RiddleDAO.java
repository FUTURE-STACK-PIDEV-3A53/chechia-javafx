package org.example.dao;

import org.example.model.Riddle;
import org.example.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RiddleDAO {
    private static RiddleDAO instance;

    private RiddleDAO() {
        try {
            createTableIfNotExists();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize RiddleDAO: " + e.getMessage(), e);
        }
    }

    public static RiddleDAO getInstance() {
        if (instance == null) {
            instance = new RiddleDAO();
        }
        return instance;
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS riddles (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "game_id BIGINT NOT NULL," +
                "question TEXT NOT NULL," +
                "answer TEXT NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (game_id) REFERENCES game(id)" +
                ")";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public Riddle save(Riddle riddle) {
        String sql = "INSERT INTO riddles (game_id, question, answer) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, riddle.getGameId());
            stmt.setString(2, riddle.getQuestion());
            stmt.setString(3, riddle.getAnswer());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating riddle failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    riddle.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating riddle failed, no ID obtained.");
                }
            }
            
            return riddle;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving riddle: " + e.getMessage(), e);
        }
    }

    public List<Riddle> findByGameId(Long gameId) {
        List<Riddle> riddles = new ArrayList<>();
        String sql = "SELECT * FROM riddles WHERE game_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, gameId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Riddle riddle = new Riddle();
                riddle.setId(rs.getLong("id"));
                riddle.setGameId(rs.getLong("game_id"));
                riddle.setQuestion(rs.getString("question"));
                riddle.setAnswer(rs.getString("answer"));
                Timestamp timestamp = rs.getTimestamp("created_at");
                if (timestamp != null) {
                    riddle.setCreatedAt(timestamp.toLocalDateTime());
                }
                riddles.add(riddle);
            }
        } catch (SQLException e) {
            System.err.println("Error finding riddles for game: " + e.getMessage());
            e.printStackTrace();
        }
        return riddles;
    }

    public void deleteByGameId(Long gameId) {
        String sql = "DELETE FROM riddles WHERE game_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, gameId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting riddles for game: " + e.getMessage(), e);
        }
    }

    public void update(Riddle riddle) {
        String sql = "UPDATE riddles SET question = ?, answer = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, riddle.getQuestion());
            stmt.setString(2, riddle.getAnswer());
            stmt.setLong(3, riddle.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating riddle failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating riddle: " + e.getMessage(), e);
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM riddles WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting riddle failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting riddle: " + e.getMessage(), e);
        }
    }
} 