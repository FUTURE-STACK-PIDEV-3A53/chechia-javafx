package chechia.tn.test;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import chechia.tn.entities.Opportunite;
import chechia.tn.service.ServiceOpportunite;
import chechia.tn.tools.MyDataBase;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

public class Main{
    public static void main(String[] args) {
        // Initialiser le ServiceOpportunite
        ServiceOpportunite os = new ServiceOpportunite();
        Opportunite o1 = new Opportunite("fullstack", "need a fullstack for total hour",12, "ariana", Opportunite.Type.STAGE);

        // Obtenir la connexion à la base de données
        Connection conn = MyDataBase.getInstance().getConnection(); // Pas de try-catch ici car SQLException est gérée dans MyDataBase

        if (conn != null) {
            System.out.println("✅ Connexion réussie !");
            // Supposons que `os.add(o1)` ne lance pas d'exception SQLException, donc pas besoin de try-catch pour cela
            os.add(o1); // Ajouter l'opportunité à la base de données (assurez-vous que cette méthode ne lance pas SQLException)
            System.out.println("✅ Opportunité ajoutée avec succès !");
        } else {
            System.out.println("❌ Échec de la connexion !");
            return; // Arrêter l'exécution si la connexion échoue
        }

        // Vérifier si on peut exécuter une requête SQL
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("SELECT 1");  // Exécute une requête de test
            System.out.println("✅ Requête SQL test réussie !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'exécution de la requête test : " + e.getMessage());
        }
    }
}
