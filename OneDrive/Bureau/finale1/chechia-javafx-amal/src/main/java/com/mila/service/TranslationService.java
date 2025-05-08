package com.mila.service;

import java.util.HashMap;
import java.util.Map;

public class TranslationService {
    private static TranslationService instance;
    private final Map<String, String> translations;

    private TranslationService() {
        translations = new HashMap<>();
        initializeTranslations();
    }

    public static synchronized TranslationService getInstance() {
        if (instance == null) {
            instance = new TranslationService();
        }
        return instance;
    }

    private void initializeTranslations() {
        // Program types
        translations.put("Académique", "Academic");
        translations.put("Culturel", "Cultural");
        translations.put("Professionnel", "Professional");

        // Countries
        translations.put("Tunisie", "Tunisia");
        translations.put("France", "France");
        translations.put("Allemagne", "Germany");
        translations.put("Italie", "Italy");
        translations.put("Espagne", "Spain");

        // UI Labels
        translations.put("Nationalité", "Nationality");
        translations.put("Durée", "Duration");
        translations.put("Nom", "Last Name");
        translations.put("Prénom", "First Name");
        translations.put("Âge", "Age");
        translations.put("Email", "Email");
        translations.put("Répartition par Nationalité", "Distribution by Nationality");
    }

    public String translateToEnglish(String frenchText) {
        if (frenchText == null) return "";
        return translations.getOrDefault(frenchText, frenchText);
    }

    public String translateToFrench(String englishText) {
        if (englishText == null) return "";
        for (Map.Entry<String, String> entry : translations.entrySet()) {
            if (entry.getValue().equals(englishText)) {
                return entry.getKey();
            }
        }
        return englishText;
    }
}