package com.mila.model;

public class Programme {
    private String nom;
    private String type;
    private String nationalite;
    private int placesDisponibles;
    
    public Programme(String nom, String type, String nationalite, int placesDisponibles) {
        this.nom = nom;
        this.type = type;
        this.nationalite = nationalite;
        this.placesDisponibles = placesDisponibles;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getNationalite() {
        return nationalite;
    }
    
    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }
    
    public int getPlacesDisponibles() {
        return placesDisponibles;
    }
    
    public void setPlacesDisponibles(int placesDisponibles) {
        this.placesDisponibles = placesDisponibles;
    }
}