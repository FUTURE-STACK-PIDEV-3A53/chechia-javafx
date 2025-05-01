package chechia.tn.controllers.omar;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import  chechia.tn.entities.Event;

import chechia.tn.entities.Reservation;
import chechia.tn.test.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;


public class EventFXController implements Initializable {
    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, Integer> idColumn;
    @FXML private TableColumn<Event, String> nomColumn;
    @FXML private TableColumn<Event, String> lieuColumn;
    @FXML private TableColumn<Event, String> dateColumn;
    @FXML private TableColumn<Event, String> typeColumn;
    @FXML private TableColumn<Event, Double> montantColumn;
    @FXML private TableColumn<Event, Integer> userIdColumn;
    @FXML private TableColumn<Event, Void> reserverColumn;

    @FXML private TextField nomField;
    @FXML private TextField lieuField;
    @FXML private DatePicker dateField;
    @FXML private TextField typeField;
    @FXML private TextField montantField;
    @FXML private TextField userIdField;

    private final EventController controller = new EventController();
    private final ReservationController reservationController = new ReservationController();
    private final ObservableList<Event> eventList = FXCollections.observableArrayList();
    private Event selectedEvent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupTableColumns();
            setupReservationColumn();
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

    private void setupReservationColumn() {
        Callback<TableColumn<Event, Void>, TableCell<Event, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Event, Void> call(final TableColumn<Event, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Réserver");
                    {
                        btn.setStyle("-fx-background-color: #e50914; -fx-text-fill: white; -fx-font-weight: BOLD; -fx-cursor: hand;");

                        btn.setOnAction(event -> {
                            Event eventData = getTableView().getItems().get(getIndex());
                            handleReservation(eventData);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        reserverColumn.setCellFactory(cellFactory);
    }

    private void handleReservation(Event event) {
        try {
            // Première dialog pour demander le nombre de personnes
            TextInputDialog dialogNombre = new TextInputDialog("1");
            dialogNombre.setTitle("Nouvelle réservation");
            dialogNombre.setHeaderText("Réservation pour: " + event.getNomEvent());
            dialogNombre.setContentText("Nombre de personnes:");

            dialogNombre.showAndWait().ifPresent(nombreStr -> {
                try {
                    int nbPersonne = Integer.parseInt(nombreStr);
                    if (nbPersonne <= 0) {
                        showError("Le nombre de personnes doit être supérieur à zéro");
                        return;
                    }

                    // Deuxième dialog pour demander le numéro de téléphone
                    TextInputDialog dialogTel = new TextInputDialog("");
                    dialogTel.setTitle("Numéro de téléphone");
                    dialogTel.setHeaderText("Entrez un numéro de téléphone");
                    dialogTel.setContentText("Numéro de téléphone:");

                    dialogTel.showAndWait().ifPresent(numTel -> {
                        if (numTel.isEmpty()) {
                            showError("Le numéro de téléphone est requis");
                            return;
                        }

                        // Afficher les informations avant de créer la réservation
                        System.out.println("Tentative de création de réservation:");
                        System.out.println("  - ID d'événement: " + event.getId());
                        System.out.println("  - Nom d'événement: " + event.getNomEvent());
                        System.out.println("  - Nombre de personnes: " + nbPersonne);
                        System.out.println("  - Numéro de téléphone: " + numTel);

                        // Utiliser l'ID utilisateur actuel (à adapter selon votre système d'authentification)
                        // Pour l'instant nous utilisons 1 comme ID par défaut
                        int userID = 1;

                        // Créer la réservation avec les nouvelles propriétés
                        Reservation reservation = new Reservation(event.getId(), userID, nbPersonne, numTel);

                        // Afficher les détails de la réservation
                        System.out.println("Détails de la réservation à créer:");
                        System.out.println(reservation.toString());

                        boolean success = reservationController.createReservation(reservation);

                        if (success) {
                            showInfo("Réservation créée avec succès pour l'événement: " + event.getNomEvent());
                        } else {
                            showError("Erreur lors de la création de la réservation dans la base de données.\n" +
                                    "Vérifiez la connexion à la base de données et les logs pour plus d'informations.");

                            // Afficher un message pour aider à résoudre le problème
                            System.out.println("SUGGESTION: Vérifiez que la table 'reservation' existe dans la base de données 'chachia'.");
                            System.out.println("Vous pouvez exécuter le script SQL dans src/main/resources/create_tables.sql");
                        }
                    });
                } catch (NumberFormatException e) {
                    showError("Veuillez entrer un nombre valide pour le nombre de personnes");
                } catch (Exception e) {
                    showError("Erreur inattendue: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            showError("Erreur lors de la réservation: " + e.getMessage());
            e.printStackTrace();
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

            // Vérifier que la localisation ne contient pas de chiffres
            if (!ValidationUtils.isValidLocalisation(lieu)) {
                showError("Le champ Lieu ne doit pas contenir de chiffres");
                return;
            }

            // Vérifier que la date est dans le futur
            if (dateField.getValue() != null) {
                java.time.LocalDate eventDate = dateField.getValue();
                java.time.LocalDate today = java.time.LocalDate.now();

                if (eventDate.isBefore(today)) {
                    showError("La date de l'événement doit être dans le futur.\nDate sélectionnée: " + eventDate + "\nAujourd'hui: " + today);
                    return;
                }
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

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}