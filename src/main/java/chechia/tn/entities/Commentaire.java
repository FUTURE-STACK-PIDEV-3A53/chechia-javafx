package chechia.tn.entities;

public class Commentaire {
    private int id;
    private int post_id;
    private String contenu;
    private String date_commentaire;

    public Commentaire() {
    }

    public Commentaire(int id, int post_id, String contenu, String date_commentaire) {
        this.id = id;
        this.post_id = post_id;
        this.contenu = contenu;
        this.date_commentaire = date_commentaire;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getDate_commentaire() {
        return date_commentaire;
    }

    public void setDate_commentaire(String date_commentaire) {
        this.date_commentaire = date_commentaire;
    }

    @Override
    public String toString() {
        return contenu + "\nPublié le: " + date_commentaire;
    }
}
