package org.example.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import org.example.DBConnection;
import org.example.model.Reservation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.utils.QRCodeGenerator;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;


public class ReservationFXController implements Initializable {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, Integer> idColumn;
    @FXML private TableColumn<Reservation, Integer> eventIdColumn;
    @FXML private TableColumn<Reservation, Integer> userIdColumn;
    @FXML private TableColumn<Reservation, Integer> nombrePersonnesColumn;
    @FXML private TableColumn<Reservation, String> num_telColumn;

    @FXML private TextField eventIdField;
    @FXML private TextField userIdField;
    @FXML private TextField nombrePersonnesField;
    @FXML private TextField num_telField;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    @FXML private Button clearButton;
    @FXML private Button verifyButton;

    @FXML private ImageView qrCodeImageView; // Space to display the QR code

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
        num_telColumn.setCellValueFactory(new PropertyValueFactory<>("num_tel"));

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
                String numTel = num_telField.getText();

                // Afficher les valeurs pour le debugging
                System.out.println("Tentative d'ajout d'une réservation avec les valeurs suivantes:");
                System.out.println("- Event ID: " + eventId);
                System.out.println("- User ID: " + userId);
                System.out.println("- Nombre de personnes: " + nombrePersonnes);
                System.out.println("- Numéro de téléphone: " + numTel);

                Reservation reservation = new Reservation(eventId, userId, nombrePersonnes, numTel);
                
                boolean success = controller.createReservation(reservation);
                if (success) {
                    showInfo("Réservation ajoutée avec succès");
                    loadReservations();
                    clearForm();
                } else {
                    showError("Impossible d'ajouter la réservation. Vérifiez que l'ID d'événement et l'ID utilisateur existent bien dans la base de données.");
                    System.out.println("ÉCHEC: La méthode createReservation a retourné false");
                }
            } catch (NumberFormatException e) {
                showError("Veuillez entrer des valeurs numériques valides pour les IDs et le nombre de personnes");
            } catch (Exception e) {
                String errorMsg = "Erreur lors de l'ajout: " + e.getMessage();
                showError(errorMsg);
                System.out.println("EXCEPTION DÉTAILLÉE: " + e.toString());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedReservation != null && validateInput()) {
            try {
                // Récupérer l'ancien nombre de personnes comme critère
                int oldNbPersonnes = selectedReservation.getNb_personne();
                
                // Récupérer les nouvelles valeurs
                int eventId = Integer.parseInt(eventIdField.getText());
                int userId = Integer.parseInt(userIdField.getText());
                int nouveauNbPersonnes = Integer.parseInt(nombrePersonnesField.getText());
                String numTel = num_telField.getText();

                // Confirmation avant la modification
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmation de modification");
                confirmAlert.setHeaderText("Modifier les réservations");
                confirmAlert.setContentText("Êtes-vous sûr de vouloir modifier toutes les réservations ayant " + oldNbPersonnes + " personne(s) ?");
                
                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        // Créer une réservation avec les nouvelles valeurs
                        Reservation updatedReservation = new Reservation(
                            selectedReservation.getId(),
                            eventId,
                            userId,
                            nouveauNbPersonnes,
                            numTel
                        );
                        
                        // Modifier toutes les réservations ayant l'ancien nombre de personnes
                        boolean success = controller.updateReservationByNbPersonne(oldNbPersonnes, updatedReservation);
                        
                        if (success) {
                            showInfo("Réservation(s) mise(s) à jour avec succès");
                            loadReservations();
                            clearForm();
                            selectedReservation = null;
                        } else {
                            showError("Impossible de mettre à jour la/les réservation(s)");
                        }
                    }
                });
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
                // Récupérer le nombre de personnes
                int nbPersonnes = selectedReservation.getNb_personne();
                
                // Confirmation avant la suppression
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmation de suppression");
                confirmAlert.setHeaderText("Supprimer les réservations");
                confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer toutes les réservations ayant " + nbPersonnes + " personne(s) ?");
                
                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        // Utiliser le nombre de personnes comme critère de suppression
                        boolean success = controller.deleteReservation(nbPersonnes);
                        
                        if (success) {
                            showInfo("Réservation(s) supprimée(s) avec succès");
                            loadReservations();
                            clearForm();
                            selectedReservation = null;
                        } else {
                            showError("Impossible de supprimer la/les réservation(s)");
                        }
                    }
                });
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

    @FXML
    private void handleVerifyIds() {
        try {
            StringBuilder message = new StringBuilder();
            
            // Récupérer les événements disponibles
            message.append("=== ÉVÉNEMENTS DISPONIBLES ===\n");
            try (Connection conn = DBConnection.getConnection()) {
                if (conn != null) {
                    String sql = "SELECT id, nom_event FROM event";
                    try (PreparedStatement stmt = conn.prepareStatement(sql);
                         ResultSet rs = stmt.executeQuery()) {
                        
                        while (rs.next()) {
                            message.append("ID: ").append(rs.getInt("id"))
                                  .append(" - ").append(rs.getString("nom_event"))
                                  .append("\n");
                        }
                    }
                    
                    // Récupérer les utilisateurs disponibles
                    message.append("\n=== UTILISATEURS DISPONIBLES ===\n");
                    String userSql = "SELECT userID, nom FROM user";
                    try (PreparedStatement stmt = conn.prepareStatement(userSql);
                         ResultSet rs = stmt.executeQuery()) {
                        
                        while (rs.next()) {
                            message.append("ID: ").append(rs.getInt("userID"))
                                  .append(" - ").append(rs.getString("nom"))
                                  .append("\n");
                        }
                    }
                } else {
                    message.append("Impossible de se connecter à la base de données");
                }
            }
            
            // Afficher les informations
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informations sur les IDs");
            alert.setHeaderText("IDs disponibles dans la base de données");
            alert.setContentText(message.toString());
            alert.getDialogPane().setPrefWidth(500);
            alert.showAndWait();
            
        } catch (Exception e) {
            showError("Erreur lors de la récupération des IDs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDiagnostic() {
        StringBuilder report = new StringBuilder();
        report.append("=== RAPPORT DE DIAGNOSTIC ===\n\n");
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                report.append("ERREUR: Impossible de se connecter à la base de données\n");
            } else {
                report.append("✓ Connexion à la base de données établie\n");
                
                // Vérifier les tables existantes
                DatabaseMetaData metaData = conn.getMetaData();
                report.append("\n=== TABLES EXISTANTES ===\n");
                try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                    boolean foundReservation = false;
                    boolean foundEvent = false;
                    boolean foundUser = false;
                    
                    while (tables.next()) {
                        String tableName = tables.getString("TABLE_NAME");
                        report.append("- ").append(tableName).append("\n");
                        
                        if (tableName.equalsIgnoreCase("reservation")) foundReservation = true;
                        if (tableName.equalsIgnoreCase("event")) foundEvent = true;
                        if (tableName.equalsIgnoreCase("user")) foundUser = true;
                    }
                    
                    report.append("\n=== VÉRIFICATION DES TABLES REQUISES ===\n");
                    report.append(foundReservation ? "✓ Table 'reservation' trouvée\n" : "❌ Table 'reservation' MANQUANTE\n");
                    report.append(foundEvent ? "✓ Table 'event' trouvée\n" : "❌ Table 'event' MANQUANTE\n");
                    report.append(foundUser ? "✓ Table 'user' trouvée\n" : "❌ Table 'user' MANQUANTE\n");
                    
                    // Vérifier le contenu de la table event
                    if (foundEvent) {
                        report.append("\n=== ÉVÉNEMENTS DISPONIBLES ===\n");
                        try (Statement stmt = conn.createStatement();
                             ResultSet rs = stmt.executeQuery("SELECT id, nom_event FROM event")) {
                            
                            int eventCount = 0;
                            while (rs.next()) {
                                eventCount++;
                                report.append("ID: ").append(rs.getInt("id"))
                                      .append(" - ").append(rs.getString("nom_event"))
                                      .append("\n");
                            }
                            
                            if (eventCount == 0) {
                                report.append("❌ AUCUN ÉVÉNEMENT TROUVÉ - Vous devez créer des événements avant d'ajouter des réservations\n");
                            }
                        }
                    }
                    
                    // Vérifier le contenu de la table user
                    if (foundUser) {
                        report.append("\n=== UTILISATEURS DISPONIBLES ===\n");
                        try (Statement stmt = conn.createStatement();
                             ResultSet rs = stmt.executeQuery("SELECT userID, nom FROM user")) {
                            
                            int userCount = 0;
                            while (rs.next()) {
                                userCount++;
                                report.append("ID: ").append(rs.getInt("userID"))
                                      .append(" - ").append(rs.getString("nom"))
                                      .append("\n");
                            }
                            
                            if (userCount == 0) {
                                report.append("❌ AUCUN UTILISATEUR TROUVÉ - Vous devez créer des utilisateurs avant d'ajouter des réservations\n");
                            }
                        }
                    }
                    
                    // Vérifier la structure de la table reservation
                    if (foundReservation) {
                        report.append("\n=== STRUCTURE DE LA TABLE RESERVATION ===\n");
                        try (ResultSet columns = metaData.getColumns(null, null, "reservation", null)) {
                            while (columns.next()) {
                                String columnName = columns.getString("COLUMN_NAME");
                                String columnType = columns.getString("TYPE_NAME");
                                report.append("- ").append(columnName).append(" (").append(columnType).append(")\n");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            report.append("\nERREUR lors du diagnostic: ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }
        
        // Afficher le rapport
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Diagnostic de la base de données");
        alert.setHeaderText("Rapport de diagnostic");
        alert.setContentText(report.toString());
        alert.getDialogPane().setPrefHeight(600);
        alert.getDialogPane().setPrefWidth(800);
        alert.showAndWait();
    }

    @FXML
    private void handleInsertTestReservation() {
        try {
            // Créer une connexion directe
            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) {
                    showError("Impossible de se connecter à la base de données");
                    return;
                }
                
                StringBuilder log = new StringBuilder("Début du processus d'insertion test\n");
                
                // 1. Vérifier si l'utilisateur de test existe, sinon le créer
                log.append("Vérification de l'utilisateur de test...\n");
                int userId = -1;
                try (PreparedStatement stmt = conn.prepareStatement(
                        "SELECT userID FROM user WHERE userID = 1")) {
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        userId = rs.getInt("userID");
                        log.append("Utilisateur trouvé avec ID: ").append(userId).append("\n");
                    } else {
                        log.append("Création d'un utilisateur de test...\n");
                        try (PreparedStatement insertStmt = conn.prepareStatement(
                                "INSERT INTO user (nom, prenom, email, password, phoneNumber) VALUES (?, ?, ?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS)) {
                            insertStmt.setString(1, "TestUser");
                            insertStmt.setString(2, "Test");
                            insertStmt.setString(3, "test@test.com");
                            insertStmt.setString(4, "password");
                            insertStmt.setString(5, "123456789");
                            
                            int rows = insertStmt.executeUpdate();
                            log.append(rows).append(" utilisateur(s) créé(s)\n");
                            
                            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                userId = generatedKeys.getInt(1);
                                log.append("Utilisateur créé avec ID: ").append(userId).append("\n");
                            }
                        }
                    }
                }
                
                // 2. Vérifier si l'événement de test existe, sinon le créer
                log.append("Vérification de l'événement de test...\n");
                int eventId = -1;
                try (PreparedStatement stmt = conn.prepareStatement(
                        "SELECT id FROM event WHERE id = 1")) {
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        eventId = rs.getInt("id");
                        log.append("Événement trouvé avec ID: ").append(eventId).append("\n");
                    } else {
                        log.append("Création d'un événement de test...\n");
                        try (PreparedStatement insertStmt = conn.prepareStatement(
                                "INSERT INTO event (nom_event, localisation_event, date_event, type, montant, userID) VALUES (?, ?, ?, ?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS)) {
                            insertStmt.setString(1, "Event Test");
                            insertStmt.setString(2, "Lieu Test");
                            insertStmt.setString(3, "2024-06-01");
                            insertStmt.setString(4, "Type Test");
                            insertStmt.setDouble(5, 100.0);
                            insertStmt.setInt(6, userId);
                            
                            int rows = insertStmt.executeUpdate();
                            log.append(rows).append(" événement(s) créé(s)\n");
                            
                            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                eventId = generatedKeys.getInt(1);
                                log.append("Événement créé avec ID: ").append(eventId).append("\n");
                            }
                        }
                    }
                }
                
                // 3. Insérer la réservation de test
                if (userId > 0 && eventId > 0) {
                    log.append("Création d'une réservation de test...\n");
                    String insertSql = "INSERT INTO reservation (event_id, userID, nb_personne, num_tel) VALUES (?, ?, ?, ?)";
                    log.append("SQL: ").append(insertSql).append("\n");
                    log.append("Paramètres: eventId=").append(eventId)
                         .append(", userId=").append(userId)
                         .append(", nbPersonnes=2, numTel=123456789\n");
                    
                    try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                        stmt.setInt(1, eventId);
                        stmt.setInt(2, userId);
                        stmt.setInt(3, 2);
                        stmt.setString(4, "123456789");
                        
                        int rows = stmt.executeUpdate();
                        log.append(rows).append(" réservation(s) créée(s)\n");
                        
                        if (rows > 0) {
                            showInfo("Réservation de test créée avec succès!\nVous pouvez maintenant cliquer sur ACTUALISER pour voir la réservation");
                            log.append("SUCCÈS: Réservation créée\n");
                        } else {
                            showError("Aucune réservation n'a été créée");
                            log.append("ÉCHEC: Aucune ligne insérée\n");
                        }
                    } catch (Exception e) {
                        log.append("ERREUR lors de l'insertion de la réservation: ").append(e.getMessage()).append("\n");
                        showError("Erreur lors de l'insertion de la réservation: " + e.getMessage());
                    }
                } else {
                    log.append("Impossible de créer la réservation: userID ou eventId invalide\n");
                    showError("Impossible de créer les données de test: userID ou eventId invalide");
                }
                
                // Afficher le log complet dans la console
                System.out.println("=========== LOG D'INSERTION TEST ===========");
                System.out.println(log.toString());
                System.out.println("===========================================");
            }
        } catch (Exception e) {
            showError("Erreur lors de l'opération: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleGeneratePdf() {
        if (selectedReservation != null) {
            try {
                // Gather information about the selected reservation
                String eventInfo = String.format(
                    "Reservation ID: %d\nEvent ID: %d\nUser ID: %d\nNombre de Personnes: %d\nAvec le numero: %s",
                    selectedReservation.getId(),
                    selectedReservation.getEventId(),
                    selectedReservation.getUserId(),
                    selectedReservation.getNombrePersonnes(),
                    selectedReservation.getNum_tel()
                );

                // File path for the PDF
                String filePath = "reservation_details.pdf";

                // Create a PDF document
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                // Add the reservation details to the PDF
                document.add(new Paragraph("Détails de la Réservation"));
                document.add(new Paragraph(eventInfo));

                document.close();

                showInfo("PDF généré avec succès : " + filePath);
                System.out.println("PDF généré avec les informations de la réservation.");
            } catch (DocumentException | java.io.IOException e) {
                showError("Erreur lors de la génération du PDF: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showError("Veuillez sélectionner une réservation pour générer un PDF.");
        }
    }

    @FXML
    private void handleGenerateQrCode() {
        if (selectedReservation != null) {
            try {
                // Gather information about the selected reservation
                String eventInfo = String.format(
                    "Event ID: %d\nNombre de Personnes: %d\nAvec le noméro: %s",
                    selectedReservation.getEventId(),
                    selectedReservation.getNombrePersonnes(),
                    selectedReservation.getNum_tel()
                );

                // File path for the QR code
                String filePath = "qr_code.png";

                // Generate the QR code using the event information
                QRCodeGenerator.generateQRCode(eventInfo, filePath);

                // Load the generated QR code into the ImageView
                Image qrCodeImage = new Image("file:" + filePath);
                qrCodeImageView.setImage(qrCodeImage);

                System.out.println("QR Code généré et affiché avec les informations de l'événement.");
            } catch (Exception e) {
                showError("Erreur lors de la génération du QR Code: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showError("Veuillez sélectionner une réservation pour générer un QR Code.");
        }
    }

    private void fillForm(Reservation reservation) {
        eventIdField.setText(String.valueOf(reservation.getEventId()));
        userIdField.setText(String.valueOf(reservation.getUserId()));
        nombrePersonnesField.setText(String.valueOf(reservation.getNombrePersonnes()));
        num_telField.setText(reservation.getNum_tel());
    }

    private void clearForm() {
        eventIdField.clear();
        userIdField.clear();
        nombrePersonnesField.clear();
        nombrePersonnesField.clear();
        num_telField.clear();
        
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
                } else {
                    // Vérifier que la date de l'événement est dans le futur
                    try (Connection conn = DBConnection.getConnection()) {
                        if (conn != null) {
                            String sql = "SELECT date_event FROM event WHERE id = ?";
                            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                                stmt.setInt(1, eventId);
                                try (ResultSet rs = stmt.executeQuery()) {
                                    if (rs.next()) {
                                        String dateStr = rs.getString("date_event");
                                        java.time.LocalDate eventDate = java.time.LocalDate.parse(dateStr);
                                        java.time.LocalDate today = java.time.LocalDate.now();
                                        
                                        if (eventDate.isBefore(today)) {
                                            errorMessage.append("La date de l'événement doit être dans le futur.\n");
                                            errorMessage.append("Date de l'événement: ").append(eventDate).append(", Aujourd'hui: ").append(today).append("\n");
                                        }
                                    } else {
                                        errorMessage.append("L'événement avec l'ID ").append(eventId).append(" n'existe pas\n");
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        errorMessage.append("Erreur lors de la vérification de la date de l'événement: ").append(e.getMessage()).append("\n");
                    }
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

        if (num_telField.getText().trim().isEmpty()) {
            errorMessage.append("Le numéro de téléphone est requis\n");
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