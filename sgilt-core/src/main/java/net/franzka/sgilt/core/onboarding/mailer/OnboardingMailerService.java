package net.franzka.sgilt.core.onboarding.mailer;

import net.franzka.sgilt.core.mailer.MailerClient;
import net.franzka.sgilt.core.mailer.MailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service d'envoi d'emails liés au process d'onboarding.
 * Délègue l'envoi effectif à {@link MailerClient}.
 */
@Service
public class OnboardingMailerService {

    private final MailerClient mailerClient;
    private final String mailerFrom;
    private final String frontendUrl;

    /**
     * Construit le service avec ses dépendances.
     *
     * @param mailerClient le client HTTP vers sgilt-mailer
     * @param mailerFrom   l'adresse email expéditrice
     * @param frontendUrl  l'URL de base du frontend
     */
    public OnboardingMailerService(
            MailerClient mailerClient,
            @Value("${sgilt.mailer.from}") String mailerFrom,
            @Value("${sgilt.frontend.url}") String frontendUrl) {
        this.mailerClient = mailerClient;
        this.mailerFrom = mailerFrom;
        this.frontendUrl = frontendUrl;
    }

    /**
     * Envoie le mail de validation de compte à un utilisateur dont l'email est inconnu dans Keycloak.
     *
     * @param email             l'adresse email du destinataire
     * @param confirmationToken le token de confirmation à inclure dans le lien
     */
    public void sendConfirmationEmail(String email, String confirmationToken) {
        String confirmationUrl = frontendUrl + "/confirmation?token=" + confirmationToken;
        String html = """
                <p>Bonjour,</p>
                <p>Merci pour votre demande. Cliquez sur le lien ci-dessous pour confirmer votre adresse email :</p>
                <p><a href="%s">Confirmer ma demande</a></p>
                <p>Ce lien expire dans 24 heures.</p>
                """.formatted(confirmationUrl);

        mailerClient.sendMail(new MailRequest(mailerFrom, email, "Confirmez votre demande", html));
    }

    /**
     * Envoie un mail d'alerte de sécurité à un utilisateur dont l'email est déjà connu dans Keycloak.
     *
     * @param email         l'adresse email du destinataire
     * @param prestataireId l'identifiant du prestataire ciblé par la tentative
     */
    public void sendSecurityAlertEmail(String email, UUID prestataireId) {
        String html = """
                <p>Bonjour,</p>
                <p>Une tentative de réservation a été effectuée avec votre adresse email pour le prestataire <strong>%s</strong>.</p>
                <p>Si vous n'êtes pas à l'origine de cette action, ignorez ce message.</p>
                """.formatted(prestataireId);

        mailerClient.sendMail(new MailRequest(mailerFrom, email, "Activité suspecte sur votre compte", html));
    }

    /**
     * Envoie le mail de bienvenue après la confirmation de compte réussie.
     *
     * @param email l'adresse email du nouveau compte créé
     */
    public void sendWelcomeEmail(String email) {
        String html = """
                <p>Bonjour,</p>
                <p>Votre compte Sgilt a été créé avec succès. Bienvenue !</p>
                <p><a href="%s">Accéder à Sgilt</a></p>
                """.formatted(frontendUrl);

        mailerClient.sendMail(new MailRequest(mailerFrom, email, "Bienvenue sur Sgilt", html));
    }
}
