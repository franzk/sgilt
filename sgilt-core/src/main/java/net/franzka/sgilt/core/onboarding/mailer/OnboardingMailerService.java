package net.franzka.sgilt.core.onboarding.mailer;

import net.franzka.sgilt.core.config.FrontendProperties;
import net.franzka.sgilt.core.config.MailerProperties;
import net.franzka.sgilt.core.mailer.MailerClient;
import net.franzka.sgilt.core.mailer.MailRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service d'envoi d'emails liés au process d'onboarding.
 * Délègue l'envoi effectif à {@link MailerClient}.
 */
@Service
public class OnboardingMailerService {

    private final MailerClient mailerClient;
    private final MailerProperties mailerProperties;
    private final FrontendProperties frontendProperties;

    /**
     * Construit le service avec ses dépendances.
     *
     * @param mailerClient       le client HTTP vers sgilt-mailer
     * @param mailerProperties   les propriétés mailer (expéditeur)
     * @param frontendProperties les propriétés frontend (URL de base)
     */
    public OnboardingMailerService(
            MailerClient mailerClient,
            MailerProperties mailerProperties,
            FrontendProperties frontendProperties) {
        this.mailerClient = mailerClient;
        this.mailerProperties = mailerProperties;
        this.frontendProperties = frontendProperties;
    }

    /**
     * Envoie le mail de validation de compte à un utilisateur dont l'email est inconnu dans Keycloak.
     *
     * @param email             l'adresse email du destinataire
     * @param confirmationToken le token de confirmation à inclure dans le lien
     */
    public void sendConfirmationEmail(String email, String confirmationToken) {
        String confirmationUrl = frontendProperties.url() + "/confirmation?token=" + confirmationToken;
        String html = """
                <p>Bonjour,</p>
                <p>Merci pour votre demande. Cliquez sur le lien ci-dessous pour confirmer votre adresse email :</p>
                <p><a href="%s">Confirmer ma demande</a></p>
                <p>Ce lien expire dans 24 heures.</p>
                """.formatted(confirmationUrl);

        mailerClient.sendMail(new MailRequest(
                mailerProperties.from(),
                email,
                "Confirmez votre demande",
                html
        ));
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

        mailerClient.sendMail(new MailRequest(
                mailerProperties.from(),
                email,
                "Activité suspecte sur votre compte",
                html
        ));
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
                """.formatted(frontendProperties.url());

        mailerClient.sendMail(new MailRequest(
                mailerProperties.from(),
                email,
                "Bienvenue sur Sgilt",
                html
        ));
    }
}
