package com.mila.service;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface générique pour les services CRUD
 * @param <T> Type de l'entité
 */
public interface IService<T> {
    
    /**
     * Ajoute une entité dans la base de données
     * @param t L'entité à ajouter
     * @return true si l'ajout a réussi, false sinon
     * @throws SQLException En cas d'erreur SQL
     */
    boolean ajouter(T t) throws SQLException;
    
    /**
     * Modifie une entité dans la base de données
     * @param t L'entité à modifier
     * @return true si la modification a réussi, false sinon
     * @throws SQLException En cas d'erreur SQL
     */
    boolean modifier(T t) throws SQLException;
    
    /**
     * Supprime une entité de la base de données
     * @param id L'identifiant de l'entité à supprimer
     * @return true si la suppression a réussi, false sinon
     * @throws SQLException En cas d'erreur SQL
     */
    boolean supprimer(int id) throws SQLException;
    
    /**
     * Récupère une entité par son identifiant
     * @param id L'identifiant de l'entité à récupérer
     * @return L'entité correspondante ou null si non trouvée
     * @throws SQLException En cas d'erreur SQL
     */
    T getById(int id) throws SQLException;
    
    /**
     * Récupère toutes les entités
     * @return La liste de toutes les entités
     * @throws SQLException En cas d'erreur SQL
     */
    List<T> getAll() throws SQLException;
}