package net.franzka.sgilt.core.onboarding.mailer;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service d'envoi d'emails liés au process d'onboarding.
 * Les méthodes sont des stubs en attente du branchement de {@code MailerClient}.
 */
@Service
public class OnboardingMailerService {

    // TODO: brancher MailerClient

    /**
     * Envoie le mail de validation de compte à un utilisateur dont l'email est inconnu dans Keycloak.
     *
     * @param email             l'adresse email du destinataire
     * @param confirmationToken le JWT de confirmation à inclure dans le lien
     */
    public void sendConfirmationEmail(String email, String confirmationToken) {
        // TODO: envoyer mail de validation compte (anonyme email inconnu)
    }

    /**
     * Envoie un mail d'alerte de sécurité à un utilisateur dont l'email est déjà connu dans Keycloak.
     *
     * @param email         l'adresse email du destinataire
     * @param prestataireId l'identifiant du prestataire ciblé par la tentative
     */
    public void sendSecurityAlertEmail(String email, UUID prestataireId) {
        // TODO: envoyer mail de sécurité (anonyme email déjà connu dans KC)
    }

    /**
     * Envoie le mail de bienvenue après la confirmation de compte réussie.
     *
     * @param email l'adresse email du nouveau compte créé
     */
    public void sendWelcomeEmail(String email) {
        // TODO: envoyer mail de bienvenue après confirm-account réussi
    }
}
