package chechia.tn.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {

    private static MyDataBase instance;
    // Paramètres de connexion configurables
    private String URL = "jdbc:mysql://127.0.0.1:3306/chachia";
    private String USERNAME = "root";
    private String PASSWORD = "";
    private Connection cnx;

    // Constructeur privé pour empêcher l'instanciation directe
    private MyDataBase() {
        connect();
    }

    private void connect() {
        try {
            // Charger le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Trying to connect to database with:");
            System.out.println("URL: " + URL);
            System.out.println("Username: " + USERNAME);
            // Établir la connexion
            cnx = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to database successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("Please check if MySQL is running and the database 'chachia' exists.");
            e.printStackTrace();
        }
    }

    // Méthode pour obtenir l'instance de la classe (singleton)
    public static MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();
        }
        return instance;
    }

    // Méthode pour récupérer la connexion
    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status: " + e.getMessage());
            connect();
        }
        return cnx;
    }

    // Si nécessaire, vous pouvez aussi ajouter cette méthode
    public static Connection getConnection() {
        return getInstance().getCnx();
    }
}