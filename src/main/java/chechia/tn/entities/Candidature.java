package chechia.tn.entities;

public class Candidature {
    private int id;
    private int userid;
    private String cv;
    private int anneeXp;
    private String experience;
    private Etat etat;
    private Type type;

    // Enum pour "etat"
    public enum Etat {
        EN_ATTENTE, ACCEPTEE, REFUSE
    }

    // Enum pour "type"
    public enum Type {
        EN_LIGNE, PRESENTIEL
    }

    // Constructeur
    public Candidature(int id, int userid, String cv, int anneeXp, String experience, Etat etat, Type type) {
        this.id = id;
        this.userid = userid;
        this.cv = cv;
        this.anneeXp = anneeXp;
        this.experience = experience;
        this.etat = etat;
        this.type = type;
    }

    public Candidature(int id, int userid) {
        this.id = id;
        this.userid = userid;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public int getAnneeXp() {
        return anneeXp;
    }

    public void setAnneeXp(int anneeXp) {
        this.anneeXp = anneeXp;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


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
                '}';
    }
}
