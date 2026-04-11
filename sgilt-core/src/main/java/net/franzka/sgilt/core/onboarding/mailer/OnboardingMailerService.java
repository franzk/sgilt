package net.franzka.sgilt.core.onboarding.mailer;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OnboardingMailerService {

    // TODO: brancher MailerClient

    public void sendConfirmationEmail(String email, String confirmationToken) {
        // TODO: envoyer mail de validation compte (anonyme email inconnu)
    }

    public void sendSecurityAlertEmail(String email, UUID prestataireId) {
        // TODO: envoyer mail de sécurité (anonyme email déjà connu dans KC)
    }

    public void sendWelcomeEmail(String email) {
        // TODO: envoyer mail de bienvenue après confirm-account réussi
    }
}
