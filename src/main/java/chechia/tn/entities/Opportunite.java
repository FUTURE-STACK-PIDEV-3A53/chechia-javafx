package chechia.tn.entities;

import java.util.Date;

public class Opportunite {
    private String titre;
    private String description;
    private  int exp_years; // Utilisation de Date pour l'année d'expérience
    private String lieu;
    private Type type;

    // Enum pour "type"
    public enum Type {
        BENEVOLAT, STAGE, EMPLOI;

        @Override
        public String toString() {
            // Affiche la première lettre en majuscule et le reste en minuscule
            return name().charAt(0) + name().substring(1).toLowerCase();
        }

        public static Type fromLabel(String label) {
            for (Type t : values()) {
                if (t.toString().equalsIgnoreCase(label)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Type inconnu : " + label);
        }
    }
    // Constructeur complet
    public Opportunite(String titre, String description, int exp_years, String lieu, Type type) {
        this.titre = titre;
        this.description = description;
        this.exp_years = exp_years;
        this.lieu = lieu;
        this.type = type;
    }

    // Getters et Setters
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getExp_years() {
        return exp_years;
    }

    public void setExp_years(int exp_years) {
        this.exp_years = exp_years;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Opportunite{" +
                "titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", exp_years=" + exp_years +
                ", lieu='" + lieu + '\'' +
                ", type=" + type +
                '}';
    }
}
