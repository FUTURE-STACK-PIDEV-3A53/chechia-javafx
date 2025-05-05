package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static final Logger logger = Logger.getLogger(DBConnection.class.getName());
    private static final String URL = "jdbc:mysql://localhost:3306/chachia?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            // Charger explicitement le pilote MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion réussie à la base de données !");
            return conn;
        } catch (SQLException e) {
            System.out.println("Connexion échouée à la base de données !");
            System.out.println("Message d'erreur SQL: " + e.getMessage());
            logger.log(Level.SEVERE, "Erreur SQL lors de la connexion à la base de données", e);
            return null;
        } catch (ClassNotFoundException e) {
            System.out.println("Pilote MySQL introuvable !");
            System.out.println("Message d'erreur: " + e.getMessage());
            logger.log(Level.SEVERE, "Pilote MySQL introuvable", e);
            return null;
        } catch (Exception e) {
            System.out.println("Erreur inattendue lors de la connexion !");
            System.out.println("Message d'erreur: " + e.getMessage());
            logger.log(Level.SEVERE, "Erreur inattendue lors de la connexion à la base de données", e);
            return null;
        }
    }
}