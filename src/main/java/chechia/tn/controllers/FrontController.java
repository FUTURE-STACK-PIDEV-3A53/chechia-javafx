package chechia.tn.controllers;

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
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FrontController implements Initializable {

    @FXML private FlowPane opportunitiesGrid;
    @FXML private FlowPane candidaturesGrid;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeContratFilter;
    @FXML private ComboBox<String> locationFilter;
    @FXML private ComboBox<String> experienceFilter;
    @FXML private Button homeButton;
    @FXML private Button opportunitesButton;
    @FXML private Button candidatureButton;

    private final ServiceOpportunite serviceOpportunite = new ServiceOpportunite();
    private final ServiceCandidature serviceCandidature = new ServiceCandidature();
    private Opportunite selectedOpportunite;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupNavigation();
        setupFilters();
        setupSearch();

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

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button postulerButton = new Button("Postuler");
        postulerButton.getStyleClass().add("card-button");
        postulerButton.setOnAction(e -> {
            selectedOpportunite = opp;
            handlePostuler();
        });

        card.getChildren().addAll(title, desc, lieu, type, exp, spacer, postulerButton);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/candidature.fxml"));
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
}
