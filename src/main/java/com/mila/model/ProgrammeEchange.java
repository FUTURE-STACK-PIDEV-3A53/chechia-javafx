package com.mila.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProgrammeEchange {
    private int id;
    private String nomProgramme;
    private TypeProgramme type;
    private Nationalite nationalite;
    private String description;
    private int duree;
    private Date datePrg;
    private List<Postulation> postulations;

    public ProgrammeEchange() {
        this.postulations = new ArrayList<>();
    }

    public ProgrammeEchange(int id, String nomProgramme, TypeProgramme type, Nationalite nationalite, 
                          String description, int duree, Date datePrg) {
        this.id = id;
        this.nomProgramme = nomProgramme;
        this.type = type;
        this.nationalite = nationalite;
        this.description = description;
        this.duree = duree;
        this.datePrg = datePrg;
        this.postulations = new ArrayList<>();
    }
    
    public ProgrammeEchange(int id, String nomProgramme, String type, String nationalite, 
                          String description, int duree, Date datePrg) {
        this.id = id;
        this.nomProgramme = nomProgramme;
        this.type = TypeProgramme.fromString(type);
        this.nationalite = Nationalite.fromString(nationalite);
        this.description = description;
        this.duree = duree;
        this.datePrg = datePrg;
        this.postulations = new ArrayList<>();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomProgramme() {
        return nomProgramme;
    }

    public void setNomProgramme(String nomProgramme) {
        this.nomProgramme = nomProgramme;
    }

    public TypeProgramme getType() {
        return type;
    }

    public void setType(TypeProgramme type) {
        this.type = type;
    }
    
    public void setType(String type) {
        this.type = TypeProgramme.fromString(type);
    }

    public Nationalite getNationalite() {
        return nationalite;
    }

    public void setNationalite(Nationalite nationalite) {
        this.nationalite = nationalite;
    }
    
    public void setNationalite(String nationalite) {
        this.nationalite = Nationalite.fromString(nationalite);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public Date getDatePrg() {
        return datePrg;
    }

    public void setDatePrg(Date datePrg) {
        this.datePrg = datePrg;
    }

    public List<Postulation> getPostulations() {
        return postulations;
    }

    public void setPostulations(List<Postulation> postulations) {
        this.postulations = postulations;
    }

    public void addPostulation(Postulation postulation) {
        this.postulations.add(postulation);
    }

    @Override
    public String toString() {
        return nomProgramme;
    }
}