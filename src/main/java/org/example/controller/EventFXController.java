package org.example.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import org.example.model.Event;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class EventFXController implements Initializable {
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, Integer> idColumn;
    @FXML private TableColumn<Event, String> nomColumn;
    @FXML private TableColumn<Event, String> lieuColumn;
    @FXML private TableColumn<Event, String> dateColumn;
    @FXML private TableColumn<Event, String> typeColumn;
    @FXML private TableColumn<Event, Double> montantColumn;
    @FXML private TableColumn<Event, Integer> userIdColumn;

    @FXML private TextField nomField;
    @FXML private TextField lieuField;
    @FXML private DatePicker dateField;
    @FXML private TextField typeField;
    @FXML private TextField montantField;
    @FXML private TextField userIdField;

    private final EventController controller = new EventController();
    private final ObservableList<Event> eventList = FXCollections.observableArrayList();
    private Event selectedEvent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupTableColumns();
            loadEvents();
        } catch (Exception e) {
            showError("Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        try {
            idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
            nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomEventProperty());
            lieuColumn.setCellValueFactory(cellData -> cellData.getValue().localisationProperty());
            dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateEventProperty());
            typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
            montantColumn.setCellValueFactory(cellData -> cellData.getValue().montantProperty().asObject());
            userIdColumn.setCellValueFactory(cellData -> cellData.getValue().userIdProperty().asObject());
            
            eventTable.setItems(eventList);
        } catch (Exception e) {
            showError("Erreur lors de la configuration des colonnes: " + e.getMessage());
        }
    }

    private void loadEvents() {
        try {
            eventList.clear();
            eventList.addAll(controller.getAllEvents());
        } catch (Exception e) {
            showError("Erreur lors du chargement des événements: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        clearForm();
        selectedEvent = null;
    }

    @FXML
    private void handleShow() {
        loadEvents();
    }

    @FXML
    private void handleUpdate() {
        try {
            selectedEvent = eventTable.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                fillForm(selectedEvent);
            } else {
                showError("Sélectionnez un événement à modifier");
            }
        } catch (Exception e) {
            showError("Erreur lors de la modification: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        try {
            Event event = eventTable.getSelectionModel().getSelectedItem();
            if (event != null) {
                controller.deleteEvent(event.getId());
                loadEvents();
            } else {
                showError("Sélectionnez un événement à supprimer");
            }
        } catch (Exception e) {
            showError("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            String nom = nomField.getText();
            String lieu = lieuField.getText();
            String date = dateField.getValue() != null ? dateField.getValue().format(DateTimeFormatter.ISO_DATE) : "";
            String type = typeField.getText();
            double montant = Double.parseDouble(montantField.getText());
            int userId = Integer.parseInt(userIdField.getText());

            if (nom.isEmpty() || lieu.isEmpty() || date.isEmpty() || type.isEmpty()) {
                showError("Veuillez remplir tous les champs");
                return;
            }

            Event event = new Event(
                selectedEvent != null ? selectedEvent.getId() : 0,
                nom, lieu, date, type, montant, userId
            );

            if (selectedEvent != null) {
                controller.updateEvent(event);
            } else {
                controller.createEvent(event);
            }

            loadEvents();
            clearForm();
            selectedEvent = null;
        } catch (NumberFormatException e) {
            showError("Veuillez entrer des valeurs numériques valides pour le montant et l'ID utilisateur");
        } catch (Exception e) {
            showError("Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
        selectedEvent = null;
    }

    private void fillForm(Event event) {
        try {
            nomField.setText(event.getNomEvent());
            lieuField.setText(event.getLocalisation());
            dateField.setValue(LocalDate.parse(event.getDateEvent()));
            typeField.setText(event.getType());
            montantField.setText(String.valueOf(event.getMontant()));
            userIdField.setText(String.valueOf(event.getUserId()));
        } catch (Exception e) {
            showError("Erreur lors du remplissage du formulaire: " + e.getMessage());
        }
    }

    private void clearForm() {
        nomField.clear();
        lieuField.clear();
        dateField.setValue(null);
        typeField.clear();
        montantField.clear();
        userIdField.clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 