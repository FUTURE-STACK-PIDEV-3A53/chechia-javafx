package org.example.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.example.model.Event;
import org.example.model.EventDAO;
import org.example.model.Reservation;
import org.example.model.ReservationDAO;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

public class UserReservationsController {

    public static class ReservationDisplay {
        private final Reservation reservation;
        private final SimpleStringProperty eventName;

        public ReservationDisplay(Reservation reservation, String eventName) {
            this.reservation = reservation;
            this.eventName = new SimpleStringProperty(eventName);
        }

        public Reservation getReservation() {
            return reservation;
        }

        public int getId() {
            return reservation.getId();
        }

        public String getEventName() {
            return eventName.get();
        }

        public SimpleStringProperty eventNameProperty() {
            return eventName;
        }

        public int getNb_personne() {
            return reservation.getNb_personne();
        }

        public String getNum_tel() {
            return reservation.getNum_tel();
        }
    }

    @FXML private TableView<ReservationDisplay> reservationTable;
    @FXML private TableColumn<ReservationDisplay, String> reservationEventColumn;
    @FXML private TableColumn<ReservationDisplay, Integer> reservationNbColumn;
    @FXML private TableColumn<ReservationDisplay, String> reservationTelColumn;
    @FXML private TableColumn<ReservationDisplay, Void> reservationActionColumn;
    @FXML private BorderPane rootPane;

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final EventDAO eventDAO = new EventDAO();
    private final ObservableList<ReservationDisplay> reservationList = FXCollections.observableArrayList();
    private int currentUserId = 1; // À remplacer par l'ID de l'utilisateur connecté
    private boolean darkTheme = true;

    @FXML
    public void initialize() {
        setupReservationTable();
        loadReservations();
    }

    private void setupReservationTable() {
        reservationEventColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        reservationNbColumn.setCellValueFactory(new PropertyValueFactory<>("nb_personne"));
        reservationTelColumn.setCellValueFactory(new PropertyValueFactory<>("num_tel"));
        reservationTable.setItems(reservationList);
        addReservationActionButton();
    }

    private void loadReservations() {
        reservationList.clear();
        List<Reservation> userReservations = reservationDAO.afficherReservations()
            .stream()
            .filter(r -> r.getUserID() == currentUserId)
            .collect(Collectors.toList());

        for (Reservation r : userReservations) {
            Event event = eventDAO.findEventById(r.getEventId());
            String eventName = (event != null) ? event.getNomEvent() : "Événement inconnu (ID: " + r.getEventId() + ")";
            reservationList.add(new ReservationDisplay(r, eventName));
        }
    }

    private void addReservationActionButton() {
        reservationActionColumn.setCellFactory(param -> new TableCell<ReservationDisplay, Void>() {
            private final Button btn = new Button("Annuler");
            {
                btn.setOnAction((ActionEvent event) -> {
                    ReservationDisplay selectedDisplay = getTableView().getItems().get(getIndex());
                    handleCancelReservation(selectedDisplay.getReservation());
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void handleCancelReservation(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Annulation");
        alert.setHeaderText("Annuler la réservation ?");
        alert.setContentText("Voulez-vous vraiment annuler cette réservation ?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = reservationDAO.supprimerReservationParId(reservation.getId());
                if (success) {
                    showInfo("Réservation annulée.");
                    loadReservations();
                } else {
                    showError("Erreur lors de l'annulation de la réservation.");
                }
            }
        });
    }

    @FXML
    private void handleClose(ActionEvent event) {
        // Suppose que le bouton est dans la scène
        if (event.getSource() instanceof Button) {
            ((Button) event.getSource()).getScene().getWindow().hide();
        }
    }

    @FXML
    private void handleToggleTheme() {
        if (rootPane != null) {
            if (darkTheme) {
                rootPane.getStylesheets().clear();
                rootPane.getStylesheets().add(getClass().getResource("/light.css").toExternalForm());
            } else {
                rootPane.getStylesheets().clear();
                rootPane.getStylesheets().add(getClass().getResource("/netflix.css").toExternalForm());
            }
            darkTheme = !darkTheme;
        }
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}