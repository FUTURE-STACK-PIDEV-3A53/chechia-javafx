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
        String sql = "INSERT INTO reservation (event_id, userID, nb_personne, num_tel) VALUES (?, ?, ?, ?)";
        
        System.out.println("Exécution de la requête SQL: " + sql);
        System.out.println("Valeurs à insérer:");
        System.out.println("  event_id: " + reservation.getEvent_id());
        System.out.println("  userID: " + reservation.getUserID());
        System.out.println("  nb_personne: " + reservation.getNb_personne());
        System.out.println("  num_tel: " + reservation.getNum_tel());
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                logger.log(Level.SEVERE, "Échec de connexion à la base de données");
                System.out.println("ERREUR CRITIQUE: Impossible de se connecter à la base de données");
                return false;
            }
            
            System.out.println("Connexion à la base de données établie avec succès");
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, reservation.getEvent_id());
                stmt.setInt(2, reservation.getUserID());
                stmt.setInt(3, reservation.getNb_personne());
                stmt.setString(4, reservation.getNum_tel());
                
                System.out.println("Requête préparée: " + stmt.toString());
                
                int rowsInserted = stmt.executeUpdate();
                System.out.println("Résultat de l'exécution: " + rowsInserted + " lignes insérées");
                return rowsInserted > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'ajout d'une réservation", e);
            System.out.println("ERREUR SQL: " + e.getMessage());
            System.out.println("Code SQL State: " + e.getSQLState());
            System.out.println("Code erreur vendeur: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception non SQL lors de l'ajout d'une réservation", e);
            System.out.println("EXCEPTION NON SQL: " + e.getMessage());
            e.printStackTrace();
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
                    rs.getInt("reservation_id"),
                    rs.getInt("event_id"),
                    rs.getInt("userID"),
                    rs.getInt("nb_personne"),
                    rs.getString("num_tel")
                );
                liste.add(reservation);
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des réservations", e);
            e.printStackTrace();
        }
        return liste;
    }
    
    /**
     * Met à jour une réservation existante
     * @param reservation La réservation modifiée
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean modifierReservation(Reservation reservation) {
        // Requête SQL selon l'ordre indiqué par l'utilisateur
        String sql = "UPDATE reservation SET event_id = ?, num_tel = ?, nb_personne = ?, userID = ? WHERE reservation_id = ?";
        
        System.out.println("Exécution de la requête SQL: " + sql);
        System.out.println("Valeurs à mettre à jour:");
        System.out.println("  event_id: " + reservation.getEvent_id());
        System.out.println("  num_tel: " + reservation.getNum_tel());
        System.out.println("  nb_personne: " + reservation.getNb_personne());
        System.out.println("  userID: " + reservation.getUserID());
        System.out.println("  reservation_id: " + reservation.getReservation_id());
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Paramètres dans l'ordre spécifié
            stmt.setInt(1, reservation.getEvent_id());
            stmt.setString(2, reservation.getNum_tel());
            stmt.setInt(3, reservation.getNb_personne());
            stmt.setInt(4, reservation.getUserID());
            stmt.setInt(5, reservation.getReservation_id());
            
            System.out.println("Requête préparée: " + stmt.toString());
            
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Résultat de l'exécution: " + rowsUpdated + " lignes mises à jour");
            return rowsUpdated > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la modification de la réservation ID: " + reservation.getReservation_id(), e);
            System.out.println("ERREUR SQL: " + e.getMessage());
            System.out.println("Code SQL State: " + e.getSQLState());
            System.out.println("Code erreur vendeur: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception non SQL lors de la modification de la réservation", e);
            System.out.println("EXCEPTION NON SQL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Supprime une réservation par le nombre de personnes
     * @param nbPersonne Le nombre de personnes de la réservation à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerReservation(int nbPersonne) {
        String sql = "DELETE FROM reservation WHERE nb_personne = ?";
        
        System.out.println("Suppression des réservations avec " + nbPersonne + " personne(s)");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, nbPersonne);
            
            int rowsDeleted = stmt.executeUpdate();
            System.out.println(rowsDeleted + " réservation(s) supprimée(s)");
            return rowsDeleted > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la suppression des réservations avec " + nbPersonne + " personne(s)", e);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Supprime une réservation par son ID (méthode conservée pour compatibilité)
     * @param id L'ID de la réservation à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerReservationParId(int id) {
        String sql = "DELETE FROM reservation WHERE reservation_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la suppression de la réservation ID: " + id, e);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Trouve une réservation par son ID
     * @param id L'ID de la réservation à trouver
     * @return La réservation si trouvée, null sinon
     */
    public Reservation trouverReservationParId(int id) {
        String sql = "SELECT * FROM reservation WHERE reservation_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Reservation(
                        rs.getInt("reservation_id"),
                        rs.getInt("event_id"),
                        rs.getInt("userID"),
                        rs.getInt("nb_personne"),
                        rs.getString("num_tel")
                    );
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la recherche de la réservation ID: " + id, e);
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Met à jour toutes les réservations ayant un certain nombre de personnes
     * @param oldNbPersonne L'ancien nombre de personnes (critère de recherche)
     * @param reservation La réservation modifiée avec les nouvelles valeurs
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean modifierReservationParNbPersonne(int oldNbPersonne, Reservation reservation) {
        String sql = "UPDATE reservation SET event_id = ?, userID = ?, nb_personne = ?, num_tel = ? WHERE nb_personne = ?";
        
        System.out.println("Mise à jour des réservations avec " + oldNbPersonne + " personne(s)");
        System.out.println("Nouvelles valeurs: ");
        System.out.println("  event_id: " + reservation.getEvent_id());
        System.out.println("  userID: " + reservation.getUserID());
        System.out.println("  nb_personne: " + reservation.getNb_personne());
        System.out.println("  num_tel: " + reservation.getNum_tel());
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reservation.getEvent_id());
            stmt.setInt(2, reservation.getUserID());
            stmt.setInt(3, reservation.getNb_personne());
            stmt.setString(4, reservation.getNum_tel());
            stmt.setInt(5, oldNbPersonne);
            
            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated + " réservation(s) mise(s) à jour");
            return rowsUpdated > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la modification des réservations avec " + oldNbPersonne + " personne(s)", e);
            e.printStackTrace();
            return false;
        }
    }
} 