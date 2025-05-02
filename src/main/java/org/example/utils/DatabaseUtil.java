package org.example.utils;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseUtil {
    public static Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }
} 