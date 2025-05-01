package chechia.tn.controllers.latifa;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import chechia.tn.entities.Candidature;
import chechia.tn.entities.Opportunite;
import chechia.tn.service.latifa.ServiceCandidature;
import chechia.tn.service.latifa.ServiceOpportunite;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class FronttController implements Initializable {

    @FXML private FlowPane opportunitiesGrid;
    @FXML private FlowPane candidaturesGrid;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeContratFilter;
    @FXML private ComboBox<String> locationFilter;
    @FXML private ComboBox<String> experienceFilter;
    @FXML private Button homeButton;
    @FXML private Button opportunitesButton;
    @FXML private Button candidatureButton;
    @FXML private Button redditButton;
    @FXML private TextArea chatHistory;
    @FXML private TextField chatInput;
    @FXML private VBox chatWindow;
    @FXML private Button chatBubbleButton;
    @FXML private TextField pageNumberField;
    @FXML private Button goToPageButton;
    @FXML private Label pageLabel;

    private final ServiceOpportunite serviceOpportunite = new ServiceOpportunite();
    private final ServiceCandidature serviceCandidature = new ServiceCandidature();
    private Opportunite selectedOpportunite;
    private HostServices hostServices;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // First ensure all FXML elements are properly injected
        assert redditButton != null : "fx:id=\"redditButton\" was not injected: check your FXML file 'front.fxml'.";

        // Then setup all the components
        setupFilters();
        setupSearch();
        setupNavigation();
        setupRedditButton();

        // Configuration initiale
        opportunitesButton.getStyleClass().add("active");
        candidaturesGrid.setVisible(false);
        loadOpportunities();
    }

    @FXML
    private void setupNavigation() {
        // Ajouter la classe active par défaut à l'onglet Opportunités
        opportunitesButton.getStyleClass().add("active");

        homeButton.setOnAction(e -> {
            updateTabSelection(homeButton);
            handleHomeClick();
        });

        opportunitesButton.setOnAction(e -> {
            updateTabSelection(opportunitesButton);
            opportunitiesGrid.setVisible(true);
            candidaturesGrid.setVisible(false);
            loadOpportunities();
        });

        candidatureButton.setOnAction(e -> {
            updateTabSelection(candidatureButton);
            opportunitiesGrid.setVisible(false);
            candidaturesGrid.setVisible(true);
            loadCandidatures();
        });
    }

    private void updateTabSelection(Button selectedButton) {
        // Retirer la classe active de tous les boutons
        homeButton.getStyleClass().removeAll("active");
        opportunitesButton.getStyleClass().removeAll("active");
        candidatureButton.getStyleClass().removeAll("active");

        // Ajouter la classe active au bouton sélectionné
        selectedButton.getStyleClass().add("active");
    }

    private void setupFilters() {
        // Initialisation des filtres
        typeContratFilter.getItems().addAll("Tous", "Benevolat", "Stage", "Emploi");
        locationFilter.getItems().addAll("Tous", "Tunis", "Sfax", "Sousse", "Nabeul", "Autre");
        experienceFilter.getItems().addAll("Tous", "0", "1", "2", "3", "4", "5+");

        // Valeurs par défaut
        typeContratFilter.setValue("Tous");
        locationFilter.setValue("Tous");
        experienceFilter.setValue("Tous");

        // Listeners pour les filtres
        typeContratFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        locationFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        experienceFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void setupRedditButton() {
        if (redditButton == null) {
            System.err.println("Reddit button is null - FXML injection failed");
            return;
        }

        redditButton.setOnAction(event -> {
            try {
                System.out.println("Attempting to load Fedi front interface...");
                // Charger le fichier FXML de Fedi
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fedi/front.fxml"));
                Parent root = loader.load();
                System.out.println("FXML loaded successfully");

                // Créer une nouvelle scène
                Scene scene = new Scene(root);
                System.out.println("Scene created");

                // Ajouter le fichier CSS
                URL cssUrl = getClass().getResource("/chechia/tn/css/front.css");
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("CSS added successfully");
                } else {
                    System.err.println("CSS file not found at /chechia/tn/css/front.css");
                }

                // Obtenir la fenêtre actuelle et changer sa scène
                Stage stage = (Stage) redditButton.getScene().getWindow();
                stage.setTitle("Reddit - Gestion des Posts");
                stage.setScene(scene);
                stage.show();
                System.out.println("New scene shown");
            } catch (IOException e) {
                System.err.println("Error loading Fedi interface: " + e.getMessage());
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur de navigation");
                alert.setContentText("Impossible de charger l'interface Reddit: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    private void applyFilters() {
        if (opportunitiesGrid.isVisible()) {
            filterOpportunities();
        } else if (candidaturesGrid.isVisible()) {
            filterCandidatures();
        }
    }

    private void filterOpportunities() {
        String searchText = searchField.getText().toLowerCase();
        String typeFilter = typeContratFilter.getValue();
        String locationFilterValue = locationFilter.getValue();
        String expFilter = experienceFilter.getValue();

        List<Opportunite> filteredList = serviceOpportunite.afficher().stream()
                .filter(o -> matchesSearch(o, searchText))
                .filter(o -> matchesTypeFilter(o, typeFilter))
                .filter(o -> matchesLocationFilter(o, locationFilterValue))
                .filter(o -> matchesExperienceFilter(o, expFilter))
                .collect(Collectors.toList());

        displayOpportunities(FXCollections.observableArrayList(filteredList));
    }

    private boolean matchesSearch(Opportunite o, String searchText) {
        if (searchText.isEmpty()) return true;
        return o.getTitre().toLowerCase().contains(searchText) ||
                o.getDescription().toLowerCase().contains(searchText);
    }

    private boolean matchesTypeFilter(Opportunite o, String typeFilter) {
        if (typeFilter == null || typeFilter.equals("Tous")) return true;
        return o.getType().name().equalsIgnoreCase(typeFilter);
    }

    private boolean matchesLocationFilter(Opportunite o, String locationFilter) {
        if (locationFilter == null || locationFilter.equals("Tous")) return true;
        return o.getLieu().equalsIgnoreCase(locationFilter);
    }

    private boolean matchesExperienceFilter(Opportunite o, String expFilter) {
        if (expFilter == null || expFilter.equals("Tous")) return true;
        if (expFilter.equals("5+")) return o.getExp_years() >= 5;
        return Integer.toString(o.getExp_years()).equals(expFilter);
    }

    private void filterCandidatures() {
        String searchText = searchField.getText().toLowerCase();
        List<Candidature> filteredList = serviceCandidature.afficher().stream()
                .filter(c -> matchesCandidatureSearch(c, searchText))
                .collect(Collectors.toList());
        displayCandidatures(FXCollections.observableArrayList(filteredList));
    }

    private boolean matchesCandidatureSearch(Candidature c, String searchText) {
        if (searchText.isEmpty()) return true;
        return c.getOpportunite().getTitre().toLowerCase().contains(searchText) ||
                c.getExperience().toLowerCase().contains(searchText);
    }

    @FXML
    private void loadOpportunities() {
        List<Opportunite> opportunites = serviceOpportunite.afficher();
        displayOpportunities(FXCollections.observableArrayList(opportunites));
    }

    private void displayOpportunities(ObservableList<Opportunite> opportunities) {
        opportunitiesGrid.getChildren().clear();
        for (Opportunite opp : opportunities) {
            VBox card = createOpportunityCard(opp);
            opportunitiesGrid.getChildren().add(card);
        }
    }

    private HBox createSocialShareButtons(Opportunite opp) {
        HBox socialButtons = new HBox(15);
        socialButtons.setAlignment(Pos.CENTER);
        socialButtons.setPadding(new Insets(5, 0, 5, 0));

        // Facebook button
        Button fbShare = new Button();
        fbShare.getStyleClass().addAll("social-button", "facebook");
        FontAwesomeIconView fbIcon = new FontAwesomeIconView(FontAwesomeIcon.FACEBOOK);
        fbShare.setGraphic(fbIcon);
        fbShare.setOnAction(e -> {
            if (hostServices != null) {
                String url = "https://www.facebook.com/sharer/sharer.php?u=" +
                        "https://votre-site.com/opportunite/" + opp.getId() +
                        "&quote=" + opp.getTitre();
                hostServices.showDocument(url);
            }
        });

        // Twitter button
        Button twitterShare = new Button();
        twitterShare.getStyleClass().addAll("social-button", "twitter");
        FontAwesomeIconView twitterIcon = new FontAwesomeIconView(FontAwesomeIcon.TWITTER);
        twitterShare.setGraphic(twitterIcon);
        twitterShare.setOnAction(e -> {
            if (hostServices != null) {
                String text = "Découvrez cette opportunité: " + opp.getTitre();
                String url = "https://twitter.com/intent/tweet?text=" +
                        text.replace(" ", "%20") +
                        "&url=https://votre-site.com/opportunite/" + opp.getId();
                hostServices.showDocument(url);
            }
        });

        // Reddit button
        Button redditShare = new Button();
        redditShare.getStyleClass().addAll("social-button", "reddit");
        FontAwesomeIconView redditIcon = new FontAwesomeIconView(FontAwesomeIcon.REDDIT);
        redditShare.setGraphic(redditIcon);
        redditShare.setOnAction(e -> {
            if (hostServices != null) {
                String url = "https://www.reddit.com/submit?url=" +
                        "https://votre-site.com/opportunite/" + opp.getId() +
                        "&title=" + opp.getTitre().replace(" ", "%20");
                hostServices.showDocument(url);
            }
        });

        socialButtons.getChildren().addAll(fbShare, twitterShare, redditShare);
        return socialButtons;
    }

    private VBox createOpportunityCard(Opportunite opp) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        Label title = new Label(opp.getTitre());
        title.getStyleClass().add("title");
        title.setWrapText(true);

        Label desc = new Label(opp.getDescription());
        desc.getStyleClass().add("info-label");
        desc.setWrapText(true);

        Label lieu = new Label("Lieu: " + opp.getLieu());
        lieu.getStyleClass().add("info-label");

        Label type = new Label("Type: " + opp.getType().name());
        type.getStyleClass().add("info-label");

        Label exp = new Label("Années d'expérience: " + opp.getExp_years());
        exp.getStyleClass().add("info-label");

        // Ajout des boutons de partage social
        HBox socialShare = createSocialShareButtons(opp);

        // Ajout du système de notation
        HBox ratingBox = new HBox(5);
        ratingBox.setAlignment(Pos.CENTER);
        Label ratingLabel = new Label("Noter cette opportunité: ");
        ratingLabel.getStyleClass().add("info-label");

        // Création des étoiles pour la notation
        HBox starsBox = new HBox(2);
        Label[] stars = new Label[5]; // Tableau pour stocker les références des étoiles
        int[] currentRating = {0}; // Pour stocker la note actuelle

        for (int i = 1; i <= 5; i++) {
            final int rating = i;
            Label star = new Label("☆");
            stars[i-1] = star; // Stocker la référence de l'étoile
            star.getStyleClass().add("star-rating");
            star.setStyle("-fx-cursor: hand;");

            star.setOnMouseEntered(e -> {
                // Effet de survol
                for (int j = 0; j < rating; j++) {
                    stars[j].setText("★");
                }
                for (int j = rating; j < 5; j++) {
                    stars[j].setText("☆");
                }
            });

            star.setOnMouseExited(e -> {
                // Retour à l'état précédent
                for (int j = 0; j < 5; j++) {
                    if (j < currentRating[0]) {
                        stars[j].setText("★");
                    } else {
                        stars[j].setText("☆");
                    }
                }
            });

            star.setOnMouseClicked(e -> {
                currentRating[0] = rating; // Mettre à jour la note actuelle
                // Mettre à jour l'affichage des étoiles
                for (int j = 0; j < 5; j++) {
                    if (j < rating) {
                        stars[j].setText("★");
                    } else {
                        stars[j].setText("☆");
                    }
                }
                showAlert(Alert.AlertType.INFORMATION,
                        "Note enregistrée",
                        "Vous avez donné une note de " + rating + " étoiles à cette opportunité.");
                // Ici vous pouvez ajouter la logique pour sauvegarder la note
            });

            starsBox.getChildren().add(star);
        }

        ratingBox.getChildren().addAll(ratingLabel, starsBox);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button postulerButton = new Button("Postuler");
        postulerButton.getStyleClass().add("card-button");
        postulerButton.setOnAction(e -> {
            selectedOpportunite = opp;
            handlePostuler();
        });

        card.getChildren().addAll(title, desc, lieu, type, exp, socialShare, ratingBox, spacer, postulerButton);
        return card;
    }

    private void loadCandidatures() {
        List<Candidature> candidatures = serviceCandidature.afficher();
        displayCandidatures(FXCollections.observableArrayList(candidatures));
    }

    private void displayCandidatures(ObservableList<Candidature> candidatures) {
        candidaturesGrid.getChildren().clear();
        for (Candidature candidature : candidatures) {
            VBox card = createCandidatureCard(candidature);
            candidaturesGrid.getChildren().add(card);
        }
    }

    private VBox createCandidatureCard(Candidature candidature) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        Label title = new Label(candidature.getOpportunite().getTitre());
        title.getStyleClass().add("title");
        title.setWrapText(true);

        Label exp = new Label("Expérience: " + candidature.getExperience());
        exp.getStyleClass().add("info-label");
        exp.setWrapText(true);

        Label anneeExp = new Label("Années d'expérience: " + candidature.getAnneeXp());
        anneeExp.getStyleClass().add("info-label");

        Label type = new Label("Type: " + candidature.getType().name());
        type.getStyleClass().add("info-label");

        Label statutLabel = new Label("Statut: " + candidature.getEtat().name());
        statutLabel.getStyleClass().addAll("status-tag", "status-" + candidature.getEtat().name().toLowerCase());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button detailsButton = new Button("Voir Détails");
        detailsButton.getStyleClass().add("card-button");
        detailsButton.setOnAction(e -> handleDetailsCandidature(candidature));

        card.getChildren().addAll(title, exp, anneeExp, type, statutLabel, spacer, detailsButton);
        return card;
    }

    @FXML
    private void handlePostuler() {
        if (selectedOpportunite == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune opportunité sélectionnée",
                    "Veuillez sélectionner une opportunité pour postuler.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/latifa/Candidature.fxml"));
            Parent root = loader.load();

            CandidatureController candidatureController = loader.getController();
            candidatureController.setOpportunite(selectedOpportunite);

            Stage stage = new Stage();
            stage.setTitle("Formulaire de candidature - " + selectedOpportunite.getTitre());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir le formulaire de candidature: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleDetailsCandidature(Candidature candidature) {
        // Afficher les informations de la candidature dans une alerte
        String details = String.format("""
            Titre: %s
            Expérience: %s
            Années d'expérience: %d
            Type: %s
            Statut: %s
            """,
                candidature.getOpportunite().getTitre(),
                candidature.getExperience(),
                candidature.getAnneeXp(),
                candidature.getType().name(),
                candidature.getEtat().name()
        );

        showAlert(Alert.AlertType.INFORMATION,
                "Détails de la candidature",
                details);
    }

    private void handleHomeClick() {
        opportunitiesGrid.setVisible(false);
        candidaturesGrid.setVisible(false);
        // TODO: Implémenter l'affichage de la page d'accueil
        showAlert(Alert.AlertType.INFORMATION, "Information", "Page d'accueil en cours de développement");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleSendMessage() {
        String message = chatInput.getText().trim();
        if (message.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez entrer un message");
            return;
        }

        // Ajouter le message de l'utilisateur
        chatHistory.appendText("Vous: " + message + "\n");
        chatInput.clear();

        // Simuler une réponse du bot (à remplacer par votre API de chatbot)
        new Thread(() -> {
            try {
                // Simuler un délai de réponse
                Thread.sleep(1000);

                String response = generateBotResponse(message.toLowerCase());

                Platform.runLater(() -> {
                    chatHistory.appendText("Bot: " + response + "\n");
                    // Faire défiler automatiquement vers le bas
                    chatHistory.setScrollTop(Double.MAX_VALUE);
                });
            } catch (InterruptedException e) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'envoi du message");
                });
            }
        }).start();
    }

    private String generateBotResponse(String message) {
        if (message.contains("bonjour") || message.contains("salut") || message.contains("hello")) {
            return "Bonjour ! Comment puis-je vous aider aujourd'hui ?";
        }
        else if (message.contains("merci")) {
            return "Je vous en prie ! N'hésitez pas si vous avez d'autres questions.";
        }
        else if (message.contains("aide") || message.contains("help")) {
            return "Je peux vous aider avec :\n- Les opportunités d'emploi\n- Les candidatures\n- Les programmes d'échange\n- Les événements\nQue souhaitez-vous savoir ?";
        }
        else if (message.contains("emploi") || message.contains("job") || message.contains("travail")) {
            return "Nous avons plusieurs opportunités d'emploi disponibles. Voulez-vous que je vous montre les dernières offres ?";
        }
        else if (message.contains("candidature") || message.contains("postuler")) {
            return "Pour postuler, vous pouvez consulter nos offres d'emploi et cliquer sur le bouton 'Postuler'. Avez-vous besoin d'aide pour trouver une opportunité spécifique ?";
        }
        else if (message.contains("échange") || message.contains("programme")) {
            return "Nos programmes d'échange sont variés. Je peux vous donner plus de détails sur un programme spécifique qui vous intéresse.";
        }
        else if (message.contains("événement") || message.contains("event")) {
            return "Nous organisons régulièrement des événements. Voulez-vous connaître les prochains événements à venir ?";
        }
        else {
            return "Je ne suis pas sûr de comprendre votre demande. Pourriez-vous la reformuler ? Je peux vous aider avec les emplois, les candidatures, les programmes d'échange et les événements.";
        }
    }

    @FXML
    private void toggleChatWindow() {
        chatWindow.setVisible(!chatWindow.isVisible());
        chatBubbleButton.setVisible(!chatWindow.isVisible());
        if (chatWindow.isVisible()) {
            // Faire défiler vers le bas quand la fenêtre s'ouvre
            chatHistory.setScrollTop(Double.MAX_VALUE);
        }
    }

}