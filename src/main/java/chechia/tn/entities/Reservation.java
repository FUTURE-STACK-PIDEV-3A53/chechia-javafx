package chechia.tn.entities;

public class Reservation {
    private int reservation_id;
    private int event_id;
    private int userID;
    private int nb_personne;
    private String num_tel;

    // Constructor with reservation_id (for existing reservations)
    public Reservation(int reservation_id, int event_id, int userID, int nb_personne, String num_tel) {
        this.reservation_id = reservation_id;
        this.event_id = event_id;
        this.userID = userID;
        this.nb_personne = nb_personne;
        this.num_tel = num_tel;
    }

    // Constructor without reservation_id (for new reservations)
    public Reservation(int event_id, int userID, int nb_personne, String num_tel) {
        this.event_id = event_id;
        this.userID = userID;
        this.nb_personne = nb_personne;
        this.num_tel = num_tel;
    }

    // Getters and Setters
    public int getReservation_id() {
        return reservation_id;
    }

    public void setReservation_id(int reservation_id) {
        this.reservation_id = reservation_id;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getNb_personne() {
        return nb_personne;
    }

    public void setNb_personne(int nb_personne) {
        this.nb_personne = nb_personne;
    }

    public String getNum_tel() {
        return num_tel;
    }

    public void setNum_tel(String num_tel) {
        this.num_tel = num_tel;
    }

    // Pour la compatibilité avec le code existant
    public int getId() {
        return reservation_id;
    }

    public void setId(int id) {
        this.reservation_id = id;
    }

    public int getEventId() {
        return event_id;
    }

    public int getUserId() {
        return userID;
    }

    public int getNombrePersonnes() {
        return nb_personne;
    }

    // Méthode de compatibilité pour dateReservation (maintenant num_tel)
    public String getDateReservation() {
        return num_tel;
    }

    public void setDateReservation(String dateReservation) {
        this.num_tel = dateReservation;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservation_id=" + reservation_id +
                ", event_id=" + event_id +
                ", userID=" + userID +
                ", nb_personne=" + nb_personne +
                ", num_tel='" + num_tel + '\'' +
                '}';
    }
}