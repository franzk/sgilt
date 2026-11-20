package net.franzka.sgilt.core.admin.mailer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.mailer.MailRequest;
import net.franzka.sgilt.core.mailer.MailType;
import net.franzka.sgilt.core.mailer.MailerClient;
import org.springframework.amqp.AmqpException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

/**
 * Service d'envoi du mail d'invitation à un client invité depuis le BO (deal conclu hors Sgilt).
 * Délègue l'envoi effectif à {@link MailerClient}, qui compose le HTML à partir du
 * {@link MailType} et des variables fournies.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ClientInvitationMailerService {

    private static final DateTimeFormatter EVENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);

    private final MailerClient mailerClient;

    /**
     * Envoie le mail d'invitation au client : sa réservation confirmée l'attend, avec le lien
     * d'activation de son compte. Ne relance pas l'exception en cas d'échec — l'appelant décide de
     * la réponse à renvoyer.
     *
     * @param email           l'adresse email du client
     * @param firstName       le prénom du client, pour personnaliser le mail
     * @param prestataireName le nom du prestataire réservé
     * @param eventDate       la date de l'événement
     * @param actionUrl       l'URL d'action (avec le token signé) permettant d'activer le compte
     * @return {@code true} si l'envoi a réussi, {@code false} si l'appel au mailer a échoué
     */
    public boolean sendClientInvitationEmail(String email, String firstName, String prestataireName,
                                             LocalDate eventDate, String actionUrl) {
        try {
            log.info("sendClientInvitationEmail → {}", email);
            mailerClient.sendMail(new MailRequest(
                    email,
                    MailType.CLIENT_INVITATION_EMAIL,
                    Map.of("firstName", firstName,
                            "prestataireName", prestataireName,
                            "eventDate", eventDate.format(EVENT_DATE_FORMATTER),
                            "actionUrl", actionUrl),
                    null));
            return true;
        } catch (AmqpException e) {
            log.error("Échec de l'envoi du mail d'invitation pour {} — actionUrl={}", email, actionUrl, e);
            return false;
        }
    }
}
