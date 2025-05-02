package org.example.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import org.json.JSONArray;

public class OpenRouterClient {
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String API_KEY = "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"; // À remplacer par votre clé API
    private final HttpClient httpClient;

    public OpenRouterClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String sendMessage(String message) throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "anthropic/claude-3-opus-20240229");
        requestBody.put("max_tokens", 500);
        
        JSONArray messages = new JSONArray();
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "Vous êtes un assistant de jeu qui aide les joueurs avec leurs questions sur le jeu. Répondez de manière concise et utile.");
        messages.put(systemMessage);

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", message);
        messages.put(userMessage);

        requestBody.put("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .header("HTTP-Referer", "https://github.com/chachiaPI")
                .header("X-Title", "ChachiaPI")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.body());
            System.out.println("API Response: " + jsonResponse.toString(2));
            
            if (jsonResponse.has("choices") && jsonResponse.getJSONArray("choices").length() > 0) {
                JSONObject choice = jsonResponse.getJSONArray("choices").getJSONObject(0);
                if (choice.has("message") && choice.getJSONObject("message").has("content")) {
                    return choice.getJSONObject("message").getString("content");
                }
            }
            
            if (jsonResponse.has("error")) {
                JSONObject error = jsonResponse.getJSONObject("error");
                throw new Exception("Erreur API: " + error.getString("message"));
            }
            
            return "Désolé, je n'ai pas pu comprendre la réponse de l'API.";
        } else {
            System.out.println("Error response: " + response.statusCode() + " - " + response.body());
            throw new Exception("Erreur lors de la communication avec l'API: " + response.statusCode() + "\n" + response.body());
        }
    }
} 