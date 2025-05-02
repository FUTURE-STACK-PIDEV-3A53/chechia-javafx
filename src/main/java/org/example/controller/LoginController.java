package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.dao.UserDAO;
import org.example.model.User;
import org.example.utils.PasswordUtils;
import org.example.utils.SessionManager;

import java.io.IOException;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField adminCodeField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Button adminButton;
    @FXML private Label errorLabel;

    private UserDAO userDAO;
    private static final String ADMIN_CODE = "admin123"; // You should store this securely in a real application

    public void initialize() {
        userDAO = UserDAO.getInstance();
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }

        User user = userDAO.getUserByEmail(email);
        if (user != null && PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
            SessionManager.setUser(user);
            openMainView(user);
        } else {
            errorLabel.setText("Invalid email or password");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/Register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Error loading registration form");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdminLogin() {
        String adminCode = adminCodeField.getText();

        if (adminCode.isEmpty()) {
            errorLabel.setText("Please enter admin code");
            return;
        }

        if (adminCode.equals(ADMIN_CODE)) {
            try {
                // Get or create admin user
                User adminUser = userDAO.getUserByEmail("admin@admin.com");
                if (adminUser == null) {
                    // If admin doesn't exist, create one
                    adminUser = new User();
                    adminUser.setUsername("admin");
                    adminUser.setEmail("admin@admin.com");
                    adminUser.setPasswordHash(PasswordUtils.hashPassword("admin123")); // Set a default password
                    adminUser.setAdmin(true);
                    if (!userDAO.addUser(adminUser)) {
                        errorLabel.setText("Failed to create admin user");
                        return;
                    }
                }

                SessionManager.setUser(adminUser);
                openMainView(adminUser);
            } catch (Exception e) {
                errorLabel.setText("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid admin code");
        }
    }

    private void openMainView(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the current user
            MainController controller = loader.getController();
            controller.setCurrentUser(user);
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Error loading main view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}