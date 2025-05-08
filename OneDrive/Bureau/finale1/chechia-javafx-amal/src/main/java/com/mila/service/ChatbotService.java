package com.mila.service;

import com.microsoft.bot.builder.Bot;
import com.microsoft.bot.builder.BotFrameworkAdapter;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.connector.authentication.ClaimsIdentity;
import com.microsoft.bot.connector.authentication.CredentialProvider;
import com.microsoft.bot.schema.Activity;

public class ChatbotService {
    private static ChatbotService instance;
    private final BotFrameworkAdapter adapter;

    private ChatbotService() {
        CredentialProvider credentialProvider = null; // Remplacez par une implémentation réelle
        adapter = new BotFrameworkAdapter(credentialProvider);
    }

    public static ChatbotService getInstance() {
        if (instance == null) {
            instance = new ChatbotService();
        }
        return instance;
    }

    public void handleActivity(Activity activity, ClaimsIdentity identity) {
        adapter.processActivity(identity, activity, (TurnContext turnContext) -> {
            // Logic to handle incoming messages and respond
            String userMessage = turnContext.getActivity().getText();
            String response = determineResponse(userMessage);
            turnContext.sendActivity(response);
            return null;
        });
    }

    private String determineResponse(String userMessage) {
        // Logic to determine the response based on user message
        if (userMessage.contains("programme d'échange")) {
            return "Voici les types de programmes d'échange disponibles : Académique, Culturel, Professionnel.";
        }
        return "Je ne suis pas sûr de comprendre votre question. Pouvez-vous reformuler ?";
    }
}