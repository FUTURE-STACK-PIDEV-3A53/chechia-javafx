package org.example.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import org.example.model.Reservation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ReservationFXController implements Initializable {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, Integer> idColumn;
    @FXML private TableColumn<Reservation, Integer> eventIdColumn;
    @FXML private TableColumn<Reservation, Integer> userIdColumn;
    @FXML private TableColumn<Reservation, Integer> nombrePersonnesColumn;
    @FXML private TableColumn<Reservation, String> dateReservationColumn;

    @FXML private TextField eventIdField;
    @FXML private TextField userIdField;
    @FXML private TextField nombrePersonnesField;
    @FXML private DatePicker dateReservationPicker;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    @FXML private Button clearButton;

    private final ReservationController controller = new ReservationController();
    private final ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    private Reservation selectedReservation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadReservations();

        // Désactiver les boutons de modification et suppression jusqu'à sélection
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        // Ajouter un écouteur de sélection au TableView
        reservationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedReservation = newSelection;
                fillForm(newSelection);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                selectedReservation = null;
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        eventIdColumn.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        nombrePersonnesColumn.setCellValueFactory(new PropertyValueFactory<>("nombrePersonnes"));
        dateReservationColumn.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));

        reservationTable.setItems(reservationList);
    }

    private void loadReservations() {
        reservationList.clear();
        try {
            reservationList.addAll(controller.getAllReservations());
        } catch (Exception e) {
            showError("Erreur lors du chargement des réservations: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        if (validateInput()) {
            try {
                int eventId = Integer.parseInt(eventIdField.getText());
                int userId = Integer.parseInt(userIdField.getText());
                int nombrePersonnes = Integer.parseInt(nombrePersonnesField.getText());
                String date = dateReservationPicker.getValue().format(DateTimeFormatter.ISO_DATE);

                Reservation reservation = new Reservation(eventId, userId, nombrePersonnes, date);
                
                boolean success = controller.createReservation(reservation);
                if (success) {
                    showInfo("Réservation ajoutée avec succès");
                    loadReservations();
                    clearForm();
                } else {
                    showError("Impossible d'ajouter la réservation");
                }
            } catch (NumberFormatException e) {
                showError("Veuillez entrer des valeurs numériques valides pour les IDs et le nombre de personnes");
            } catch (Exception e) {
                showError("Erreur lors de l'ajout: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedReservation != null && validateInput()) {
            try {
                int eventId = Integer.parseInt(eventIdField.getText());
                int userId = Integer.parseInt(userIdField.getText());
                int nombrePersonnes = Integer.parseInt(nombrePersonnesField.getText());
                String date = dateReservationPicker.getValue().format(DateTimeFormatter.ISO_DATE);

                Reservation updatedReservation = new Reservation(
                    selectedReservation.getId(),
                    eventId,
                    userId,
                    nombrePersonnes,
                    date
                );
                
                boolean success = controller.updateReservation(updatedReservation);
                if (success) {
                    showInfo("Réservation mise à jour avec succès");
                    loadReservations();
                    clearForm();
                    selectedReservation = null;
                } else {
                    showError("Impossible de mettre à jour la réservation");
                }
            } catch (NumberFormatException e) {
                showError("Veuillez entrer des valeurs numériques valides pour les IDs et le nombre de personnes");
            } catch (Exception e) {
                showError("Erreur lors de la mise à jour: " + e.getMessage());
            }
        } else {
            showError("Veuillez sélectionner une réservation à modifier");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedReservation != null) {
            try {
                boolean success = controller.deleteReservation(selectedReservation.getId());
                if (success) {
                    showInfo("Réservation supprimée avec succès");
                    loadReservations();
                    clearForm();
                    selectedReservation = null;
                } else {
                    showError("Impossible de supprimer la réservation");
                }
            } catch (Exception e) {
                showError("Erreur lors de la suppression: " + e.getMessage());
            }
        } else {
            showError("Veuillez sélectionner une réservation à supprimer");
        }
    }

    @FXML
    private void handleRefresh() {
        loadReservations();
    }

    @FXML
    private void handleClear() {
        clearForm();
        selectedReservation = null;
        reservationTable.getSelectionModel().clearSelection();
    }

    private void fillForm(Reservation reservation) {
        eventIdField.setText(String.valueOf(reservation.getEventId()));
        userIdField.setText(String.valueOf(reservation.getUserId()));
        nombrePersonnesField.setText(String.valueOf(reservation.getNombrePersonnes()));
        
        try {
            LocalDate date = LocalDate.parse(reservation.getDateReservation());
            dateReservationPicker.setValue(date);
        } catch (Exception e) {
            dateReservationPicker.setValue(null);
        }
    }

    private void clearForm() {
        eventIdField.clear();
        userIdField.clear();
        nombrePersonnesField.clear();
        dateReservationPicker.setValue(null);
        
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (eventIdField.getText().trim().isEmpty()) {
            errorMessage.append("L'ID de l'événement est requis\n");
        } else {
            try {
                int eventId = Integer.parseInt(eventIdField.getText().trim());
                if (eventId <= 0) {
                    errorMessage.append("L'ID de l'événement doit être positif\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("L'ID de l'événement doit être un nombre\n");
            }
        }

        if (userIdField.getText().trim().isEmpty()) {
            errorMessage.append("L'ID de l'utilisateur est requis\n");
        } else {
            try {
                int userId = Integer.parseInt(userIdField.getText().trim());
                if (userId <= 0) {
                    errorMessage.append("L'ID de l'utilisateur doit être positif\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("L'ID de l'utilisateur doit être un nombre\n");
            }
        }

        if (nombrePersonnesField.getText().trim().isEmpty()) {
            errorMessage.append("Le nombre de personnes est requis\n");
        } else {
            try {
                int nombrePersonnes = Integer.parseInt(nombrePersonnesField.getText().trim());
                if (nombrePersonnes <= 0) {
                    errorMessage.append("Le nombre de personnes doit être positif\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Le nombre de personnes doit être un nombre\n");
            }
        }

        if (dateReservationPicker.getValue() == null) {
            errorMessage.append("La date de réservation est requise\n");
        }

        if (errorMessage.length() > 0) {
            showError(errorMessage.toString());
            return false;
        }

        return true;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 