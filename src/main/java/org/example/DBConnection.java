package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/chachia?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(" Connexion réussie à la base de données !");
            return conn;
        } catch (SQLException e) {
            System.out.println(" Connexion échouée !");
            System.out.println("Message : " + e.getMessage());
            return null;
        }
    }
}