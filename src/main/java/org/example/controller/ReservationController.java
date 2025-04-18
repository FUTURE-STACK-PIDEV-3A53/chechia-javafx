package org.example.controller;

import java.util.List;

import org.example.model.Reservation;
import org.example.model.ReservationDAO;

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

    public boolean deleteReservation(int id) {
        return dao.supprimerReservation(id);
    }

    public Reservation getReservationById(int id) {
        return dao.trouverReservationParId(id);
    }
} 