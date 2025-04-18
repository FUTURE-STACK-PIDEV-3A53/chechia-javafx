package org.example.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Event {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nomEvent = new SimpleStringProperty();
    private final StringProperty localisation = new SimpleStringProperty();
    private final StringProperty dateEvent = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final DoubleProperty montant = new SimpleDoubleProperty();
    private final IntegerProperty userId = new SimpleIntegerProperty();

    // Constructeurs
    public Event() {}

    public Event(int id, String nomEvent, String localisation, String dateEvent,
                 String type, double montant, int userId) {
        this.id.set(id);
        this.nomEvent.set(nomEvent);
        this.localisation.set(localisation);
        this.dateEvent.set(dateEvent);
        this.type.set(type);
        this.montant.set(montant);
        this.userId.set(userId);
    }

    // Getters pour les propriétés
    public IntegerProperty idProperty() { return id; }
    public StringProperty nomEventProperty() { return nomEvent; }
    public StringProperty localisationProperty() { return localisation; }
    public StringProperty dateEventProperty() { return dateEvent; }
    public StringProperty typeProperty() { return type; }
    public DoubleProperty montantProperty() { return montant; }
    public IntegerProperty userIdProperty() { return userId; }

    // Getters traditionnels
    public int getId() { return id.get(); }
    public String getNomEvent() { return nomEvent.get(); }
    public String getLocalisation() { return localisation.get(); }
    public String getDateEvent() { return dateEvent.get(); }
    public String getType() { return type.get(); }
    public double getMontant() { return montant.get(); }
    public int getUserId() { return userId.get(); }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setNomEvent(String nomEvent) { this.nomEvent.set(nomEvent); }
    public void setLocalisation(String localisation) { this.localisation.set(localisation); }
    public void setDateEvent(String dateEvent) { this.dateEvent.set(dateEvent); }
    public void setType(String type) { this.type.set(type); }
    public void setMontant(double montant) { this.montant.set(montant); }
    public void setUserId(int userId) { this.userId.set(userId); }
}