package org.example.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class EventAdminDashboardController implements Initializable {
    private static final Logger logger = Logger.getLogger(EventAdminDashboardController.class.getName());

    @FXML private Label welcomeLabel;
    @FXML private Button eventsButton;
    @FXML private Button reservationsButton;
    @FXML private Button logoutButton;
    @FXML private Label adminEventCountLabel;
    @FXML private Label adminReservationCountLabel;
    @FXML private BorderPane rootPane;
    @FXML private Button themeToggleButton;
    
    private Stage stage;
    private boolean darkTheme = true;
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set welcome message
        welcomeLabel.setText("Bienvenue, Admin! " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        updateAdminStats();
        
        // Appliquer le thème sombre par défaut
        if (rootPane != null) {
            rootPane.getStylesheets().clear();
            String css = getClass().getResource("/netflix.css").toExternalForm();
            rootPane.getStylesheets().add(css);
            themeToggleButton.setText("🌓 Thème");
        }
    }
    
    @FXML
    private void handleToggleTheme(ActionEvent event) {
        darkTheme = !darkTheme;
        if (rootPane != null) {
            rootPane.getStylesheets().clear();
            String css = getClass().getResource(darkTheme ? "/netflix.css" : "/light.css").toExternalForm();
            rootPane.getStylesheets().add(css);
            themeToggleButton.setText(darkTheme ? "🌓 Thème" : "🌞 Thème");
        }
    }
    
    private void updateAdminStats() {
        int eventCount = getEventCount();
        int reservationCount = getReservationCount();
        if (adminEventCountLabel != null) adminEventCountLabel.setText("Événements : " + eventCount);
        if (adminReservationCountLabel != null) adminReservationCountLabel.setText("Réservations : " + reservationCount);
    }

    private int getEventCount() {
        try {
            org.example.model.EventDAO eventDAO = new org.example.model.EventDAO();
            return eventDAO.afficherEvents().size();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors du comptage des événements", e);
            return 0;
        }
    }

    private int getReservationCount() {
        try {
            org.example.model.ReservationDAO reservationDAO = new org.example.model.ReservationDAO();
            return reservationDAO.afficherReservations().size();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors du comptage des réservations", e);
            return 0;
        }
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            
            Stage loginStage = new Stage();
            loginStage.setTitle("Connexion");
            
            Scene scene = new Scene(root);
            scene.getStylesheets().clear();
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            
            loginStage.setScene(scene);
            loginStage.setMinWidth(400);
            loginStage.setMinHeight(500);
            loginStage.show();
            
            if (stage != null) {
                stage.close();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la déconnexion", e);
        }
    }
    
    @FXML
    private void handleExit() {
        Platform.exit();
    }
    
    @FXML
    private void handleOpenUserDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventUserDashboard.fxml"));
            Parent root = loader.load();

            Stage userStage = new Stage();
            userStage.setTitle("Espace Utilisateur");

            Scene scene = new Scene(root);
            scene.getStylesheets().clear();
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            userStage.setScene(scene);
            userStage.setMinWidth(800);
            userStage.setMinHeight(600);
            userStage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'ouverture de l'interface utilisateur front", e);
        }
    }
} 