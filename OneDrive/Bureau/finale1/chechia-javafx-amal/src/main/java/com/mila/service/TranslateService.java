package com.mila.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class TranslateService {
    private static final String PROJECT_ID = "your-project-id";
    private static final String API_KEY = "your-new-api-key";
    private final Translate translate;
    private static TranslateService instance;

    private TranslateService() {
        translate = TranslateOptions.newBuilder()
                .setProjectId(PROJECT_ID)
                .setApiKey(API_KEY)
                .build()
                .getService();
    }

    public static TranslateService getInstance() {
        if (instance == null) {
            instance = new TranslateService();
        }
        return instance;
    }

    public Translation translate(String text, Translate.TranslateOption... options) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        return translate.translate(text, options);
    }

    public String translateText(String text, String targetLanguage) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        Translation translation = translate.translate(
                text,
                Translate.TranslateOption.targetLanguage(targetLanguage));

        return translation.getTranslatedText();
    }

    public String translateToFrench(String text) {
        return translateText(text, "fr");
    }

    public String translateToEnglish(String text) {
        return translateText(text, "en");
    }

    public String translateToArabic(String text) {
        return translateText(text, "ar");
    }
}