package com.mila.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.mila.model.Programme;
import com.mila.model.ProgrammeEchange;
import com.mila.controller.PostulationFormController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ProgramCardController {
    @FXML private Label titleLabel;
    @FXML private Label typeLabel;
    @FXML private Label nationalityLabel;
    @FXML private Label placesLabel;
    
    private ProgrammeEchange programme;
    
    public void setProgramme(ProgrammeEchange programme) {
        this.programme = programme;
        titleLabel.setText(programme.getNomProgramme());
        typeLabel.setText(programme.getType().toString());
        nationalityLabel.setText(programme.getNationalite().toString());
        placesLabel.setText(String.valueOf(programme.getDuree()));
    }
    
    @FXML
    private void handleApply() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostulationForm.fxml"));
            Scene scene = new Scene(loader.load());
            
            PostulationFormController controller = loader.getController();
            controller.setProgramme(programme);
            
            Stage stage = new Stage();
            stage.setTitle("Postuler - " + programme.getNomProgramme());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}