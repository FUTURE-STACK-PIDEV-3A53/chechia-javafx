package org.example.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

import org.example.model.Event;
import org.example.model.EventDAO;
import org.example.model.Reservation;
import org.example.model.ReservationDAO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class EventUserDashboardController {
    @FXML private HBox eventCarousel;
    @FXML private BorderPane netflixRoot;
    @FXML private Label eventCountLabel;
    @FXML private Label reservationCountLabel;
    @FXML private Label placesCountLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Button chatbotButton;
    private final ObservableList<Event> eventList = FXCollections.observableArrayList();

    private final EventDAO eventDAO = new EventDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    private int currentUserId = 1; // À remplacer par l'ID de l'utilisateur connecté
    private boolean darkTheme = true;

    @FXML
    public void initialize() {
        if (netflixRoot != null) {
            netflixRoot.getStylesheets().add(getClass().getResource("/netflix.css").toExternalForm());
        }
        updateStats();
        loadEvents();
        setupSortComboBox();
    }

    private void updateStats() {
        int eventCount = eventDAO.afficherEvents().size();
        List<Reservation> reservations = reservationDAO.afficherReservations();
        int userReservationCount = 0;
        int userPlacesCount = 0;
        for (Reservation r : reservations) {
            if (r.getUserID() == currentUserId) {
                userReservationCount++;
                userPlacesCount += r.getNb_personne();
            }
        }
        if (eventCountLabel != null) eventCountLabel.setText("Événements disponibles : " + eventCount);
        if (reservationCountLabel != null) reservationCountLabel.setText("Mes réservations : " + userReservationCount);
        if (placesCountLabel != null) placesCountLabel.setText("Total de places réservées : " + userPlacesCount);
    }

    private void loadEvents() {
        eventCarousel.getChildren().clear();
        eventList.clear();
        List<Event> events = eventDAO.afficherEvents();
        eventList.addAll(events);
        for (Event event : events) {
            VBox card = createEventCard(event);
            eventCarousel.getChildren().add(card);
        }
    }

    private VBox createEventCard(Event event) {
        VBox card = new VBox(10);
        card.getStyleClass().add("netflix-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(240);
        card.setPrefHeight(400);

        // StackPane pour alterner entre rectangle et QR code
        StackPane imagePane = new StackPane();
        Rectangle poster = new Rectangle(240, 240);
        poster.setArcWidth(18);
        poster.setArcHeight(18);
        poster.setFill(Color.web("#333"));
        imagePane.getChildren().add(poster);

        Label title = new Label(event.getNomEvent());
        title.getStyleClass().add("netflix-card-title");
        Label date = new Label(event.getDateEvent());
        date.getStyleClass().add("netflix-card-date");
        Label location = new Label(event.getLocalisation());
        location.getStyleClass().add("netflix-card-location");
        Label type = new Label(event.getType());
        type.getStyleClass().add("netflix-card-type");
        Label price = new Label(String.format("%.2f DT", event.getMontant()));
        price.getStyleClass().add("netflix-card-price");

        Button reserveBtn = new Button("Réserver");
        reserveBtn.getStyleClass().add("netflix-card-btn");
        reserveBtn.setOnAction(e -> handleReservation(event));

        Button qrBtn = new Button("QR Code");
        qrBtn.getStyleClass().add("netflix-card-btn");
        qrBtn.setOnAction(e -> {
            Image qrImage = generateEventQrCode(event);
            if (qrImage != null) {
                imagePane.getChildren().clear();
                ImageView qrView = new ImageView(qrImage);
                qrView.setFitWidth(240);
                qrView.setFitHeight(240);
                imagePane.getChildren().add(qrView);
            }
        });

        // Bouton de partage avec menu
        MenuButton shareBtn = new MenuButton("Partager");
        shareBtn.getStyleClass().add("netflix-card-btn");
        
        // Créer le texte de partage
        String shareText = String.format(
            "🎉 Événement: %s\n" +
            "📅 Date: %s\n" +
            "📍 Lieu: %s\n" +
            "🎭 Type: %s\n" +
            "💰 Prix: %.2f DT",
            event.getNomEvent(),
            event.getDateEvent(),
            event.getLocalisation(),
            event.getType(),
            event.getMontant()
        );
        
        // Options de partage
        MenuItem facebookItem = new MenuItem("Facebook");
        facebookItem.setOnAction(e -> shareToFacebook(shareText));
        
        MenuItem whatsappItem = new MenuItem("WhatsApp");
        whatsappItem.setOnAction(e -> shareToWhatsApp(shareText));
        
        MenuItem messengerItem = new MenuItem("Messenger");
        messengerItem.setOnAction(e -> shareToMessenger(shareText));
        
        MenuItem instagramItem = new MenuItem("Instagram");
        instagramItem.setOnAction(e -> shareToInstagram(shareText));
        
        shareBtn.getItems().addAll(facebookItem, whatsappItem, messengerItem, instagramItem);

        card.getChildren().addAll(imagePane, title, date, location, type, price, reserveBtn, qrBtn, shareBtn);
        return card;
    }

    private Image generateEventQrCode(Event event) {
        try {
            String data = event.getNomEvent()
                        + " | " + event.getDateEvent()
                        + " | " + event.getLocalisation()
                        + " | " + event.getType()
                        + " | " + event.getMontant() + " DT";
            int size = 240;
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size, hints);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return new Image(new ByteArrayInputStream(pngOutputStream.toByteArray()));
        } catch (Exception e) {
            showError("Erreur lors de la génération du QR code: " + e.getMessage());
            return null;
        }
    }

    private void handleReservation(Event event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Réserver un événement");
        dialog.setHeaderText("Nombre de personnes");
        dialog.setContentText("Veuillez entrer le nombre de personnes :");
        dialog.showAndWait().ifPresent(nbStr -> {
            try {
                int nb = Integer.parseInt(nbStr);
                String tel = "";
                TextInputDialog telDialog = new TextInputDialog();
                telDialog.setTitle("Téléphone");
                telDialog.setHeaderText("Numéro de téléphone");
                telDialog.setContentText("Veuillez entrer votre numéro de téléphone :");
                telDialog.showAndWait().ifPresent(telStr -> {
                    Reservation reservation = new Reservation(event.getId(), currentUserId, nb, telStr);
                    boolean success = reservationDAO.ajouterReservation(reservation);
                    if (success) {
                        showInfo("Réservation effectuée !");
                        updateStats();
                        showSmartNotification(event);
                        suggestSimilarEvent(event);
                    } else {
                        showError("Erreur lors de la réservation.");
                    }
                });
            } catch (NumberFormatException e) {
                showError("Veuillez entrer un nombre valide.");
            }
        });
    }

    private void showSmartNotification(Event event) {
        try {
            LocalDate eventDate = LocalDate.parse(event.getDateEvent(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            long days = LocalDate.now().until(eventDate, ChronoUnit.DAYS);
            if (days >= 0 && days <= 3) {
                showInfo("N'oubliez pas, votre événement '" + event.getNomEvent() + "' est dans " + days + " jour(s) !");
            }
        } catch (Exception ignored) {}
    }

    private void suggestSimilarEvent(Event event) {
        List<Event> allEvents = eventDAO.afficherEvents();
        for (Event e : allEvents) {
            if (e.getId() != event.getId() && e.getType().equalsIgnoreCase(event.getType())) {
                showInfo("Suggestion : Découvrez aussi l'événement '" + e.getNomEvent() + "' du même type ('" + e.getType() + "').");
                break;
            }
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Fermer la fenêtre ou revenir à la page de login
        ((Button) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    private void handleShowUserReservations(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserReservations.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Mes réservations");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/netflix.css").toExternalForm());
            stage.setScene(scene);
            stage.setMinWidth(700);
            stage.setMinHeight(500);
            stage.show();
        } catch (Exception e) {
            showError("Erreur lors de l'ouverture de la fenêtre des réservations: " + e.getMessage());
        }
    }

    @FXML
    private void handleToggleTheme(ActionEvent event) {
        if (netflixRoot != null) {
            if (darkTheme) {
                netflixRoot.getStylesheets().clear();
                netflixRoot.getStylesheets().add(getClass().getResource("/light.css").toExternalForm());
            } else {
                netflixRoot.getStylesheets().clear();
                netflixRoot.getStylesheets().add(getClass().getResource("/netflix.css").toExternalForm());
            }
            darkTheme = !darkTheme;
        }
    }

    @FXML
    private void handleOpenChatbot() {
        Stage chatbotStage = new Stage();
        chatbotStage.setTitle("Assistant Virtuel");
        
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #141414;");

        // Zone de chat avec style personnalisé
        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setPrefHeight(400);
        chatArea.setStyle("-fx-control-inner-background: #1f1f1f; " +
                         "-fx-text-fill: white; " +
                         "-fx-font-family: 'Arial'; " +
                         "-fx-font-size: 14px;");

        // Zone de saisie avec style
        TextField inputField = new TextField();
        inputField.setPromptText("Posez votre question...");
        inputField.setStyle("-fx-background-color: #333; " +
                          "-fx-text-fill: white; " +
                          "-fx-prompt-text-fill: #888;");

        // Bouton d'envoi avec style Netflix
        Button sendBtn = new Button("Envoyer");
        sendBtn.setDefaultButton(true);
        sendBtn.setStyle("-fx-background-color: #E50914; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;");

        // Suggestions rapides
        HBox quickSuggestions = new HBox(10);
        quickSuggestions.setAlignment(Pos.CENTER);
        String[] suggestions = {
            "📅 Événements à venir",
            "💰 Prix et réductions",
            "❓ Aide générale",
            "📍 Lieux disponibles"
        };

        for (String suggestion : suggestions) {
            Button suggestionBtn = new Button(suggestion);
            suggestionBtn.setStyle("-fx-background-color: #333; " +
                                 "-fx-text-fill: white; " +
                                 "-fx-background-radius: 15;");
            suggestionBtn.setOnAction(e -> {
                inputField.setText(suggestion.replaceAll("[📅💰❓📍] ", ""));
                sendBtn.fire();
            });
            quickSuggestions.getChildren().add(suggestionBtn);
        }

        HBox inputBox = new HBox(10, inputField, sendBtn);
        inputBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(inputField, Priority.ALWAYS);

        root.getChildren().addAll(chatArea, quickSuggestions, inputBox);

        Scene scene = new Scene(root, 500, 600);
        chatbotStage.setScene(scene);
        chatbotStage.show();

        // Message d'accueil avec emojis et formatage
        chatArea.appendText("🤖 Assistant: Bonjour ! Je suis votre assistant virtuel.\n\n");
        chatArea.appendText("Je peux vous aider avec :\n");
        chatArea.appendText("📅 Gestion des événements\n");
        chatArea.appendText("🎫 Réservations\n");
        chatArea.appendText("💰 Prix et disponibilités\n");
        chatArea.appendText("📍 Informations sur les lieux\n");
        chatArea.appendText("❌ Annulations\n");
        chatArea.appendText("👤 Gestion de compte\n");
        chatArea.appendText("🌍 Langue de l'application\n\n");
        chatArea.appendText("💡 Conseil: Utilisez les boutons de suggestion rapide ci-dessous ou posez votre question directement!\n\n");

        sendBtn.setOnAction(e -> {
            String question = inputField.getText().trim();
            if (!question.isEmpty()) {
                chatArea.appendText("\n👤 Vous: " + question + "\n");
                String response = getChatbotResponse(question);
                chatArea.appendText("🤖 Assistant: " + response + "\n");
                inputField.clear();
                // Faire défiler automatiquement vers le bas
                chatArea.setScrollTop(Double.MAX_VALUE);
            }
        });

        inputField.setOnAction(sendBtn.getOnAction());
    }

    private String getChatbotResponse(String question) {
        question = question.toLowerCase();
        
        // Événements à venir
        if (question.contains("événements à venir") || question.contains("prochains événements")) {
            List<Event> events = eventDAO.afficherEvents();
            if (events.isEmpty()) {
                return "Aucun événement n'est prévu pour le moment.";
            }
            StringBuilder response = new StringBuilder("Voici les prochains événements :\n");
            for (int i = 0; i < Math.min(3, events.size()); i++) {
                Event event = events.get(i);
                response.append("📅 ").append(event.getNomEvent())
                        .append(" - Le ").append(event.getDateEvent())
                        .append(" à ").append(event.getLocalisation())
                        .append("\n");
            }
            return response.toString();
        }
        
        // Prix et réductions
        if (question.contains("prix") || question.contains("tarif") || question.contains("réduction")) {
            return "🏷️ Les prix de nos événements varient selon le type :\n" +
                   "- Concerts : 30-80 DT\n" +
                   "- Spectacles : 25-60 DT\n" +
                   "- Festivals : 40-100 DT\n" +
                   "💡 Conseil : Réservez tôt pour bénéficier des meilleurs tarifs !";
        }
        
        // Réservations
        if (question.contains("réserver") || question.contains("reservation")) {
            return "📝 Pour réserver un événement :\n" +
                   "1. Cliquez sur le bouton 'Réserver' de l'événement\n" +
                   "2. Indiquez le nombre de personnes\n" +
                   "3. Entrez votre numéro de téléphone\n" +
                   "4. Confirmez la réservation\n\n" +
                   "💡 Conseil : Gardez votre numéro de réservation, il vous sera demandé en cas d'annulation.";
        }
        
        // Lieux disponibles
        if (question.contains("lieu") || question.contains("localisation") || question.contains("où")) {
            return "📍 Nos événements se déroulent dans plusieurs lieux :\n" +
                   "- Théâtre municipal de Tunis\n" +
                   "- Cité de la Culture\n" +
                   "- Carthage Event Center\n" +
                   "- Palais des Congrès\n\n" +
                   "🚗 Chaque lieu dispose d'un parking gratuit pour les participants.";
        }
        
        // Annulation
        if (question.contains("annul")) {
            return "❌ Pour annuler une réservation :\n" +
                   "1. Allez dans 'Mes réservations'\n" +
                   "2. Sélectionnez la réservation à annuler\n" +
                   "3. Cliquez sur 'Annuler'\n\n" +
                   "⚠️ Note : L'annulation est possible jusqu'à 24h avant l'événement.";
        }
        
        // Compte utilisateur
        if (question.contains("compte") || question.contains("profil")) {
            return "👤 Gestion de votre compte :\n" +
                   "- Vos réservations sont visibles dans 'Mes réservations'\n" +
                   "- Le nombre total de vos places réservées est affiché en haut\n" +
                   "- Pour modifier vos informations, contactez un administrateur\n\n" +
                   "🔐 La modification du mot de passe sera bientôt disponible !";
        }
        
        // Aide générale
        if (question.contains("aide") || question.contains("help")) {
            return "🎯 Voici comment utiliser l'application :\n" +
                   "1. Parcourez les événements disponibles\n" +
                   "2. Utilisez la recherche ou les filtres\n" +
                   "3. Cliquez sur un événement pour plus d'infos\n" +
                   "4. Réservez en quelques clics\n\n" +
                   "❓ Besoin d'aide spécifique ? Posez votre question !";
        }
        
        // Langue
        if (question.contains("langue") || question.contains("language")) {
            return "🌍 L'application est actuellement en français.\n" +
                   "🔜 Prochainement disponible en :\n" +
                   "- Anglais\n" +
                   "- Arabe\n" +
                   "Nous vous informerons dès que ces langues seront disponibles !";
        }
        
        // Recherche
        if (question.contains("recherche") || question.contains("chercher") || question.contains("trouver")) {
            return "🔍 Pour trouver un événement :\n" +
                   "1. Utilisez la barre de recherche en haut\n" +
                   "2. Filtrez par type d'événement\n" +
                   "3. Triez par date ou prix\n" +
                   "4. Utilisez le calendrier pour une vue mensuelle";
        }
        
        // Question sur les questions possibles
        if ((question.contains("questions") || question.contains("question")) && 
            (question.contains("peux") || question.contains("possible") || 
             question.contains("répondre") || question.contains("réponds"))) {
            return "💬 Je peux répondre à vos questions sur :\n" +
                   "📅 Les événements à venir\n" +
                   "🎫 Les réservations\n" +
                   "💰 Les prix et réductions\n" +
                   "📍 Les lieux des événements\n" +
                   "❌ Les annulations\n" +
                   "👤 Votre compte\n" +
                   "🔍 La recherche d'événements\n" +
                   "🌍 Les langues disponibles\n\n" +
                   "💡 Conseil : Utilisez les boutons de suggestion rapide !";
        }

        // Réponse par défaut
        return "👋 Je suis là pour vous aider ! Voici ce que je peux faire :\n" +
               "- Informations sur les événements\n" +
               "- Aide à la réservation\n" +
               "- Prix et disponibilités\n" +
               "- Localisation des événements\n" +
               "- Gestion de compte\n\n" +
               "💡 Conseil : Essayez les boutons de suggestion ou posez une question plus précise !";
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

    private void setupSortComboBox() {
        sortComboBox.getItems().setAll("Nom", "Prix", "Date");
        sortComboBox.setValue("Nom");
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        List<Event> filteredEvents = eventList.stream()
            .filter(event -> event.getNomEvent().toLowerCase().contains(searchText) ||
                            event.getLocalisation().toLowerCase().contains(searchText) ||
                            event.getType().toLowerCase().contains(searchText))
            .collect(Collectors.toList());
        eventCarousel.getChildren().clear();
        for (Event event : filteredEvents) {
            VBox card = createEventCard(event);
            eventCarousel.getChildren().add(card);
        }
    }

    @FXML
    private void handleSort() {
        String sortBy = sortComboBox.getValue();
        if (sortBy == null) return;

        eventCarousel.getChildren().clear();
        List<Event> sortedEvents = new ArrayList<>(eventList);

        switch (sortBy) {
            case "Nom":
                sortedEvents.sort((e1, e2) -> e1.getNomEvent().compareToIgnoreCase(e2.getNomEvent()));
                break;
            case "Prix":
                sortedEvents.sort((e1, e2) -> Double.compare(e1.getMontant(), e2.getMontant()));
                break;
            case "Date":
                sortedEvents.sort((e1, e2) -> e1.getDateEvent().compareTo(e2.getDateEvent()));
                break;
        }

        for (Event event : sortedEvents) {
            VBox card = createEventCard(event);
            eventCarousel.getChildren().add(card);
        }
    }

    @FXML
    private void handleOpenCalendar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventCalendar.fxml"));
            Parent root = loader.load();
            
            Stage calendarStage = new Stage();
            calendarStage.setTitle("Calendrier des Événements");
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/netflix.css").toExternalForm());
            
            calendarStage.setScene(scene);
            calendarStage.setMinWidth(800);
            calendarStage.setMinHeight(600);
            calendarStage.show();
            
        } catch (IOException e) {
            showError("Erreur lors de l'ouverture du calendrier: " + e.getMessage());
        }
    }

    private void shareToFacebook(String text) {
        try {
            String encodedText = java.net.URLEncoder.encode(text, "UTF-8");
            String url = "https://www.facebook.com/sharer/sharer.php?u=&quote=" + encodedText;
            openInBrowser(url);
        } catch (Exception e) {
            showError("Erreur lors du partage sur Facebook: " + e.getMessage());
        }
    }

    private void shareToWhatsApp(String text) {
        try {
            String encodedText = java.net.URLEncoder.encode(text, "UTF-8");
            String url = "https://wa.me/?text=" + encodedText;
            openInBrowser(url);
        } catch (Exception e) {
            showError("Erreur lors du partage sur WhatsApp: " + e.getMessage());
        }
    }

    private void shareToMessenger(String text) {
        try {
            String encodedText = java.net.URLEncoder.encode(text, "UTF-8");
            String url = "https://www.messenger.com/new?text=" + encodedText;
            openInBrowser(url);
        } catch (Exception e) {
            showError("Erreur lors du partage sur Messenger: " + e.getMessage());
        }
    }

    private void shareToInstagram(String text) {
        // Instagram ne permet pas le partage direct via URL, donc on copie le texte dans le presse-papiers
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
        
        showInfo("Le texte a été copié dans le presse-papiers. Vous pouvez maintenant le coller dans Instagram.");
        
        try {
            // Ouvrir Instagram si possible
            openInBrowser("https://www.instagram.com");
        } catch (Exception e) {
            // Ignorer l'erreur si Instagram ne peut pas être ouvert
        }
    }

    private void openInBrowser(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            showError("Erreur lors de l'ouverture du navigateur: " + e.getMessage());
        }
    }
} 