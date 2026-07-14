package net.franzka.sgilt.notifications.notification.mailer;

import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.notifications.mailer.MailRequest;
import net.franzka.sgilt.notifications.mailer.MailType;
import net.franzka.sgilt.notifications.mailer.MailerClient;
import net.franzka.sgilt.notifications.notification.domain.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Envoie l'email correspondant à une notification in-app, quand une clé i18n connue lui est
 * associée — pour l'instant, toutes les notifications déclenchent un email (pas encore de matrice
 * de préférence par type/utilisateur, prévue dans une tâche séparée). Le contexte du mail réutilise
 * directement les {@code params} déjà construits pour l'in-app par {@code NotificationEventMapper},
 * enrichis du lien absolu vers le front — un nouveau type de notification ajoute une entrée dans
 * {@link #MAIL_TYPE_BY_MESSAGE_KEY} et un gabarit côté sgilt-mailer, pas de nouvelle méthode ici.
 */
@Service
@Slf4j
public class NotificationMailerService {

    private static final Map<String, MailType> MAIL_TYPE_BY_MESSAGE_KEY = Map.ofEntries(
            Map.entry("notification.reservation.new_request", MailType.RESERVATION_NEW_REQUEST_EMAIL),
            Map.entry("notification.reservation.status.in_discussion", MailType.RESERVATION_STATUS_IN_DISCUSSION_EMAIL),
            Map.entry("notification.reservation.status.confirmed", MailType.RESERVATION_STATUS_CONFIRMED_EMAIL),
            Map.entry("notification.reservation.status.refused_pre_contact", MailType.RESERVATION_STATUS_REFUSED_PRE_CONTACT_EMAIL),
            Map.entry("notification.reservation.status.refused_post_contact", MailType.RESERVATION_STATUS_REFUSED_POST_CONTACT_EMAIL),
            Map.entry("notification.reservation.status.canceled_post_confirmation", MailType.RESERVATION_STATUS_CANCELED_POST_CONFIRMATION_EMAIL),
            Map.entry("notification.reservation.status.canceled_by_client_pre_contact", MailType.RESERVATION_STATUS_CANCELED_BY_CLIENT_PRE_CONTACT_EMAIL),
            Map.entry("notification.reservation.status.canceled_by_client_post_contact", MailType.RESERVATION_STATUS_CANCELED_BY_CLIENT_POST_CONTACT_EMAIL),
            Map.entry("notification.reservation.note_added", MailType.RESERVATION_NOTE_ADDED_EMAIL),
            Map.entry("notification.reservation.document_added", MailType.RESERVATION_DOCUMENT_ADDED_EMAIL)
    );

    private final MailerClient mailerClient;
    private final String frontendUrl;

    public NotificationMailerService(MailerClient mailerClient, @Value("${sgilt.frontend.url}") String frontendUrl) {
        this.mailerClient = mailerClient;
        this.frontendUrl = frontendUrl;
    }

    /**
     * Envoie l'email correspondant à la notification, si sa clé i18n est connue de
     * {@link #MAIL_TYPE_BY_MESSAGE_KEY}.
     *
     * @param notification la notification déjà construite et persistée
     */
    public void sendNotificationEmail(Notification notification) {
        MailType mailType = MAIL_TYPE_BY_MESSAGE_KEY.get(notification.getMessageKey());
        if (mailType == null) {
            log.warn("Pas de MailType associé à la clé {} — email non envoyé", notification.getMessageKey());
            return;
        }

        Map<String, Object> context = new HashMap<>(notification.getParams());
        context.put("actionUrl", frontendUrl + notification.getHref());

        mailerClient.sendMail(new MailRequest(notification.getRecipientEmail(), mailType, context, null));
    }
}
