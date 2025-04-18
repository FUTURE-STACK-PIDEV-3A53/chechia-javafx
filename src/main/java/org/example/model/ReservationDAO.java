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

public class ReservationDAO {
    private static final Logger logger = Logger.getLogger(ReservationDAO.class.getName());

    /**
     * Ajoute une nouvelle réservation dans la base de données
     * @param reservation La réservation à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouterReservation(Reservation reservation) {
        String sql = "INSERT INTO reservation (event_id, user_id, nombre_personnes, date_reservation) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (conn == null) {
                logger.log(Level.SEVERE, "Échec de connexion à la base de données");
                return false;
            }
            
            stmt.setInt(1, reservation.getEventId());
            stmt.setInt(2, reservation.getUserId());
            stmt.setInt(3, reservation.getNombrePersonnes());
            stmt.setString(4, reservation.getDateReservation());
            
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'ajout d'une réservation", e);
            return false;
        }
    }
    
    /**
     * Récupère toutes les réservations de la base de données
     * @return Liste des réservations
     */
    public List<Reservation> afficherReservations() {
        List<Reservation> liste = new ArrayList<>();
        String sql = "SELECT * FROM reservation";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Reservation reservation = new Reservation(
                    rs.getInt("id"),
                    rs.getInt("event_id"),
                    rs.getInt("user_id"),
                    rs.getInt("nombre_personnes"),
                    rs.getString("date_reservation")
                );
                liste.add(reservation);
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des réservations", e);
        }
        return liste;
    }
    
    /**
     * Met à jour une réservation existante
     * @param reservation La réservation modifiée
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean modifierReservation(Reservation reservation) {
        String sql = "UPDATE reservation SET event_id = ?, user_id = ?, nombre_personnes = ?, date_reservation = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reservation.getEventId());
            stmt.setInt(2, reservation.getUserId());
            stmt.setInt(3, reservation.getNombrePersonnes());
            stmt.setString(4, reservation.getDateReservation());
            stmt.setInt(5, reservation.getId());
            
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la modification de la réservation ID: " + reservation.getId(), e);
            return false;
        }
    }
    
    /**
     * Supprime une réservation par son ID
     * @param id L'ID de la réservation à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerReservation(int id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la suppression de la réservation ID: " + id, e);
            return false;
        }
    }
    
    /**
     * Trouve une réservation par son ID
     * @param id L'ID de la réservation à trouver
     * @return La réservation si trouvée, null sinon
     */
    public Reservation trouverReservationParId(int id) {
        String sql = "SELECT * FROM reservation WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Reservation(
                        rs.getInt("id"),
                        rs.getInt("event_id"),
                        rs.getInt("user_id"),
                        rs.getInt("nombre_personnes"),
                        rs.getString("date_reservation")
                    );
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la recherche de la réservation ID: " + id, e);
        }
        
        return null;
    }
} 