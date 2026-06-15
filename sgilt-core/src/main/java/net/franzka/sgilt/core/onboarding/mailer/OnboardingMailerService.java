package net.franzka.sgilt.core.onboarding.mailer;

import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.mailer.MailRequest;
import net.franzka.sgilt.core.mailer.MailType;
import net.franzka.sgilt.core.mailer.MailerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service d'envoi d'emails liés au process d'onboarding.
 * Délègue l'envoi effectif à {@link MailerClient}, qui compose le HTML
 * à partir du {@link MailType} et des variables fournies.
 */
@Service
@Slf4j
public class OnboardingMailerService {

    private final MailerClient mailerClient;
    private final String frontendUrl;

    /**
     * Construit le service avec ses dépendances.
     *
     * @param mailerClient le client HTTP vers sgilt-mailer
     * @param frontendUrl  l'URL de base du frontend
     */
    public OnboardingMailerService(
            MailerClient mailerClient,
            @Value("${sgilt.frontend.url}") String frontendUrl) {
        this.mailerClient = mailerClient;
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

        log.info("sendVerificationEmail → {}", email);
        mailerClient.sendMail(new MailRequest(
                email,
                MailType.VERIFICATION_EMAIL,
                Map.of("confirmationUrl", confirmationUrl),
                null));
    }

    /**
     * Envoie un email d'information à un utilisateur dont l'email est déjà connu dans Keycloak.
     *
     * @param email l'adresse email du destinataire
     */
    public void sendSecurityAlertEmail(String email) {
        log.info("sendSecurityAlertEmail → {}", email);
        mailerClient.sendMail(new MailRequest(
                email,
                MailType.SECURITY_ALERT_EMAIL,
                Map.of("appUrl", frontendUrl + "/app"),
                null));
    }

    /**
     * Envoie l'email de bienvenue après la confirmation de compte réussie.
     *
     * @param email l'adresse email du nouveau compte créé
     */
    public void sendWelcomeEmail(String email) {
        log.info("sendWelcomeEmail → {}", email);
        mailerClient.sendMail(new MailRequest(
                email,
                MailType.WELCOME_EMAIL,
                Map.of("appUrl", frontendUrl + "/app"),
                null));
    }
}
