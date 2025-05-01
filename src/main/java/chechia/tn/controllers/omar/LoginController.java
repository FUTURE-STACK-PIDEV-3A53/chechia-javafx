package chechia.tn.controllers.omar;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class LoginController implements Initializable {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button cancelButton;
    @FXML private Button quickLoginButton;
    @FXML private Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Placeholder for any initialization needed
        statusLabel.setText("");

        // Setup Enter key handling for login
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Veuillez entrer un nom d'utilisateur et un mot de passe");
            return;
        }

        // Simple authentication for demonstration
        // In a real application, you would check against a database
        if (username.equals("admin") && password.equals("admin123")) {
            // Authentication successful
            try {
                // Get the Stage from the login button
                Stage loginStage = (Stage) loginButton.getScene().getWindow();
                loginStage.close();

                // Open the admin dashboard
                openAdminDashboard();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Erreur lors de l'ouverture du tableau de bord", e);
                statusLabel.setText("Erreur système: " + e.getMessage());
            }
        } else {
            // Authentication failed
            statusLabel.setText("Nom d'utilisateur ou mot de passe incorrect");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

    @FXML
    private void handleQuickLogin() {
        try {
            // Feedback de connexion
            statusLabel.setText("Connexion en cours...");
            statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

            // Get the Stage from the login button
            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();

            // Open the admin dashboard with automatic credentials
            openAdminDashboard();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de l'accès rapide", e);
            statusLabel.setText("Erreur système: " + e.getMessage());
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    @FXML
    private void handleCancel() {
        Platform.exit();
    }

    private void openAdminDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboard.fxml"));
            Parent root = loader.load();

            // Get the controller and set stage
            AdminDashboardController controller = loader.getController();

            Stage dashboardStage = new Stage();
            controller.setStage(dashboardStage);

            // Create the scene
            Scene scene = new Scene(root);

            // Apply CSS
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            // Configure the window
            dashboardStage.setTitle("Tableau de bord administrateur");
            dashboardStage.setScene(scene);
            dashboardStage.setMinWidth(800);
            dashboardStage.setMinHeight(600);

            // Show the window
            dashboardStage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors du chargement du tableau de bord", e);
        }
    }
}