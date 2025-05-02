package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import org.example.utils.OpenRouterClient;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatBotController implements Initializable {
    @FXML
    private VBox chatBox;
    @FXML
    private TextField messageInput;
    @FXML
    private Button sendButton;
    @FXML
    private ScrollPane scrollPane;

    private OpenRouterClient openRouterClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        openRouterClient = new OpenRouterClient();
        setupChatBox();
        setupSendButton();
    }

    private void setupChatBox() {
        chatBox.setSpacing(10);
        chatBox.setPadding(new Insets(10));
        
        // Ajouter un message de bienvenue
        addMessage("Assistant", "Bonjour ! Je suis votre assistant de jeu. Comment puis-je vous aider ?", false);
    }

    private void setupSendButton() {
        sendButton.setOnAction(e -> handleSendMessage());
        messageInput.setOnAction(e -> handleSendMessage());
    }

    @FXML
    private void handleSendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty()) {
            // Afficher le message de l'utilisateur
            addMessage("Vous", message, true);
            messageInput.clear();

            // Envoyer le message à l'API et afficher la réponse
            try {
                String response = openRouterClient.sendMessage(message);
                addMessage("Assistant", response, false);
            } catch (Exception ex) {
                addMessage("Assistant", "Désolé, je n'ai pas pu traiter votre demande. Veuillez réessayer.", false);
                ex.printStackTrace();
            }
        }
    }

    private void addMessage(String sender, String message, boolean isUser) {
        TextFlow textFlow = new TextFlow();
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        textFlow.setStyle("-fx-background-color: " + (isUser ? "#DCF8C6" : "#FFFFFF") + "; " +
                         "-fx-background-radius: 10; " +
                         "-fx-border-radius: 10; " +
                         "-fx-border-color: #E0E0E0; " +
                         "-fx-border-width: 1;");

        Text senderText = new Text(sender + ": ");
        senderText.setFill(Color.GRAY);
        Text messageText = new Text(message);
        
        textFlow.getChildren().addAll(senderText, messageText);
        chatBox.getChildren().add(textFlow);
        
        // Faire défiler vers le bas
        scrollPane.setVvalue(1.0);
    }
} 