package chechia.tn.tools;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {

    private static MyDataBase instance;
    private final String URL = "jdbc:mysql://127.0.0.1:3306/chachia?serverTimezone=UTC";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    private Connection cnx;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;

    // Constructeur privé pour empêcher l'instanciation directe
    private MyDataBase() {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                cnx = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                if (cnx != null) {
                    System.out.println("✅ Connexion à la base de données établie avec succès");
                    return;
                }
            } catch (ClassNotFoundException e) {
                System.err.println("❌ Erreur: Driver MySQL non trouvé - " + e.getMessage());
                break; // Pas besoin de réessayer si le driver n'est pas trouvé
            } catch (SQLException e) {
                System.err.println("❌ Tentative " + (retries + 1) + "/" + MAX_RETRIES + 
                    " - Erreur de connexion à la base de données: " + e.getMessage());
                retries++;
                if (retries < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        if (cnx == null) {
            System.err.println("❌ Échec de la connexion après " + MAX_RETRIES + " tentatives");
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
