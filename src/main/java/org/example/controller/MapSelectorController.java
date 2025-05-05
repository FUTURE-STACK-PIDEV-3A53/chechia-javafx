package org.example.controller;

import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class MapSelectorController {
    @FXML private WebView webView;
    @FXML private TextField searchField;
    @FXML private Label selectedLocationLabel;
    
    private String selectedLocation = null;
    private double selectedLat = 0;
    private double selectedLng = 0;
    private Consumer<String> onLocationSelected;

    @FXML
    public void initialize() {
        try {
            // Charger la carte Google Maps
            webView.getEngine().load(getClass().getResource("/map.html").toExternalForm());
            
            // Permettre l'appel de méthodes Java depuis JavaScript
            webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                switch (newState) {
                    case SUCCEEDED:
                        try {
                            JSObject window = (JSObject) webView.getEngine().executeScript("window");
                            window.setMember("java", this);
                        } catch (Exception e) {
                            showError("Erreur lors de l'initialisation de la carte: " + e.getMessage());
                        }
                        break;
                    case FAILED:
                        showError("Échec du chargement de la carte");
                        break;
                }
            });
        } catch (Exception e) {
            showError("Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    public void setOnLocationSelected(Consumer<String> callback) {
        this.onLocationSelected = callback;
    }

    // Cette méthode sera appelée depuis JavaScript quand un lieu est sélectionné
    public void locationSelected(String name, double lat, double lng) {
        selectedLocation = name;
        selectedLat = lat;
        selectedLng = lng;
        selectedLocationLabel.setText("Lieu sélectionné : " + name);
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            try {
                // Échapper les apostrophes pour éviter l'injection JavaScript
                query = query.replace("'", "\\'");
                webView.getEngine().executeScript("searchLocation('" + query + "')");
            } catch (Exception e) {
                showError("Erreur lors de la recherche: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleConfirm() {
        if (selectedLocation != null && onLocationSelected != null) {
            onLocationSelected.accept(selectedLocation);
            closeWindow();
        } else {
            showError("Veuillez sélectionner un lieu sur la carte");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) webView.getScene().getWindow();
        stage.close();
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
} 