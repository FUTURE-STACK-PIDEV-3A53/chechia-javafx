package chechia.tn.controllers.omar;

import java.util.List;

import chechia.tn.entities.Reservation;
import chechia.tn.service.omar.ReservationDAO;

public class ReservationController {
    private final ReservationDAO dao;

    public ReservationController() {
        dao = new ReservationDAO();
    }

    public boolean createReservation(Reservation reservation) {
        return dao.ajouterReservation(reservation);
    }

    public List<Reservation> getAllReservations() {
        return dao.afficherReservations();
    }

    public boolean updateReservation(Reservation reservation) {
        return dao.modifierReservation(reservation);
    }

    /**
     * Supprime une réservation par le nombre de personnes
     * @param nbPersonne Le nombre de personnes
     * @return true si la suppression a réussi, false sinon
     */
    public boolean deleteReservation(int nbPersonne) {
        return dao.supprimerReservation(nbPersonne);
    }

    /**
     * Supprime une réservation par son ID
     * @param id L'ID de la réservation
     * @return true si la suppression a réussi, false sinon
     */
    public boolean deleteReservationById(int id) {
        return dao.supprimerReservationParId(id);
    }

    public Reservation getReservationById(int id) {
        return dao.trouverReservationParId(id);
    }

    /**
     * Met à jour toutes les réservations ayant un certain nombre de personnes
     * @param oldNbPersonne L'ancien nombre de personnes
     * @param reservation La réservation avec les nouvelles valeurs
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updateReservationByNbPersonne(int oldNbPersonne, Reservation reservation) {
        return dao.modifierReservationParNbPersonne(oldNbPersonne, reservation);
    }
}