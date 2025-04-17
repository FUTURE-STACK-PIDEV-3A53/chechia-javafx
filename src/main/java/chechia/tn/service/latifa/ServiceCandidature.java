package chechia.tn.service.latifa;

import chechia.tn.entities.Candidature;
import chechia.tn.entities.Opportunite;
import chechia.tn.service.IService;
import chechia.tn.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCandidature implements IService<Candidature> {

    private final Connection cnx;

    public ServiceCandidature() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void add(Candidature c) {
        // Vérification que l'opportunité n'est pas null
        if (c.getOpportunite() == null) {
            System.err.println("Erreur : la candidature doit être associée à une opportunité.");
            return;
        }

        // Vérifier que l'opportunité existe dans la base de données
        Opportunite opp = getOpportuniteById(c.getOpportunite().getId());
        if (opp == null) {
            System.err.println("Erreur : l'opportunité avec l'ID " + c.getOpportunite().getId() + " n'existe pas dans la base de données.");
            return;
        }

        // Vérification que tous les champs sont valides avant d'ajouter
        if (c.getCv() == null || c.getCv().isEmpty()) {
            System.err.println("Erreur : le CV doit être renseigné.");
            return;
        }

        String qry = "INSERT INTO candidature (userid, cv, annee_xp, experience, etat, type, opportunite_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, c.getUserid());
            pstm.setString(2, c.getCv());
            pstm.setInt(3, c.getAnneeXp());
            pstm.setString(4, c.getExperience());
            pstm.setString(5, c.getEtat().name().toLowerCase()); // Convert to lowercase
            pstm.setString(6, c.getType().name().toLowerCase()); // Convert to lowercase
            pstm.setInt(7, c.getOpportunite().getId());

            System.out.println("Tentative d'ajout de candidature avec opportunite_id: " + c.getOpportunite().getId()); // Debug line

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Candidature ajoutée avec succès !");
            } else {
                System.out.println("Aucune ligne insérée. Vérifiez la base de données.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la candidature : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Candidature> afficher() {
        List<Candidature> candidatures = new ArrayList<>();
        String qry = "SELECT * FROM candidature";

        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(qry)) {
            while (rs.next()) {
                // Get the values from database
                String etatStr = rs.getString("etat");
                String typeStr = rs.getString("type");

                // Convert database values to enum values (case-insensitive)
                Candidature.Etat etat = Candidature.Etat.valueOf(etatStr.toUpperCase());
                Candidature.Type type = Candidature.Type.valueOf(typeStr.toUpperCase());

                Candidature c = new Candidature(
                        rs.getInt("id"),
                        rs.getInt("userid"),
                        rs.getString("cv"),
                        rs.getInt("annee_xp"),
                        rs.getString("experience"),
                        etat,
                        type
                );

                // Récupérer l'opportunité associée à la candidature
                int opportuniteId = rs.getInt("opportunite_id");
                if (opportuniteId != 0) {
                    Opportunite opportunite = getOpportuniteById(opportuniteId);
                    c.setOpportunite(opportunite);
                }

                candidatures.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des candidatures : " + e.getMessage());
            e.printStackTrace(); // Add stack trace for better debugging
        }
        return candidatures;
    }

    @Override
    public void update(Candidature c) {
        if (c.getOpportunite() == null) {
            System.err.println("Erreur : la candidature doit être associée à une opportunité.");
            return;
        }

        String qry = "UPDATE candidature SET cv = ?, annee_xp = ?, experience = ?, etat = ?, type = ?, opportunite_id = ? WHERE id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, c.getCv());
            pstm.setInt(2, c.getAnneeXp());
            pstm.setString(3, c.getExperience());
            pstm.setString(4, c.getEtat().name()); // Gère correctement l'état en tant qu'énumération
            pstm.setString(5, c.getType().name()); // Gère correctement le type
            pstm.setInt(6, c.getOpportunite().getId()); // Associe l'opportunité à la candidature
            pstm.setInt(7, c.getId());

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Candidature mise à jour !");
            } else {
                System.out.println("Aucune candidature mise à jour (ID introuvable).");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @Override
    public void delete(Candidature c) {
        String qry = "DELETE FROM candidature WHERE id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, c.getId());

            int rowsAffected = pstm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Candidature supprimée !");
            } else {
                System.out.println("Aucune candidature supprimée (ID introuvable).");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    // Méthode pour mettre à jour l'état d'une candidature
    public void updateEtat(int candidatureId, Candidature.Etat nouvelEtat) {
        String qry = "UPDATE candidature SET etat = ? WHERE id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, nouvelEtat.name());
            pstm.setInt(2, candidatureId);

            int rowsUpdated = pstm.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("État de la candidature mis à jour avec succès !");
            } else {
                System.out.println("Aucune candidature trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'état de la candidature : " + e.getMessage());
        }
    }

    // Méthode pour récupérer une opportunité par son ID
    private Opportunite getOpportuniteById(int opportuniteId) {
        String qry = "SELECT * FROM opportunite_professionnelle WHERE id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, opportuniteId);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return new Opportunite(
                            rs.getInt("id"),
                            rs.getString("titre"),
                            rs.getString("description"),
                            rs.getInt("exp_years"),
                            rs.getString("lieu"),
                            Opportunite.Type.valueOf(rs.getString("type").toUpperCase()) // Gestion de la casse
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'opportunité : " + e.getMessage());
        }
        return null; // Retourne null si l'opportunité n'est pas trouvée
    }
}
