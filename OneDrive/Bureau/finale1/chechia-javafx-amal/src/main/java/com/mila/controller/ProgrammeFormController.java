package com.mila.controller;

import com.mila.model.Nationalite;
import com.mila.model.ProgrammeEchange;
import com.mila.model.TypeProgramme;
import com.mila.service.ProgrammeEchangeService;
import com.mila.service.TranslationService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import com.mila.service.TranslateService;

import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

public class ProgrammeFormController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private TextField nomProgrammeField;
    @FXML private ComboBox<TypeProgramme> typeComboBox;
    @FXML private ComboBox<Nationalite> nationaliteComboBox;
    @FXML private TextField dureeField;
    @FXML private TextArea descriptionField;
    @FXML private Button translateButton;
    
    private ProgrammeEchangeService programmeService;
    private ProgrammeEchange programme;
    private boolean editMode = false;
    private MainController mainController;
    private TranslationService translationService;
    
    public ProgrammeFormController() {
        programmeService = new ProgrammeEchangeService();
        programme = new ProgrammeEchange();
        translationService = TranslationService.getInstance();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Ajouter un validateur pour le champ durée (doit être un nombre)
        dureeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                dureeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        // Initialiser les ComboBox avec les valeurs des énumérations
        typeComboBox.getItems().addAll(TypeProgramme.values());
        nationaliteComboBox.getItems().addAll(Nationalite.values());
        
        // Ajouter le bouton de traduction
        translateButton = new Button("Traduire en Anglais");
        translateButton.setOnAction(e -> handleTranslate());
        // Traduire les labels et boutons
        titleLabel.setText(translationService.translateToEnglish(titleLabel.getText()));
        translateButton.setText(translationService.translateToEnglish("Traduire en Anglais"));
    }
    
    /**
     * Initialise le formulaire avec les données d'un programme existant
     * @param programme Le programme à modifier
     */
    public void initForEdit(ProgrammeEchange programme) {
        this.programme = programme;
        this.editMode = true;
        titleLabel.setText("Modifier un Programme d'Échange");
        
        // Remplir les champs avec les données du programme
        nomProgrammeField.setText(programme.getNomProgramme());
        typeComboBox.setValue(programme.getType());
        nationaliteComboBox.setValue(programme.getNationalite());
        dureeField.setText(String.valueOf(programme.getDuree()));
        descriptionField.setText(programme.getDescription());
    }
    
    /**
     * Définit le contrôleur principal pour permettre la mise à jour de la vue principale
     * @param mainController Le contrôleur principal
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    /**
     * Gère l'action du bouton Enregistrer
     */
    @FXML
    private void handleSave() {
        if (validateInputs()) {
            try {
                // Récupérer les valeurs des champs
                programme.setNomProgramme(nomProgrammeField.getText());
                programme.setType(typeComboBox.getValue());
                programme.setNationalite(nationaliteComboBox.getValue());
                programme.setDuree(Integer.parseInt(dureeField.getText()));
                programme.setDescription(descriptionField.getText());
                programme.setDatePrg(new Date()); // Date actuelle
                
                // Enregistrer le programme
                boolean success;
                if (editMode) {
                    success = programmeService.modifier(programme);
                } else {
                    success = programmeService.ajouter(programme);
                }
                
                if (success) {
                    // Mettre à jour la vue principale si nécessaire
                    if (mainController != null) {
                        mainController.refreshData();
                    }
                    
                    // Fermer la fenêtre
                    closeWindow();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement", 
                            "L'opération a échoué. Veuillez réessayer.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de l'enregistrement", e.getMessage());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de format", "Format incorrect", 
                        "Veuillez entrer un nombre valide pour la durée.");
            }
        }
    }
    
    /**
     * Gère l'action du bouton Annuler
     */
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    /**
     * Valide les entrées du formulaire
     * @return true si toutes les entrées sont valides, false sinon
     */
    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();
        
        if (nomProgrammeField.getText().trim().isEmpty()) {
            errorMessage.append("Le nom du programme ne peut pas être vide.\n");
        }
        if (typeComboBox.getValue() == null) {
            errorMessage.append("Veuillez sélectionner un type de programme.\n");
        }
        if (nationaliteComboBox.getValue() == null) {
            errorMessage.append("Veuillez sélectionner une nationalité.\n");
        }
        if (dureeField.getText().trim().isEmpty()) {
            errorMessage.append("La durée ne peut pas être vide.\n");
        }
        
        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Veuillez corriger les erreurs suivantes:", 
                    errorMessage.toString());
            return false;
        }
        
        return true;
    }
    
    /**
     * Ferme la fenêtre courante
     */
    private void closeWindow() {
        Stage stage = (Stage) nomProgrammeField.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Affiche une boîte de dialogue d'alerte
     * @param type Le type d'alerte
     * @param title Le titre de l'alerte
     * @param header L'en-tête de l'alerte
     * @param content Le contenu de l'alerte
     */
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    private void handleTranslate() {
        try {
            // Traduire tous les textes
            Translation titleTranslation = TranslateService.getInstance().translate(
                titleLabel.getText(),
                Translate.TranslateOption.sourceLanguage("fr"),
                Translate.TranslateOption.targetLanguage("en")
            );
            titleLabel.setText(titleTranslation.getTranslatedText());
            
            if (!nomProgrammeField.getText().isEmpty()) {
                Translation nomTranslation = TranslateService.getInstance().translate(
                    nomProgrammeField.getText(),
                    Translate.TranslateOption.sourceLanguage("fr"),
                    Translate.TranslateOption.targetLanguage("en")
                );
                nomProgrammeField.setText(nomTranslation.getTranslatedText());
            }
            
            if (!descriptionField.getText().isEmpty()) {
                Translation descTranslation = TranslateService.getInstance().translate(
                    descriptionField.getText(),
                    Translate.TranslateOption.sourceLanguage("fr"),
                    Translate.TranslateOption.targetLanguage("en")
                );
                descriptionField.setText(descTranslation.getTranslatedText());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de traduction", "Impossible de traduire le texte", e.getMessage());
        }
    }
}