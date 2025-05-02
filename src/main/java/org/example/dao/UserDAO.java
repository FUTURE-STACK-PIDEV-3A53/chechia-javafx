package org.example.dao;

import org.example.model.User;
import org.example.utils.DatabaseConnection;
import org.example.utils.DatabaseUtil;
import org.example.utils.PasswordUtils;
import org.example.utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends BaseDAO {
    private static UserDAO instance;

    public static UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    private UserDAO() {
        // Ne pas créer la table car elle existe déjà
    }

    public boolean isEmailExist(String email) {
        String sql = "SELECT 1 FROM user1 WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            return true;
        }
    }

    public boolean isUsernameExist(String username) {
        String sql = "SELECT 1 FROM user1 WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            return true;
        }
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM user1 WHERE email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by email: " + e.getMessage());
        }
        return null;
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO user1 (username, email, password_hash) VALUES (?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getEmail());
                pstmt.setString(3, user.getPasswordHash());
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            user.setId(rs.getLong(1));
                            DatabaseConnection.commit();
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            DatabaseConnection.rollback();
        }
        return false;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE user1 SET username = ?, email = ?, password_hash = ? WHERE userID = ?";
        Connection conn = null;
        
        try {
            conn = getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getEmail());
                pstmt.setString(3, user.getPasswordHash());
                pstmt.setLong(4, user.getId());
                
                int result = pstmt.executeUpdate();
                DatabaseConnection.commit();
                return result > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            DatabaseConnection.rollback();
        }
        return false;
    }

    public boolean deleteUser(Long id) {
        String sql = "DELETE FROM user1 WHERE userID = ?";
        Connection conn = null;
        
        try {
            conn = getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, id);
                int result = pstmt.executeUpdate();
                DatabaseConnection.commit();
                return result > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            DatabaseConnection.rollback();
        }
        return false;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user1";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("userID"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        // On ne définit pas isAdmin car la colonne n'existe pas
        return user;
    }
}
