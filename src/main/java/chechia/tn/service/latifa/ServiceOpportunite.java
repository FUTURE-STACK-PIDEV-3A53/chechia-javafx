package chechia.tn.service.latifa;

import chechia.tn.entities.Opportunite;
import chechia.tn.service.IService;
import chechia.tn.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceOpportunite implements IService<Opportunite> {
    private final Connection cnx;

    public ServiceOpportunite() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    public void add(Opportunite opportunite) {
        String qry = "INSERT INTO opportunite_professionnelle (titre, description, exp_years, lieu, type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, opportunite.getTitre());
            pstm.setString(2, opportunite.getDescription());
            pstm.setInt(3, opportunite.getExp_years());  // Utilisation de setInt pour l'année d'expérience
            pstm.setString(4, opportunite.getLieu());
            pstm.setString(5, opportunite.getType().name());

            pstm.executeUpdate();
            System.out.println("Opportunité ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'opportunité : " + e.getMessage());
        }
    }

    @Override
    public List<Opportunite> afficher() {
        List<Opportunite> opportunites = new ArrayList<>();
        String qry = "SELECT * FROM opportunite_professionnelle";

        try (Statement stm = cnx.createStatement()) {
            try (ResultSet rs = stm.executeQuery(qry)) {
                while (rs.next()) {
                    Opportunite opportunite = new Opportunite(
                            rs.getInt("id"),
                            rs.getString("titre"),
                            rs.getString("description"),
                            rs.getInt("exp_years"),
                            rs.getString("lieu"),
                            Opportunite.Type.valueOf(rs.getString("type").toUpperCase())
                    );
                    opportunites.add(opportunite);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des opportunités : " + e.getMessage());
        }
        return opportunites;
    }

    @Override
    public void update(Opportunite opportunite) {
        String qry = "UPDATE opportunite_professionnelle SET description = ?, exp_years = ?, lieu = ?, type = ? WHERE titre = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, opportunite.getDescription());
            pstm.setInt(2, opportunite.getExp_years());  // Utilisation de setInt pour l'année d'expérience
            pstm.setString(3, opportunite.getLieu());
            pstm.setString(4, opportunite.getType().name());
            pstm.setString(5, opportunite.getTitre());

            int rowsUpdated = pstm.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Opportunité mise à jour avec succès !");
            } else {
                System.out.println("Aucune opportunité trouvée avec ce titre.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'opportunité : " + e.getMessage());
        }
    }

    @Override
    public void delete(Opportunite opportunite) {
        String qry = "DELETE FROM opportunite_professionnelle WHERE titre = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, opportunite.getTitre());

            int rowsDeleted = pstm.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Opportunité supprimée avec succès !");
            } else {
                System.out.println("Aucune opportunité trouvée avec ce titre.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'opportunité : " + e.getMessage());
        }
    }

    // Nouvelle méthode pour rechercher une opportunité par son ID
    public Opportunite findById(int id) {
        String qry = "SELECT * FROM opportunite_professionnelle WHERE id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, id);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return new Opportunite(
                            rs.getInt("id"),
                            rs.getString("titre"),
                            rs.getString("description"),
                            rs.getInt("exp_years"),
                            rs.getString("lieu"),
                            Opportunite.Type.valueOf(rs.getString("type").toUpperCase())
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'opportunité : " + e.getMessage());
        }
        return null;  // Retourne null si l'opportunité n'est pas trouvée
    }
}
