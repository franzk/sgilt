package net.franzka.sgilt.notifications.notification.mapper;

import net.franzka.sgilt.notifications.notification.domain.Notification;
import net.franzka.sgilt.notifications.notification.domain.NotificationType;
import net.franzka.sgilt.notifications.notification.event.ReservationConfirmedEvent;
import net.franzka.sgilt.notifications.notification.event.ReservationCreatedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Construit les notifications à partir des évènements de domaine reçus — pas de texte pré-rendu,
 * seulement la clé i18n, ses paramètres d'interpolation et le lien de navigation, résolus/affichés
 * côté front. Un nouveau type d'évènement ajoute une méthode ici, pas une nouvelle classe. Pas de
 * {@code @Mapper} MapStruct : la moitié des champs (clé, params, lien) sont construits, pas
 * renommés depuis l'évènement — ça n'apporterait rien de plus qu'un {@code expression = "java(...)"}
 * planqué dans une annotation.
 */
@Component
public class NotificationEventMapper {

    private static final String MESSAGE_KEY_NEW_REQUEST = "notification.reservation.new_request";
    private static final String MESSAGE_KEY_RESERVATION_CONFIRMED = "notification.reservation.confirmed";

    /**
     * Construit la notification "nouvelle demande de réservation" pour le prestataire concerné.
     *
     * @param event les faits bruts de la réservation créée
     * @return la notification à persister
     */
    public Notification toNotification(ReservationCreatedEvent event) {
        return Notification.builder()
                .recipientEmail(event.recipientEmail())
                .recipientUserId(event.recipientUserId())
                .type(NotificationType.NEW_REQUEST)
                .messageKey(MESSAGE_KEY_NEW_REQUEST)
                .params(Map.of(
                        "clientFirstName", event.clientFirstName(),
                        "clientLastName", event.clientLastName(),
                        "eventTitle", event.eventTitle()))
                .href("/pro/reservations/" + event.reservationId())
                .read(false)
                .build();
    }

    /**
     * Construit la notification "réservation confirmée" pour le client concerné.
     *
     * @param event les faits bruts de la réservation confirmée
     * @return la notification à persister
     */
    public Notification toNotification(ReservationConfirmedEvent event) {
        return Notification.builder()
                .recipientEmail(event.recipientEmail())
                .recipientUserId(event.recipientUserId())
                .type(NotificationType.STATE_CHANGE)
                .messageKey(MESSAGE_KEY_RESERVATION_CONFIRMED)
                .params(Map.of(
                        "prestataireName", event.prestataireName(),
                        "eventTitle", event.eventTitle()))
                .href("/user/reservations/" + event.reservationId())
                .read(false)
                .build();
    }
}
