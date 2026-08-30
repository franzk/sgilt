package net.franzka.sgilt.core.prestataire.mailer;

import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.mailer.MailRequest;
import net.franzka.sgilt.core.mailer.MailType;
import net.franzka.sgilt.core.mailer.MailerClient;
import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service d'envoi d'emails liés au cycle de vie d'un prestataire et de sa fiche
 * (onboarding, publication).
 * Délègue l'envoi effectif à {@link MailerClient}, qui compose le HTML
 * à partir du {@link MailType} et des variables fournies.
 */
@Service
@Slf4j
public class PrestataireMailerService {

    private final MailerClient mailerClient;
    private final String frontendUrl;

    /**
     * Construit le service avec ses dépendances.
     *
     * @param mailerClient le client de publication des mails (RabbitMQ)
     * @param frontendUrl  l'URL de base du frontend, pour construire les liens inclus dans les mails
     */
    public PrestataireMailerService(MailerClient mailerClient, @Value("${sgilt.frontend.url}") String frontendUrl) {
        this.mailerClient = mailerClient;
        this.frontendUrl = frontendUrl;
    }

    /**
     * Envoie l'email d'activation à un prestataire nouvellement provisionné (flow autonome). Ne
     * relance pas l'exception en cas d'échec — l'appelant décide de la réponse à renvoyer.
     *
     * @param prestataireEmail l'adresse email du prestataire destinataire
     * @param firstName        le prénom du destinataire, pour personnaliser le mail
     * @param actionUrl        l'URL d'action (avec le token signé) permettant de définir le mot de passe
     * @return {@code true} si l'envoi a réussi, {@code false} si l'appel au mailer a échoué
     */
    public boolean sendPrestataireOnboardingEmail(String prestataireEmail, String firstName, String actionUrl) {
        try {
            log.info("sendPrestataireOnboardingEmail → {}", prestataireEmail);
            mailerClient.sendMail(new MailRequest(
                    prestataireEmail,
                    MailType.PRESTATAIRE_ONBOARDING_EMAIL,
                    Map.of("firstName", firstName, "actionUrl", actionUrl),
                    null));
            return true;
        } catch (AmqpException e) {
            log.error("Échec de l'envoi du mail d'activation pour {} — actionUrl={}", prestataireEmail, actionUrl, e);
            return false;
        }
    }

    /**
     * Envoie l'email de notification de publication à un prestataire déjà activé (flow autonome) —
     * sans lien d'activation, juste un pointeur vers sa page désormais visible.
     * Ne relance pas l'exception en cas d'échec — l'appelant décide de la réponse à renvoyer.
     *
     * @param prestataireEmail l'adresse email du prestataire destinataire
     * @param firstName        le prénom du destinataire, pour personnaliser le mail
     * @param slug              le slug de la fiche, pour construire le lien vers la page publique
     * @return {@code true} si l'envoi a réussi, {@code false} si l'appel au mailer a échoué
     */
    public boolean sendPrestatairePublishedEmail(String prestataireEmail, String firstName, String slug) {
        try {
            log.info("sendPrestatairePublishedEmail → {}", prestataireEmail);
            mailerClient.sendMail(new MailRequest(
                    prestataireEmail,
                    MailType.PRESTATAIRE_PUBLISHED_EMAIL,
                    Map.of("firstName", firstName, "pageUrl", frontendUrl + "/" + slug),
                    null));
            return true;
        } catch (AmqpException e) {
            log.error("Échec de l'envoi du mail de publication pour {}", prestataireEmail, e);
            return false;
        }
    }

    /**
     * Envoie l'email de bienvenue à un prestataire clé-en-main dont la fiche vient d'être publiée
     * sans jamais avoir reçu de mail auparavant — met en avant la page déjà construite, avec le
     * lien d'activation pour accéder à son espace.
     * Ne relance pas l'exception en cas d'échec — l'appelant décide de la réponse à renvoyer.
     *
     * @param prestataireEmail l'adresse email du prestataire destinataire
     * @param firstName        le prénom du destinataire, pour personnaliser le mail
     * @param actionUrl        l'URL d'action (avec le token signé) permettant de définir le mot de passe
     * @param slug             le slug de la fiche, pour construire le lien vers la page publique
     * @return {@code true} si l'envoi a réussi, {@code false} si l'appel au mailer a échoué
     */
    public boolean sendPrestatairePageReadyEmail(String prestataireEmail, String firstName, String actionUrl, String slug) {
        try {
            log.info("sendPrestatairePageReadyEmail → {}", prestataireEmail);
            mailerClient.sendMail(new MailRequest(
                    prestataireEmail,
                    MailType.PRESTATAIRE_PAGE_READY_EMAIL,
                    Map.of("firstName", firstName, "actionUrl", actionUrl, "pageUrl", frontendUrl + "/" + slug),
                    null));
            return true;
        } catch (AmqpException e) {
            log.error("Échec de l'envoi du mail de bienvenue clé-en-main pour {} — actionUrl={}", prestataireEmail, actionUrl, e);
            return false;
        }
    }
}
