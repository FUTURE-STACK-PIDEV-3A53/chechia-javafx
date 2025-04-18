package com.mila.service;

import com.mila.model.Postulation;
import com.mila.model.ProgrammeEchange;
import com.mila.model.TypeEntretien;
import com.mila.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les postulations dans la base de données
 */
public class PostulationService implements IService<Postulation> {

    private Connection connection;
    private ProgrammeEchangeService programmeService;
    
    public PostulationService() {
        connection = MyDataBase.getInstance().getConnection();
        programmeService = new ProgrammeEchangeService();
    }
    
    @Override
    public boolean ajouter(Postulation postulation) throws SQLException {
        String query = "INSERT INTO postulation (prg_echange_id, nom, prenom, age, email, lettre_motivation, cv, entretien) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, postulation.getPrgEchangeId());
            ps.setString(2, postulation.getNom());
            ps.setString(3, postulation.getPrenom());
            ps.setInt(4, postulation.getAge());
            ps.setString(5, postulation.getEmail());
            ps.setString(6, postulation.getLettreMotivation());
            ps.setBytes(7, postulation.getCv());
            ps.setString(8, postulation.getTypeEntretien().name());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    postulation.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean modifier(Postulation postulation) throws SQLException {
        String query = "UPDATE postulation SET prg_echange_id = ?, nom = ?, prenom = ?, age = ?, "
                + "email = ?, lettre_motivation = ?, cv = ?, entretien = ? WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, postulation.getPrgEchangeId());
            ps.setString(2, postulation.getNom());
            ps.setString(3, postulation.getPrenom());
            ps.setInt(4, postulation.getAge());
            ps.setString(5, postulation.getEmail());
            ps.setString(6, postulation.getLettreMotivation());
            ps.setBytes(7, postulation.getCv());
            ps.setString(8, postulation.getTypeEntretien().name());
            ps.setInt(9, postulation.getId());
            
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean supprimer(int id) throws SQLException {
        String query = "DELETE FROM postulation WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Postulation getById(int id) throws SQLException {
        String query = "SELECT * FROM postulation WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Postulation postulation = new Postulation();
                postulation.setId(rs.getInt("id"));
                int programmeId = rs.getInt("prg_echange_id");
                postulation.setPrgEchangeId(programmeId);
                postulation.setNom(rs.getString("nom"));
                postulation.setPrenom(rs.getString("prenom"));
                postulation.setAge(rs.getInt("age"));
                postulation.setEmail(rs.getString("email"));
                postulation.setLettreMotivation(rs.getString("lettre_motivation"));
                postulation.setCv(rs.getBytes("cv"));
                postulation.setTypeEntretien(TypeEntretien.fromString(rs.getString("entretien")));
                
                // Charger le programme associé
                ProgrammeEchange programme = programmeService.getById(programmeId);
                postulation.setProgrammeEchange(programme);
                
                return postulation;
            }
        }
        return null;
    }

    @Override
    public List<Postulation> getAll() throws SQLException {
        List<Postulation> postulations = new ArrayList<>();
        String query = "SELECT * FROM postulation";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Postulation postulation = new Postulation();
                postulation.setId(rs.getInt("id"));
                int programmeId = rs.getInt("prg_echange_id");
                postulation.setPrgEchangeId(programmeId);
                postulation.setNom(rs.getString("nom"));
                postulation.setPrenom(rs.getString("prenom"));
                postulation.setAge(rs.getInt("age"));
                postulation.setEmail(rs.getString("email"));
                postulation.setLettreMotivation(rs.getString("lettre_motivation"));
                postulation.setCv(rs.getBytes("cv"));
                postulation.setTypeEntretien(TypeEntretien.fromString(rs.getString("entretien")));
                
                // Charger le programme associé
                ProgrammeEchange programme = programmeService.getById(programmeId);
                postulation.setProgrammeEchange(programme);
                
                postulations.add(postulation);
            }
        }
        return postulations;
    }
    
    /**
     * Récupère toutes les postulations pour un programme donné
     * @param programmeId L'identifiant du programme
     * @return La liste des postulations associées au programme
     * @throws SQLException En cas d'erreur SQL
     */
    public List<Postulation> getByProgrammeId(int programmeId) throws SQLException {
        List<Postulation> postulations = new ArrayList<>();
        String query = "SELECT * FROM postulation WHERE prg_echange_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, programmeId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Postulation postulation = new Postulation();
                postulation.setId(rs.getInt("id"));
                postulation.setPrgEchangeId(programmeId);
                postulation.setNom(rs.getString("nom"));
                postulation.setPrenom(rs.getString("prenom"));
                postulation.setAge(rs.getInt("age"));
                postulation.setEmail(rs.getString("email"));
                postulation.setLettreMotivation(rs.getString("lettre_motivation"));
                postulation.setCv(rs.getBytes("cv"));
                postulation.setTypeEntretien(TypeEntretien.fromString(rs.getString("entretien")));
                
                // Charger le programme associé
                ProgrammeEchange programme = programmeService.getById(programmeId);
                postulation.setProgrammeEchange(programme);
                
                postulations.add(postulation);
            }
        }
        return postulations;
    }
}