package chechia.tn.entities;

public class Post {
    private int id;
    private String titre;
    private String contenu;
    private String date_post;
    private String image;
    private String video;

    public Post() {
    }

    public Post(int id, String titre, String contenu, String date_post, String image, String video) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.date_post = date_post;
        this.image = image;
        this.video = video;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getDate_post() {
        return date_post;
    }

    public void setDate_post(String date_post) {
        this.date_post = date_post;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    @Override
    public String toString() {
        return "Titre: " + titre + 
               "\nContenu: " + contenu + 
               "\nDate: " + date_post + 
               "\nImage: " + image + 
               (video != null && !video.isEmpty() ? "\nVidéo: " + video : "");
    }
}
