package chechia.tn.service.fedi;

import chechia.tn.entities.Post;
import chechia.tn.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostService {
    private Connection cnx;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;

    public PostService() {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                cnx = MyDataBase.getInstance().getCnx();
                if (cnx != null && !cnx.isClosed()) {
                    System.out.println("✅ Service Post initialisé avec succès");
                    return;
                }
                throw new SQLException("La connexion à la base de données n'a pas pu être établie");
            } catch (SQLException e) {
                System.err.println("❌ Tentative " + (retries + 1) + "/" + MAX_RETRIES + 
                    " - Erreur de connexion à la base de données: " + e.getMessage());
                retries++;
                if (retries < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        System.err.println("❌ Échec de l'initialisation du service Post après " + MAX_RETRIES + " tentatives");
    }

    public void ajouter(Post post) {
        String req = "INSERT INTO post (titre, contenu, date_post, image, video) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, post.getTitre());
            ps.setString(2, post.getContenu());
            ps.setString(3, post.getDate_post());
            ps.setString(4, post.getImage());
            ps.setString(5, post.getVideo());
            ps.executeUpdate();
            System.out.println("Post ajouté avec succès");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void modifier(Post post) {
        String req = "UPDATE post SET titre=?, contenu=?, date_post=?, image=?, video=? WHERE id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, post.getTitre());
            ps.setString(2, post.getContenu());
            ps.setString(3, post.getDate_post());
            ps.setString(4, post.getImage());
            ps.setString(5, post.getVideo());
            ps.setInt(6, post.getId());
            ps.executeUpdate();
            System.out.println("Post modifié avec succès");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void supprimer(int id) {
        String req = "DELETE FROM post WHERE id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Post supprimé avec succès");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Post> afficher() {
        List<Post> list = new ArrayList<>();
        String req = "SELECT * FROM post";
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setTitre(rs.getString("titre"));
                post.setContenu(rs.getString("contenu"));
                post.setDate_post(rs.getString("date_post"));
                post.setImage(rs.getString("image"));
                post.setVideo(rs.getString("video"));
                list.add(post);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public Post getById(int id) {
        String req = "SELECT * FROM post WHERE id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setTitre(rs.getString("titre"));
                post.setContenu(rs.getString("contenu"));
                post.setDate_post(rs.getString("date_post"));
                post.setImage(rs.getString("image"));
                post.setVideo(rs.getString("video"));
                return post;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}