package chechia.tn.services;

import chechia.tn.entities.Commentaire;
import chechia.tn.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireService {
    private Connection cnx;

    public CommentaireService() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    public boolean postExists(int postId) {
        String req = "SELECT COUNT(*) FROM post WHERE id = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public void ajouter(Commentaire commentaire) {
        if (!postExists(commentaire.getPost_id())) {
            System.out.println("Erreur: Le post associé n'existe pas");
            return;
        }

        String req = "INSERT INTO commentaire (post_id, contenu, date_commentaire) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, commentaire.getPost_id());
            ps.setString(2, commentaire.getContenu());
            ps.setString(3, commentaire.getDate_commentaire());
            ps.executeUpdate();
            System.out.println("Commentaire ajouté avec succès");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void modifier(Commentaire commentaire) {
        if (!postExists(commentaire.getPost_id())) {
            System.out.println("Erreur: Le post associé n'existe pas");
            return;
        }

        String req = "UPDATE commentaire SET post_id=?, contenu=?, date_commentaire=? WHERE id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, commentaire.getPost_id());
            ps.setString(2, commentaire.getContenu());
            ps.setString(3, commentaire.getDate_commentaire());
            ps.setInt(4, commentaire.getId());
            ps.executeUpdate();
            System.out.println("Commentaire modifié avec succès");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void supprimer(int id) {
        String req = "DELETE FROM commentaire WHERE id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Commentaire supprimé avec succès");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void supprimerParPostId(int postId) {
        String req = "DELETE FROM commentaire WHERE post_id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, postId);
            ps.executeUpdate();
            System.out.println("Tous les commentaires du post supprimés avec succès");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Commentaire> afficher() {
        List<Commentaire> list = new ArrayList<>();
        String req = "SELECT * FROM commentaire";
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Commentaire commentaire = new Commentaire();
                commentaire.setId(rs.getInt("id"));
                commentaire.setPost_id(rs.getInt("post_id"));
                commentaire.setContenu(rs.getString("contenu"));
                commentaire.setDate_commentaire(rs.getString("date_commentaire"));
                list.add(commentaire);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public List<Commentaire> getByPostId(int postId) {
        List<Commentaire> list = new ArrayList<>();
        String req = "SELECT * FROM commentaire WHERE post_id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Commentaire commentaire = new Commentaire();
                commentaire.setId(rs.getInt("id"));
                commentaire.setPost_id(rs.getInt("post_id"));
                commentaire.setContenu(rs.getString("contenu"));
                commentaire.setDate_commentaire(rs.getString("date_commentaire"));
                list.add(commentaire);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public Commentaire getById(int id) {
        String req = "SELECT * FROM commentaire WHERE id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Commentaire commentaire = new Commentaire();
                commentaire.setId(rs.getInt("id"));
                commentaire.setPost_id(rs.getInt("post_id"));
                commentaire.setContenu(rs.getString("contenu"));
                commentaire.setDate_commentaire(rs.getString("date_commentaire"));
                return commentaire;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}