package com.mila.service;

import com.mila.model.Postulation;
import com.mila.model.ProgrammeEchange;
import com.mila.model.TypeEntretien;
import com.mila.model.TypeProgramme;
import com.mila.model.Nationalite;
import com.mila.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les programmes d'échange dans la base de données
 */
public class ProgrammeEchangeService implements IService<ProgrammeEchange> {

    private Connection connection;
    
    public ProgrammeEchangeService() {
        connection = MyDataBase.getInstance().getConnection();
    }
    
    @Override
    public boolean ajouter(ProgrammeEchange programme) throws SQLException {
        String query = "INSERT INTO prg_echange (nom_programme, type, nationalite, description, duree, date_prg) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, programme.getNomProgramme());
            ps.setString(2, programme.getType().name());
            ps.setString(3, programme.getNationalite().name());
            ps.setString(4, programme.getDescription());
            ps.setInt(5, programme.getDuree());
            ps.setDate(6, new java.sql.Date(programme.getDatePrg().getTime()));
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    programme.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean modifier(ProgrammeEchange programme) throws SQLException {
        String query = "UPDATE prg_echange SET nom_programme = ?, type = ?, nationalite = ?, "
                + "description = ?, duree = ?, date_prg = ? WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, programme.getNomProgramme());
            ps.setString(2, programme.getType().name());
            ps.setString(3, programme.getNationalite().name());
            ps.setString(4, programme.getDescription());
            ps.setInt(5, programme.getDuree());
            ps.setDate(6, new java.sql.Date(programme.getDatePrg().getTime()));
            ps.setInt(7, programme.getId());
            
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean supprimer(int id) throws SQLException {
        // D'abord supprimer les postulations associées
        String deletePostulationsQuery = "DELETE FROM postulation WHERE prg_echange_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(deletePostulationsQuery)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        
        // Ensuite supprimer le programme
        String deleteProgrammeQuery = "DELETE FROM prg_echange WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteProgrammeQuery)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public ProgrammeEchange getById(int id) throws SQLException {
        String query = "SELECT * FROM prg_echange WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                ProgrammeEchange programme = new ProgrammeEchange();
                programme.setId(rs.getInt("id"));
                programme.setNomProgramme(rs.getString("nom_programme"));
                programme.setType(TypeProgramme.fromString(rs.getString("type")));
                programme.setNationalite(Nationalite.fromString(rs.getString("nationalite")));
                programme.setDescription(rs.getString("description"));
                programme.setDuree(rs.getInt("duree"));
                programme.setDatePrg(rs.getDate("date_prg"));
                
                // Charger les postulations associées
                programme.setPostulations(getPostulationsForProgramme(id));
                
                return programme;
            }
        }
        return null;
    }

    @Override
    public List<ProgrammeEchange> getAll() throws SQLException {
        List<ProgrammeEchange> programmes = new ArrayList<>();
        String query = "SELECT * FROM prg_echange";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                ProgrammeEchange programme = new ProgrammeEchange();
                int id = rs.getInt("id");
                programme.setId(id);
                programme.setNomProgramme(rs.getString("nom_programme"));
                programme.setType(TypeProgramme.fromString(rs.getString("type")));
                programme.setNationalite(Nationalite.fromString(rs.getString("nationalite")));
                programme.setDescription(rs.getString("description"));
                programme.setDuree(rs.getInt("duree"));
                programme.setDatePrg(rs.getDate("date_prg"));
                
                // Charger les postulations associées
                programme.setPostulations(getPostulationsForProgramme(id));
                
                programmes.add(programme);
            }
        }
        return programmes;
    }
    
    /**
     * Récupère toutes les postulations pour un programme donné
     * @param programmeId L'identifiant du programme
     * @return La liste des postulations associées au programme
     * @throws SQLException En cas d'erreur SQL
     */
    private List<Postulation> getPostulationsForProgramme(int programmeId) throws SQLException {
        List<Postulation> postulations = new ArrayList<>();
        String query = "SELECT * FROM postulation WHERE prg_echange_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, programmeId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Postulation postulation = new Postulation();
                postulation.setId(rs.getInt("id"));
                postulation.setPrgEchangeId(rs.getInt("prg_echange_id"));
                postulation.setNom(rs.getString("nom"));
                postulation.setPrenom(rs.getString("prenom"));
                postulation.setAge(rs.getInt("age"));
                postulation.setEmail(rs.getString("email"));
                postulation.setLettreMotivation(rs.getString("lettre_motivation"));
                postulation.setCv(rs.getBytes("cv"));
                postulation.setTypeEntretien(TypeEntretien.fromString(rs.getString("entretien")));
                
                postulations.add(postulation);
            }
        }
        return postulations;
    }
}