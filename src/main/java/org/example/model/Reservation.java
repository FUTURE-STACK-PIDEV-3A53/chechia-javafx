package org.example.model;

public class Reservation {
    private int id;
    private int eventId;
    private int userId;
    private int nombrePersonnes;
    private String dateReservation;

    // Constructor with id (for existing reservations)
    public Reservation(int id, int eventId, int userId, int nombrePersonnes, String dateReservation) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.nombrePersonnes = nombrePersonnes;
        this.dateReservation = dateReservation;
    }

    // Constructor without id (for new reservations)
    public Reservation(int eventId, int userId, int nombrePersonnes, String dateReservation) {
        this.eventId = eventId;
        this.userId = userId;
        this.nombrePersonnes = nombrePersonnes;
        this.dateReservation = dateReservation;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getNombrePersonnes() {
        return nombrePersonnes;
    }

    public void setNombrePersonnes(int nombrePersonnes) {
        this.nombrePersonnes = nombrePersonnes;
    }

    public String getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(String dateReservation) {
        this.dateReservation = dateReservation;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", userId=" + userId +
                ", nombrePersonnes=" + nombrePersonnes +
                ", dateReservation='" + dateReservation + '\'' +
                '}';
    }
} 