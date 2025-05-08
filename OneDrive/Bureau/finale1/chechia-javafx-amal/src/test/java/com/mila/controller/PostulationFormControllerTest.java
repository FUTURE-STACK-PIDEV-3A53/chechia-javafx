package com.mila.controller;

import com.mila.model.Postulation;
import com.mila.model.ProgrammeEchange;
import com.mila.service.PostulationService;
import com.mila.service.ProgrammeEchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostulationFormControllerTest {

    private PostulationFormController controller;
    private PostulationService postulationService;
    private ProgrammeEchangeService programmeService;
    private ProgrammeEchange testProgramme;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        // Créer les mocks
        postulationService = mock(PostulationService.class);
        programmeService = mock(ProgrammeEchangeService.class);
        emailService = mock(EmailService.class);

        // Initialiser le contrôleur avec les mocks
        controller = new PostulationFormController();
        // Injection du mock EmailService
        java.lang.reflect.Field emailServiceField;
        try {
            emailServiceField = PostulationFormController.class.getDeclaredField("emailService");
            emailServiceField.setAccessible(true);
            emailServiceField.set(controller, emailService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Créer un programme de test
        testProgramme = new ProgrammeEchange();
        testProgramme.setId(1);
        testProgramme.setNomProgramme("Programme Test");
    }

    @Test
    void testSubmitValidPostulation() throws Exception {
        // Configurer le mock pour simuler un succès
        when(postulationService.ajouter(any(Postulation.class))).thenReturn(true);

        // Simuler la saisie des données valides
        controller.setProgramme(testProgramme);
        // Remplir les champs du formulaire
        controller.getNomField().setText("Doe");
        controller.getPrenomField().setText("John");
        controller.getAgeField().setText("25");
        controller.getEmailField().setText("john.doe@test.com");
        controller.getLettreMotivationField().setText("Lettre de motivation test");

        // Déclencher la soumission
        controller.handleSubmit();

        // Vérifier que le service a été appelé
        verify(postulationService).ajouter(any(Postulation.class));
    }

    @Test
    void testSubmitInvalidPostulation() {
        // Ne pas remplir les champs obligatoires
        controller.setProgramme(testProgramme);

        // Déclencher la soumission
        controller.handleSubmit();

        // Vérifier qu'aucune postulation n'a été enregistrée
        verify(postulationService, never()).ajouter(any(Postulation.class));
    }

    @Test
    void testEmailConfirmation() throws Exception {
        // Configurer le mock pour simuler un succès
        when(postulationService.ajouter(any(Postulation.class))).thenReturn(true);

        // Simuler une postulation valide
        controller.setProgramme(testProgramme);
        controller.getNomField().setText("Doe");
        controller.getPrenomField().setText("John");
        controller.getAgeField().setText("25");
        controller.getEmailField().setText("john.doe@test.com");
        controller.getLettreMotivationField().setText("Lettre de motivation test");

        // Déclencher la soumission
        controller.handleSubmit();

        // Vérifier que l'email a été envoyé
        verify(emailService).sendConfirmationEmail(eq("john.doe@test.com"), eq("Doe"), eq("John"), eq("Programme Test"));
    }
}