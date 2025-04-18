package org.example.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AdminDashboardController implements Initializable {
    private static final Logger logger = Logger.getLogger(AdminDashboardController.class.getName());

    @FXML private Label welcomeLabel;
    @FXML private Button eventsButton;
    @FXML private Button reservationsButton;
    @FXML private Button logoutButton;
    
    private Stage stage;
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set welcome message
        welcomeLabel.setText("Bienvenue, Admin! " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
    
    @FXML
    private void handleEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventManagement.fxml"));
            Parent root = loader.load();
            
            Stage eventStage = new Stage();
            eventStage.setTitle("Gestion des Événements");
            
            // Create scene with proper styling
            Scene scene = new Scene(root);
            scene.getStylesheets().clear();
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            
            eventStage.setScene(scene);
            eventStage.setMinWidth(800);
            eventStage.setMinHeight(600);
            eventStage.show();
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'ouverture de la gestion des événements", e);
        }
    }
    
    @FXML
    private void handleReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reservation.fxml"));
            Parent root = loader.load();
            
            Stage reservationStage = new Stage();
            reservationStage.setTitle("Gestion des Réservations");
            
            // Create scene with proper styling
            Scene scene = new Scene(root);
            scene.getStylesheets().clear();
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            
            reservationStage.setScene(scene);
            reservationStage.setMinWidth(800);
            reservationStage.setMinHeight(600);
            reservationStage.show();
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'ouverture de la gestion des réservations", e);
        }
    }
    
    @FXML
    private void handleLogout() {
        // Close this window and go back to login
        if (stage != null) {
            stage.close();
            
            try {
                // Reload the main/login screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                Parent root = loader.load();
                
                Stage loginStage = new Stage();
                loginStage.setTitle("Connexion");
                
                // Create scene with proper styling
                Scene scene = new Scene(root);
                scene.getStylesheets().clear();
                String css = getClass().getResource("/styles.css").toExternalForm();
                scene.getStylesheets().add(css);
                
                loginStage.setScene(scene);
                loginStage.setMinWidth(400);
                loginStage.setMinHeight(300);
                loginStage.show();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Erreur lors du retour à la page de connexion", e);
            }
        }
    }
    
    @FXML
    private void handleExit() {
        Platform.exit();
    }
} 