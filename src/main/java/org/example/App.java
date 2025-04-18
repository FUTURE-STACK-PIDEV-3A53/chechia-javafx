package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the main interface directly
            Parent root = FXMLLoader.load(getClass().getResource("/main.fxml")); 
            Scene scene = new Scene(root);
            primaryStage.setTitle("Game Room Management"); // Set main window title
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Application Error");
            alert.setHeaderText("Failed to load the application interface.");
            alert.setContentText("An unexpected error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}