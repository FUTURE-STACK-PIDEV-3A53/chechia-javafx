package chechia.tn.entities;

public class Candidature {
    private int id;
    private int userid;
    private String cv;
    private int anneeXp;
    private String experience;
    private Etat etat;
    private Type type;
    private Opportunite opportunite;

    public enum Etat {
        EN_ATTENTE, ACCEPTEE, REFUSE
    }

    public enum Type {
        EN_LIGNE, PRESENTIEL
    }

    // Constructeur sans opportunité
    public Candidature(int id, int userid, String cv, int anneeXp, String experience, Etat etat, Type type) {
        this.id = id;
        this.userid = userid;
        this.cv = cv;
        this.anneeXp = anneeXp;
        this.experience = experience;
        this.etat = etat;
        this.type = type;
    }

    // Constructeur avec opportunité
    public Candidature(int id, int userid, String cv, int anneeXp, String experience, Etat etat, Type type, Opportunite opportunite) {
        this(id, userid, cv, anneeXp, experience, etat, type);
        this.opportunite = opportunite;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserid() { return userid; }
    public void setUserid(int userid) { this.userid = userid; }

    public String getCv() { return cv; }
    public void setCv(String cv) { this.cv = cv; }

    public int getAnneeXp() { return anneeXp; }
    public void setAnneeXp(int anneeXp) { this.anneeXp = anneeXp; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public Etat getEtat() { return etat; }
    public void setEtat(Etat etat) { this.etat = etat; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public Opportunite getOpportunite() { return opportunite; }
    public void setOpportunite(Opportunite opportunite) { this.opportunite = opportunite; }

    @Override
    public String toString() {
        return "Candidature{" +
                "id=" + id +
                ", userid=" + userid +
                ", cv='" + cv + '\'' +
                ", anneeXp=" + anneeXp +
                ", experience='" + experience + '\'' +
                ", etat=" + etat +
                ", type=" + type +
                ", opportunite=" + (opportunite != null ? opportunite.getId() : "null") +
                '}';
    }
}

