package com.mila.model;

public class Postulation {
    private int id;
    private int prgEchangeId;
    private String nom;
    private String prenom;
    private int age;
    private String lettreMotivation;
    private byte[] cv;
    private String email;
    private ProgrammeEchange programmeEchange;

    public Postulation() {
    }

    public Postulation(int id, int prgEchangeId, String nom, String prenom, int age,
                     String lettreMotivation, byte[] cv, String email) {
        this.id = id;
        this.prgEchangeId = prgEchangeId;
        this.nom = nom;
        this.prenom = prenom;
        this.age = age;
        this.lettreMotivation = lettreMotivation;
        this.cv = cv;
        this.email = email;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrgEchangeId() {
        return prgEchangeId;
    }

    public void setPrgEchangeId(int prgEchangeId) {
        this.prgEchangeId = prgEchangeId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getLettreMotivation() {
        return lettreMotivation;
    }

    public void setLettreMotivation(String lettreMotivation) {
        this.lettreMotivation = lettreMotivation;
    }

    public byte[] getCv() {
        return cv;
    }

    public void setCv(byte[] cv) {
        this.cv = cv;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ProgrammeEchange getProgrammeEchange() {
        return programmeEchange;
    }

    public void setProgrammeEchange(ProgrammeEchange programmeEchange) {
        this.programmeEchange = programmeEchange;
        this.prgEchangeId = programmeEchange.getId();
    }

    @Override
    public String toString() {
        return prenom + " " + nom;
    }
}