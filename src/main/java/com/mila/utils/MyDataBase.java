package com.mila.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {

    private static MyDataBase instance;
    private final String URL = "jdbc:mysql://127.0.0.1:3306/chachia";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    private Connection cnx;

    // Constructeur privé pour empêcher l'instanciation directe
    private MyDataBase() {
        try {
            // Charger explicitement le pilote MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            cnx = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to database successfully!"); // Message de confirmation de la connexion
        } catch (SQLException e) {
            System.err.println("Erreur de connexion SQL: " + e.getMessage()); // Afficher l'erreur en cas d'échec
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Pilote MySQL introuvable: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Méthode pour obtenir l'instance de la classe (singleton)
    public static MyDataBase getInstance() {
        if (instance == null)
            instance = new MyDataBase(); // Créer une nouvelle instance si elle n'existe pas
        return instance;
    }

    // Méthode pour récupérer la connexion
    public Connection getCnx() {
        return cnx;
    }

    // Si nécessaire, vous pouvez aussi ajouter cette méthode
    public Connection getConnection() {
        return cnx;
    }
}