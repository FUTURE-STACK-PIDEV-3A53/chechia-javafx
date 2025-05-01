package chechia.tn.controllers.latifa;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class questionController implements Initializable {

    @FXML
    private TextArea jobOfferTextArea;
    @FXML private Button analyzeButton;
    @FXML private FlowPane questionsContainer;
    @FXML private ProgressIndicator analysisLoading;
    @FXML private VBox interviewAnalysisSection;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String OPENAI_API_KEY = "votre-clé-api"; // Remplacez par votre clé API

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation si nécessaire
        setupUI();
    }

    private void setupUI() {
        if (jobOfferTextArea != null) {
            jobOfferTextArea.getStyleClass().add("modern-textarea");
        }
        if (analyzeButton != null) {
            analyzeButton.getStyleClass().add("primary-button");
        }
    }

    @FXML
    private void handleAnalyzeJobOffer() {
        if (jobOfferTextArea == null || analyzeButton == null ||
                analysisLoading == null || questionsContainer == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Composants UI non initialisés");
            return;
        }

        String jobOffer = jobOfferTextArea.getText().trim();
        if (jobOffer.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer une offre d'emploi à analyser.");
            return;
        }

        analyzeButton.setDisable(true);
        analysisLoading.setVisible(true);
        questionsContainer.getChildren().clear();

        Task<List<QuestionCard>> analysisTask = new Task<>() {
            @Override
            protected List<QuestionCard> call() throws Exception {
                return generateInterviewQuestions(jobOffer);
            }
        };

        analysisTask.setOnSucceeded(e -> {
            List<QuestionCard> questions = analysisTask.getValue();
            displayQuestions(questions);
            analyzeButton.setDisable(false);
            analysisLoading.setVisible(false);
        });

        analysisTask.setOnFailed(e -> {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Une erreur est survenue lors de l'analyse: " +
                            analysisTask.getException().getMessage());
            analyzeButton.setDisable(false);
            analysisLoading.setVisible(false);
        });

        new Thread(analysisTask).start();
    }

    private List<QuestionCard> generateInterviewQuestions(String jobOffer) throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");

        JSONArray messages = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "Vous êtes un expert en recrutement qui génère des questions pertinentes pour un entretien d'embauche.");
        messages.put(systemMessage);

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", "Générez 5 questions d'entretien pertinentes pour cette offre d'emploi : " + jobOffer);
        messages.put(userMessage);

        requestBody.put("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonResponse = new JSONObject(response.body());

        String content = jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        List<QuestionCard> questions = new ArrayList<>();
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                questions.add(new QuestionCard(line.replaceFirst("^\\d+\\.\\s*", "")));
            }
        }

        return questions;
    }

    private void displayQuestions(List<QuestionCard> questions) {
        Platform.runLater(() -> {
            questionsContainer.getChildren().clear();

            for (QuestionCard question : questions) {
                VBox questionBox = createQuestionBox(question);
                questionsContainer.getChildren().add(questionBox);
            }
        });
    }

    private VBox createQuestionBox(QuestionCard question) {
        VBox box = new VBox(10);
        box.getStyleClass().add("question-card");
        box.setPadding(new Insets(15));
        box.setMaxWidth(300);

        Label questionLabel = new Label(question.getQuestion());
        questionLabel.setWrapText(true);
        questionLabel.getStyleClass().add("question-text");

        box.getChildren().add(questionLabel);

        box.setOnMouseEntered(e -> box.setStyle(box.getStyle() + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        box.setOnMouseExited(e -> box.setStyle(box.getStyle() + "-fx-scale-x: 1; -fx-scale-y: 1;"));

        return box;
    }

    private static class QuestionCard {
        private final String question;

        public QuestionCard(String question) {
            this.question = question;
        }

        public String getQuestion() {
            return question;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
