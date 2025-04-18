package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/chechia";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    private static Connection connection;
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                System.out.println("Attempting to connect to database...");
                System.out.println("URL: " + URL);
                System.out.println("User: " + USER);
                
                // Load the JDBC driver
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    System.out.println("MySQL JDBC Driver loaded successfully");
                } catch (ClassNotFoundException e) {
                    System.err.println("Failed to load MySQL JDBC driver: " + e.getMessage());
                    throw new SQLException("MySQL JDBC Driver not found", e);
                }
                
                // Establish connection
                try {
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("Database connection established");
                } catch (SQLException e) {
                    System.err.println("Failed to connect to database: " + e.getMessage());
                    throw e;
                }
                
                // Configure connection
                try {
                    connection.setAutoCommit(false);
                    System.out.println("Auto-commit disabled");
                    
                    if (connection.isValid(5)) {
                        System.out.println("Connection validated successfully");
                    } else {
                        throw new SQLException("Connection validation failed");
                    }
                } catch (SQLException e) {
                    System.err.println("Failed to configure connection: " + e.getMessage());
                    throw e;
                }
            } catch (Exception e) {
                System.err.println("Unexpected error during database connection: " + e.getMessage());
                e.printStackTrace();
                throw new SQLException("Failed to establish database connection", e);
            }
        } else {
            // Verify existing connection
            try {
                if (!connection.isValid(1)) {
                    System.out.println("Existing connection is invalid, creating new connection");
                    connection.close();
                    return getConnection();
                }
                System.out.println("Using existing valid connection");
            } catch (SQLException e) {
                System.err.println("Error checking existing connection: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    if (!connection.getAutoCommit()) {
                        try {
                            connection.commit();
                            System.out.println("Final commit executed");
                        } catch (SQLException e) {
                            System.err.println("Error during final commit: " + e.getMessage());
                            try {
                                connection.rollback();
                                System.out.println("Rolled back due to commit error");
                            } catch (SQLException re) {
                                System.err.println("Error during rollback: " + re.getMessage());
                            }
                        }
                    }
                    connection.close();
                    System.out.println("Database connection closed successfully");
                }
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
                e.printStackTrace();
            } finally {
                connection = null;
            }
        }
    }
}