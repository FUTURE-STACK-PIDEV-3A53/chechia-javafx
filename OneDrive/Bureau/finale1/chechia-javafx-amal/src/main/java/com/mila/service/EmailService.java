package com.mila.service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

public class EmailService {
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    private static final String FROM_EMAIL = "amalsaafi552@gmail.com";
    private static final String PASSWORD = "opaa123@12";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    
    private Session getEmailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });
    }

    public void sendConfirmationEmail(String toEmail, String nom, String prenom, String programmeName) {
        LOGGER.info("Tentative d'envoi d'email à " + toEmail + " pour le programme " + programmeName);
        try {
            Session session = getEmailSession();
            MimeMessage emailContent = new MimeMessage(session);
            emailContent.setFrom(new InternetAddress(FROM_EMAIL));
            emailContent.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            emailContent.addRecipient(Message.RecipientType.CC, new InternetAddress("amalsaafi552@gmail.com"));
            emailContent.setSubject("Confirmation de votre postulation - " + programmeName);

            String htmlContent = String.format(
                "<html><body>" +
                "<h2>Confirmation de postulation</h2>" +
                "<p>Cher/Chère %s %s,</p>" +
                "<p>Nous avons bien reçu votre postulation pour le programme \"%s\". " +
                "Notre équipe examinera votre candidature et vous contactera prochainement.</p>" +
                "<p>Cordialement,<br>L'équipe des programmes d'échange</p>" +
                "</body></html>",
                prenom, nom, programmeName
            );

            emailContent.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(emailContent);

            LOGGER.info("Email envoyé avec succès à " + toEmail);

        } catch (Exception e) {
            String errorMessage = "Erreur lors de l'envoi de l'email: " + e.getMessage();
            LOGGER.log(Level.SEVERE, errorMessage, e);
            throw new RuntimeException("Échec de l'envoi de l'email. Veuillez vérifier les paramètres SMTP.", e);
        }
    }

    public void testSendEmail() {
        String testEmail = "amalsaafi552@gmail.com";
        sendConfirmationEmail(testEmail, "Test", "Utilisateur", "Programme Test");
    }
}