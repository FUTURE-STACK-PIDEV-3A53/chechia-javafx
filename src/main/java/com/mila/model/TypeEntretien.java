package com.mila.model;

/**
 * Énumération des types d'entretiens pour les postulations
 */
public enum TypeEntretien {
    EN_LIGNE("En ligne"),
    PRESENTIEL("Présentiel"),
    TELEPHONE("Par téléphone");
    
    private final String libelle;
    
    TypeEntretien(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    @Override
    public String toString() {
        return this.libelle;
    }
    
    /**
     * Convertit une chaîne de caractères en valeur d'énumération
     * @param text La chaîne à convertir
     * @return La valeur d'énumération correspondante ou EN_LIGNE par défaut
     */
    public static TypeEntretien fromString(String text) {
        for (TypeEntretien type : TypeEntretien.values()) {
            if (type.name().equalsIgnoreCase(text) || type.getLibelle().equalsIgnoreCase(text)) {
                return type;
            }
        }
        return EN_LIGNE; // Valeur par défaut
    }
    

}