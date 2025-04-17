package chechia.tn.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFX extends Application {
    private static final Logger LOGGER = Logger.getLogger(MainFX.class.getName());

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/front.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setTitle(" Opportunite");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur de chargement FXML", e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
