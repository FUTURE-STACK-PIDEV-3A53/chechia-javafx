package com.mila.model;

/**
 * Énumération des nationalités pour les programmes d'échange
 */
public enum Nationalite {
    TUNISIENNE("Tunisien"),
    ETRANGER("Étranger");
    
    private final String libelle;
    
    Nationalite(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    /**
     * Convertit une chaîne de caractères en valeur d'énumération
     * @param text La chaîne à convertir
     * @return La valeur d'énumération correspondante ou TUNISIENNE par défaut
     */
    public static Nationalite fromString(String text) {
        for (Nationalite nationalite : Nationalite.values()) {
            if (nationalite.name().equalsIgnoreCase(text) || nationalite.getLibelle().equalsIgnoreCase(text)) {
                return nationalite;
            }
        }
        return TUNISIENNE; // Valeur par défaut
    }
    
    @Override
    public String toString() {
        return libelle;
    }
}