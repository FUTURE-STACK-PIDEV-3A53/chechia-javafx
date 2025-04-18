package com.mila.controller;

import com.mila.model.Program;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddProgramController {
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TextField nationalityField;
    @FXML
    private Spinner<Integer> placesSpinner;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        // Initialiser les types de programme
        typeComboBox.getItems().addAll(
            "Échange académique",
            "Stage international",
            "Programme de recherche",
            "Double diplôme"
        );
    }

    @FXML
    private void handleSubmit() {
        if (validateInputs()) {
            Program newProgram = new Program(
                nameField.getText(),
                typeComboBox.getValue(),
                nationalityField.getText(),
                placesSpinner.getValue(),
                descriptionArea.getText()
            );

            // TODO: Ajouter le programme à la base de données
            showStatus("Programme ajouté avec succès !", true);
            clearForm();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        if (nameField.getText().trim().isEmpty()) {
            errors.append("Le nom du programme est requis.\n");
        }
        if (typeComboBox.getValue() == null) {
            errors.append("Le type de programme est requis.\n");
        }
        if (nationalityField.getText().trim().isEmpty()) {
            errors.append("La nationalité est requise.\n");
        }

        if (errors.length() > 0) {
            showStatus(errors.toString(), false);
            return false;
        }
        return true;
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("success", "error");
        statusLabel.getStyleClass().add(success ? "success" : "error");
        statusLabel.setManaged(true);
        statusLabel.setVisible(true);
    }

    private void clearForm() {
        nameField.clear();
        typeComboBox.setValue(null);
        nationalityField.clear();
        placesSpinner.getValueFactory().setValue(1);
        descriptionArea.clear();
    }
}