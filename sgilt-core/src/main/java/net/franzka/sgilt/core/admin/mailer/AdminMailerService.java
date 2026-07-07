package net.franzka.sgilt.core.admin.mailer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.mailer.MailRequest;
import net.franzka.sgilt.core.mailer.MailType;
import net.franzka.sgilt.core.mailer.MailerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Service d'envoi d'emails liés au provisionnement admin d'un prestataire.
 * Délègue l'envoi effectif à {@link MailerClient}, qui compose le HTML
 * à partir du {@link MailType} et des variables fournies.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminMailerService {

    private final MailerClient mailerClient;

    /**
     * Envoie l'email d'activation à un prestataire nouvellement provisionné par un admin.
     * Ne relance pas l'exception en cas d'échec — l'appelant décide de la réponse à renvoyer.
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
        } catch (RestClientException e) {
            log.error("Échec de l'envoi du mail d'activation pour {} — actionUrl={}", prestataireEmail, actionUrl, e);
            return false;
        }
    }
}
