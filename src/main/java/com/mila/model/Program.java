package com.mila.model;

public class Program {
    private String name;
    private String type;
    private String nationality;
    private int availablePlaces;
    private String description;

    public Program(String name, String type, String nationality, int availablePlaces, String description) {
        this.name = name;
        this.type = type;
        this.nationality = nationality;
        this.availablePlaces = availablePlaces;
        this.description = description;
    }

    // Getters
    public String getName() { return name; }
    public String getType() { return type; }
    public String getNationality() { return nationality; }
    public int getAvailablePlaces() { return availablePlaces; }
    public String getDescription() { return description; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public void setAvailablePlaces(int availablePlaces) { this.availablePlaces = availablePlaces; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s - %d places", name, type, nationality, availablePlaces);
    }
}