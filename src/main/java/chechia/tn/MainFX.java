package chechia.tn;

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
            FXMLLoader fxmlLoader = new FXMLLoader(MainFX.class.getResource("/chechia/tn/front.fxml"));
            if (fxmlLoader.getLocation() == null) {
                throw new IOException("Le fichier FXML n'a pas été trouvé");
            }
            Scene scene = new Scene(fxmlLoader.load());
            String css = MainFX.class.getResource("/chechia/tn/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.setTitle("Administration des Posts");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur de chargement FXML: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}