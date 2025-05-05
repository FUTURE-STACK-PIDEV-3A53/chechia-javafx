package org.example.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.model.Event;
import org.example.model.EventDAO;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EventCalendarController {
    @FXML private GridPane calendarGrid;
    @FXML private Label monthYearLabel;
    @FXML private Button previousMonthButton;
    @FXML private Button nextMonthButton;

    private YearMonth currentYearMonth;
    private final EventDAO eventDAO = new EventDAO();
    private Map<LocalDate, List<Event>> eventsByDate = new HashMap<>();

    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        updateCalendar();
    }

    private void updateCalendar() {
        // Mettre à jour le label du mois et année
        monthYearLabel.setText(currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        // Charger les événements pour le mois en cours
        loadEventsForMonth();

        // Effacer la grille existante (sauf la première ligne avec les jours de la semaine)
        clearCalendarGrid();

        // Remplir la grille avec les jours du mois
        fillCalendarGrid();
    }

    private void loadEventsForMonth() {
        eventsByDate.clear();
        List<Event> monthEvents = eventDAO.afficherEvents().stream()
            .filter(event -> {
                LocalDate eventDate = LocalDate.parse(event.getDateEvent());
                return eventDate.getYear() == currentYearMonth.getYear() &&
                       eventDate.getMonth() == currentYearMonth.getMonth();
            })
            .collect(Collectors.toList());

        // Grouper les événements par date
        eventsByDate = monthEvents.stream()
            .collect(Collectors.groupingBy(event -> LocalDate.parse(event.getDateEvent())));
    }

    private void clearCalendarGrid() {
        // Garder seulement la première ligne (jours de la semaine)
        calendarGrid.getChildren().removeIf(node -> 
            GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
    }

    private void fillCalendarGrid() {
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentYearMonth.lengthOfMonth();

        for (int i = 0; i < daysInMonth; i++) {
            LocalDate date = firstOfMonth.plusDays(i);
            int row = (dayOfWeek + i) / 7 + 1;
            int col = (dayOfWeek + i) % 7;

            VBox dayBox = createDayBox(date);
            calendarGrid.add(dayBox, col, row);
        }
    }

    private VBox createDayBox(LocalDate date) {
        VBox dayBox = new VBox(5);
        dayBox.setStyle("-fx-background-color: #1a1a1a; -fx-padding: 10; -fx-background-radius: 5;");
        dayBox.setMinHeight(120); // Hauteur minimale
        dayBox.setPrefHeight(150); // Hauteur préférée
        dayBox.setMaxHeight(Double.MAX_VALUE); // Permet l'expansion
        VBox.setVgrow(dayBox, Priority.ALWAYS);
        
        // Ajouter le numéro du jour
        Label dayLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dayLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        dayBox.getChildren().add(dayLabel);

        // Ajouter les événements pour ce jour
        List<Event> dayEvents = eventsByDate.get(date);
        if (dayEvents != null) {
            ScrollPane scrollPane = new ScrollPane();
            VBox eventsBox = new VBox(5);
            eventsBox.setStyle("-fx-spacing: 5;");
            VBox.setVgrow(eventsBox, Priority.ALWAYS);

            for (Event event : dayEvents) {
                VBox eventBox = new VBox(2);
                eventBox.setStyle("-fx-background-color: #E50914; -fx-padding: 5; -fx-background-radius: 3;");
                
                Label eventLabel = new Label(event.getNomEvent());
                eventLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
                eventLabel.setWrapText(true);
                
                Label timeLabel = new Label(event.getType());
                timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
                
                eventBox.getChildren().addAll(eventLabel, timeLabel);
                eventsBox.getChildren().add(eventBox);
            }

            scrollPane.setContent(eventsBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 5;");
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);
            
            dayBox.getChildren().add(scrollPane);
        }

        return dayBox;
    }

    @FXML
    private void handlePreviousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar();
    }

    @FXML
    private void handleNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar();
    }

    @FXML
    private void handleClose() {
        ((Stage) calendarGrid.getScene().getWindow()).close();
    }
} 