package org.example.model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.example.DBConnection;

public class EventDAO {
    private static final Logger logger = Logger.getLogger(EventDAO.class.getName());

    public void ajouterEvent(Event event) {
        String sql = "INSERT INTO event (nom_event, localisation_event, date_event, type, montant, userID) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.out.println("❌ Connexion à la base de données échouée.");
                return;
            }

            stmt.setString(1, event.getNomEvent());
            stmt.setString(2, event.getLocalisation());
            stmt.setString(3, event.getDateEvent());
            stmt.setString(4, event.getType());
            stmt.setDouble(5, event.getMontant());
            stmt.setInt(6, event.getUserId());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Événement ajouté avec succès !");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding event", e);
        }
    }


    public List<Event> afficherEvents() {
        List<Event> liste = new ArrayList<>();
        String sql = "SELECT * FROM event";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Event e = new Event(
                        rs.getInt("id"),
                        rs.getString("nom_event"),
                        rs.getString("localisation_event"),
                        rs.getString("date_event"),
                        rs.getString("type"),
                        rs.getDouble("montant"),
                        rs.getInt("userID"));
                liste.add(e);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving events", e);
        }
        return liste;
    }

    public void supprimerEvent(int id) {
        String sql = "DELETE FROM event WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting event with ID: " + id, e);
        }
    }

    public void modifierEvent(Event event) {
        String sql = "UPDATE event SET nom_event=?, localisation_event=?, date_event=?, type=?, montant=?, userID=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getNomEvent());
            stmt.setString(2, event.getLocalisation());
            stmt.setString(3, event.getDateEvent());
            stmt.setString(4, event.getType());
            stmt.setDouble(5, event.getMontant());
            stmt.setInt(6, event.getUserId());
            stmt.setInt(7, event.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating event with ID: " + event.getId(), e);
        }
    }
}
