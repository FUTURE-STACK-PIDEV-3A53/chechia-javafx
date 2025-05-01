
package chechia.tn.test;

import chechia.tn.controllers.latifa.FronttController;
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/latifa/front.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Initialiser HostServices dans le contrôleur
            FronttController controller = fxmlLoader.getController();
            controller.setHostServices(getHostServices());

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur de chargement FXML", e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}