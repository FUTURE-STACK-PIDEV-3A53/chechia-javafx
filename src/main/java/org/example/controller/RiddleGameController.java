package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.dao.RiddleDAO;
import org.example.model.Game;
import org.example.model.Player;
import org.example.model.Riddle;
import org.example.dao.PlayerDAO;
import org.example.model.User;
import org.example.utils.SessionManager;

import java.util.List;
import java.util.Random;

public class RiddleGameController {
    @FXML private TextArea questionArea;
    @FXML private TextField answerField;
    @FXML private Button submitButton;
    @FXML private Label scoreLabel;
    @FXML private Button nextButton;
    @FXML private Label feedbackLabel;
    @FXML private Button exitButton;
    @FXML private Label gameTitleLabel;

    private Game game;
    private Player player;
    private List<Riddle> riddles;
    private Riddle currentRiddle;
    private int currentScore = 0;
    private RiddleDAO riddleDAO;
    private Random random = new Random();
    private int currentRiddleIndex = 0;

    @FXML
    public void initialize() {
        riddleDAO = RiddleDAO.getInstance();
        nextButton.setDisable(true);
        updateScoreLabel();

        // Initialisation du joueur avec les informations de la session
        if (SessionManager.isLoggedIn()) {
            User currentUser = SessionManager.getUser();
            this.player = new Player();
            this.player.setId(currentUser.getId());
            this.player.setUsername(currentUser.getUsername());
            this.player.setScore(0);
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Vous devez être connecté pour jouer.");
            closeWindow();
            return;
        }
    }

    public void setGameAndPlayer(Game game, Player player) {
        if (game == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Game cannot be null");
            closeWindow();
            return;
        }
        
        this.game = game;
        this.player = player;
        
        // Initialize RiddleDAO if not already done
        if (riddleDAO == null) {
            riddleDAO = RiddleDAO.getInstance();
        }
        
        // Load riddles for this game
        try {
            riddles = riddleDAO.findByGameId(game.getId());
            if (riddles == null || riddles.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "No riddles found for this game");
                closeWindow();
                return;
            }
            
            // Initialize game state
            currentRiddleIndex = 0;
            displayCurrentRiddle();
            
            // Log initialization
            System.out.println("Game initialized with " + riddles.size() + " riddles");
            System.out.println("Current player: " + player.getUsername() + " (ID: " + player.getId() + ")");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load riddles: " + e.getMessage());
            closeWindow();
        }
    }

    private void displayCurrentRiddle() {
        if (currentRiddleIndex < riddles.size()) {
            currentRiddle = riddles.get(currentRiddleIndex);
            questionArea.setText(currentRiddle.getQuestion());
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Fin du jeu", "Félicitations ! Vous avez terminé toutes les énigmes.");
            closeWindow();
        }
    }

    @FXML
    private void handleSubmitAnswer() {
        String answer = answerField.getText().trim();
        if (answer.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez entrer une réponse");
            return;
        }

        if (answer.equalsIgnoreCase(currentRiddle.getAnswer())) {
            player.setScore(player.getScore() + 10);
            showAlert(Alert.AlertType.INFORMATION, "Félicitations", "Bonne réponse ! +10 points");
            PlayerDAO.getInstance().save(player);
            System.out.println("Score sauvegardé pour le joueur : " + player.getUsername() + " - Score : " + player.getScore());
        } else {
            showAlert(Alert.AlertType.ERROR, "Incorrect", "Mauvaise réponse. Essayez encore !");
        }

        answerField.clear();
        loadNextRiddle();
    }

    @FXML
    private void handleNextQuestion() {
        loadNextRiddle();
    }

    @FXML
    private void handleExit() {
        closeWindow();
    }

    private void selectNewRiddle() {
        if (riddles.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Game Over", "No more riddles available! Final score: " + currentScore);
            closeWindow();
            return;
        }

        int index = random.nextInt(riddles.size());
        currentRiddle = riddles.remove(index);
        questionArea.setText(currentRiddle.getQuestion());
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + currentScore);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void closeWindow() {
        try {
            if (submitButton != null && submitButton.getScene() != null && submitButton.getScene().getWindow() != null) {
                Stage stage = (Stage) submitButton.getScene().getWindow();
                stage.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing window: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadNextRiddle() {
        if (currentRiddleIndex < riddles.size() - 1) {
            currentRiddleIndex++;
            currentRiddle = riddles.get(currentRiddleIndex);
            questionArea.setText(currentRiddle.getQuestion());
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Fin du jeu", "Félicitations ! Vous avez terminé toutes les énigmes.");
            closeWindow();
        }
    }
} 