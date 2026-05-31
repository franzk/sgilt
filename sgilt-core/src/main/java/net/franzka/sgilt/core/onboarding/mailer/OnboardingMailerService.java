package net.franzka.sgilt.core.onboarding.mailer;

import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.mailer.MailRequest;
import net.franzka.sgilt.core.mailer.MailerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service d'envoi d'emails liés au process d'onboarding.
 * Délègue l'envoi effectif à {@link MailerClient}.
 */
@Service
@Slf4j
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
     * Envoie l'email de validation de compte à un utilisateur dont l'email est inconnu dans Keycloak.
     *
     * @param email             l'adresse email du destinataire
     * @param verificationToken le token de confirmation à inclure dans le lien
     */
    public void sendVerificationEmail(String email, String verificationToken) {
        String confirmationUrl = frontendUrl + "/onboarding/verify?token=" + verificationToken;
        String html = """
                <p>Bonjour,</p>
                <p>Vous venez d’initier une demande de réservation sur Sgilt avec cette adresse email.</p>
                <p>Pour confirmer votre adresse email et créer votre compte, cliquez sur le lien ci-dessous :</p>
                <p><a href="%s">Confirmer mon adresse email</a></p>
                <p>Ce lien expire dans 24 heures.</p>
                <p>Si vous n’êtes pas à l’origine de cette action, vous pouvez simplement ignorer ce message.</p>
                <p>À bientôt,</p>
                <p>L’équipe Sgilt.</p>
                """.formatted(confirmationUrl);

        log.info("sendVerificationEmail → {}", email);
        mailerClient.sendMail(new MailRequest(mailerFrom, email, "Sgilt - Confirmez votre demande", html));
    }

    /**
     * Envoie un email d'information à un utilisateur dont l'email est déjà connu dans Keycloak.
     *
     * @param email         l'adresse email du destinataire
     */
    public void sendSecurityAlertEmail(String email) {
        String html = """
                <p>Bonjour,</p>
                <p>Vous venez d'initier une demande de réservation avec votre adresse email.</p>
                <p>Cette adresse est déjà liée à un compte Sgilt.</p>
                <p>Pour des raisons de sécurité, la demande n’a pas été créée.</p>
                <p></p>
                <p>Pour créer une nouvelle demande ou suivre vos événements, connectez-vous à votre espace :</p>
                <p><a href="%s">Accéder à Sgilt</a></p>
                <p></p>
                <p>Si vous n’êtes pas à l’origine de cette action, vous pouvez simplement ignorer ce message.</p>
                <p></p>
                <p>À bientôt,</p>
                <p>L’équipe Sgilt.</p>
                """.formatted(frontendUrl + "/app");

        log.info("sendSecurityAlertEmail → {}", email);
        mailerClient.sendMail(new MailRequest(mailerFrom, email, "Sgilt - Demande de réservation initiée avec votre adresse email", html));
    }

    /**
     * Envoie l'email de bienvenue après la confirmation de compte réussie.
     *
     * @param email l'adresse email du nouveau compte créé
     */
    public void sendWelcomeEmail(String email) {
        String html = """
            <p>Bonjour,</p>
            <p>Votre adresse email a bien été confirmée et votre compte Sgilt a été créé.</p>
            <p>Votre demande de réservation est maintenant enregistrée.</p>
            <p>Vous pouvez accéder à votre espace pour suivre vos événements et retrouver vos demandes en cours :</p>
            <p><a href="%s">Accéder à Sgilt</a></p>
            <p></p>
            <p>À bientôt,</p>
            <p>L’équipe Sgilt.</p>
            """.formatted(frontendUrl + "/app");
        log.info("sendWelcomeEmail → {}", email);
        mailerClient.sendMail(new MailRequest(mailerFrom, email, "Bienvenue sur Sgilt", html));
    }
}
