package org.example.dao;

import org.example.db.DBConnection;
import org.example.model.UserScore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserScoreDAO {
    private static UserScoreDAO instance;

    public static UserScoreDAO getInstance() {
        if (instance == null) {
            instance = new UserScoreDAO();
        }
        return instance;
    }

    private UserScoreDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS user_scores (" +
                    "id INT NOT NULL AUTO_INCREMENT, " +
                    "user_id INT NOT NULL, " +
                    "game_id INT NOT NULL, " +
                    "score INT NOT NULL DEFAULT 0, " +
                    "last_played TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "PRIMARY KEY (id), " +
                    "FOREIGN KEY (user_id) REFERENCES user1(userID), " +
                    "FOREIGN KEY (game_id) REFERENCES game(id))";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating user_scores table: " + e.getMessage());
        }
    }

    public UserScore save(UserScore userScore) {
        String sql = "INSERT INTO user_scores (user_id, game_id, score) VALUES (?, ?, ?)";
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setLong(1, userScore.getUserId());
                pstmt.setLong(2, userScore.getGameId());
                pstmt.setInt(3, userScore.getScore());

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            userScore.setId(rs.getLong(1));
                        }
                    }
                }
                conn.commit();
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error during rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error saving user score: " + e.getMessage());
            throw new RuntimeException("Failed to save user score", e);
        }
        return userScore;
    }

    public List<UserScore> getTopScoresByGame(Long gameId, int limit) {
        List<UserScore> scores = new ArrayList<>();
        String sql = "SELECT us.*, u.username FROM user_scores us " +
                    "JOIN user1 u ON us.user_id = u.userID " +
                    "WHERE us.game_id = ? " +
                    "ORDER BY us.score DESC " +
                    "LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, gameId);
            pstmt.setInt(2, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    UserScore score = new UserScore();
                    score.setId(rs.getLong("id"));
                    score.setUserId(rs.getLong("user_id"));
                    score.setGameId(rs.getLong("game_id"));
                    score.setScore(rs.getInt("score"));
                    score.setLastPlayed(rs.getTimestamp("last_played").toLocalDateTime());
                    score.setUsername(rs.getString("username"));
                    scores.add(score);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting top scores: " + e.getMessage());
        }
        return scores;
    }

    public UserScore getHighestScore(Long userId, Long gameId) {
        String sql = "SELECT us.*, u.username FROM user_scores us " +
                    "JOIN user1 u ON us.user_id = u.userID " +
                    "WHERE us.user_id = ? AND us.game_id = ? " +
                    "ORDER BY us.score DESC " +
                    "LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            pstmt.setLong(2, gameId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UserScore score = new UserScore();
                    score.setId(rs.getLong("id"));
                    score.setUserId(rs.getLong("user_id"));
                    score.setGameId(rs.getLong("game_id"));
                    score.setScore(rs.getInt("score"));
                    score.setLastPlayed(rs.getTimestamp("last_played").toLocalDateTime());
                    score.setUsername(rs.getString("username"));
                    return score;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting highest score: " + e.getMessage());
        }
        return null;
    }
} 