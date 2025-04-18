package org.example;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale de l'application JavaFX
 */
public class Main extends Application {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger le fichier FXML de connexion
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/Login.fxml"));
            Parent root = loader.load();
            
            // Créer la scène
            Scene scene = new Scene(root);
            
            // Appliquer le style CSS
            scene.getStylesheets().clear(); // Clear any existing stylesheets
            String css = Main.class.getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            
            // Configurer la fenêtre
            primaryStage.setTitle("Connexion");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(300);
            primaryStage.setResizable(true);
            
            // Afficher la fenêtre
            primaryStage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors du chargement des ressources FXML:", e);
            System.exit(1);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Ressource FXML ou CSS introuvable:", e);
            System.exit(1);
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Erreur lors du démarrage de l'application:", e);
            System.exit(1);
        }
    }

    /**
     * Ouvre une fenêtre de gestion des réservations
     */
    public static void openReservationWindow() {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/Reservation.fxml"));
            Parent root = loader.load();
            
            // Créer la scène
            Scene scene = new Scene(root);
            
            // Appliquer le style CSS
            scene.getStylesheets().clear(); // Clear any existing stylesheets
            String css = Main.class.getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            
            // Configurer la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Gestion des Réservations");
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            
            // Afficher la fenêtre
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors du chargement de la vue des réservations:", e);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Ressource FXML ou CSS introuvable pour les réservations:", e);
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'ouverture de la gestion des réservations:", e);
        }
    }
    
    /**
     * Ouvre une fenêtre de gestion des événements
     */
    public static void openEventWindow() {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/EventManagement.fxml"));
            Parent root = loader.load();
            
            // Créer la scène
            Scene scene = new Scene(root);
            
            // Appliquer le style CSS
            scene.getStylesheets().clear(); // Clear any existing stylesheets
            String css = Main.class.getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            
            // Configurer la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Gestion des Événements");
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            
            // Afficher la fenêtre
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors du chargement de la vue des événements:", e);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Ressource FXML ou CSS introuvable pour les événements:", e);
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'ouverture de la gestion des événements:", e);
        }
    }

    /**
     * Point d'entrée principal de l'application
     */
    public static void main(String[] args) {
        try {
            // Lancer l'application JavaFX
            launch(args);
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Erreur fatale:", e);
            System.exit(1);
        }
    }
}
