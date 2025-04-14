package chechia.tn.tools;


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
            cnx = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected ....."); // Message de confirmation de la connexion
        } catch (SQLException e) {
            System.out.println(e.getMessage()); // Afficher l'erreur en cas d'échec
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
