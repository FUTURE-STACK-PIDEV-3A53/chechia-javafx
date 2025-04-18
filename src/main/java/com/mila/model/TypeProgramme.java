package com.mila.model;

/**
 * Énumération des types de programmes d'échange
 */
public enum TypeProgramme {
    ACADEMIQUE("Académique"),
    STAGE("Stage"),
    PROFESSIONNEL("Professionnel"),
    CULTUREL("Culturel"),
    VOLONTARIAT("Volontariat");
    
    private final String libelle;
    
    TypeProgramme(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    /**
     * Convertit une chaîne de caractères en valeur d'énumération
     * @param text La chaîne à convertir
     * @return La valeur d'énumération correspondante ou ACADEMIQUE par défaut
     */
    public static TypeProgramme fromString(String text) {
        for (TypeProgramme type : TypeProgramme.values()) {
            if (type.name().equalsIgnoreCase(text) || type.getLibelle().equalsIgnoreCase(text)) {
                return type;
            }
        }
        return ACADEMIQUE; // Valeur par défaut
    }
    
    @Override
    public String toString() {
        return libelle;
    }
}