package net.franzka.sgilt.notifications.notification.mapper;

import net.franzka.sgilt.notifications.notification.domain.Notification;
import net.franzka.sgilt.notifications.notification.domain.NotificationType;
import net.franzka.sgilt.notifications.notification.event.ReservationAddedBySgiltEvent;
import net.franzka.sgilt.notifications.notification.event.ReservationContactReminderDueEvent;
import net.franzka.sgilt.notifications.notification.event.ReservationCreatedEvent;
import net.franzka.sgilt.notifications.notification.event.ReservationFeedItemAddedEvent;
import net.franzka.sgilt.notifications.notification.event.ReservationStatusChangedEvent;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

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
    private static final String MESSAGE_KEY_STATUS_PREFIX = "notification.reservation.status.";
    private static final String MESSAGE_KEY_NOTE_ADDED = "notification.reservation.note_added";
    private static final String MESSAGE_KEY_DOCUMENT_ADDED = "notification.reservation.document_added";
    private static final String MESSAGE_KEY_DOCUMENTS_ADDED = "notification.reservation.documents_added";
    private static final String MESSAGE_KEY_CONTACT_REMINDER = "notification.reservation.contact_reminder";
    private static final String MESSAGE_KEY_ADDED_BY_SGILT = "notification.reservation.added_by_sgilt";

    // Date de prestation telle qu'affichée au prestataire, in-app comme dans le mail (« 12 juin 2027 »).
    private static final DateTimeFormatter EVENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);

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
     * Construit la notification de changement de statut, dans les deux sens (prestataire agit →
     * client notifié, ou client agit → prestataire notifié — {@code event.actorRole()} porte la
     * direction). La clé i18n se dérive du statut ({@code notification.reservation.status.<status en
     * minuscules>}).
     *
     * @param event les faits bruts du changement de statut
     * @return la notification à persister
     */
    public Notification toNotification(ReservationStatusChangedEvent event) {
        return Notification.builder()
                .recipientEmail(event.recipientEmail())
                .recipientUserId(event.recipientUserId())
                .type(NotificationType.STATE_CHANGE)
                .messageKey(MESSAGE_KEY_STATUS_PREFIX + event.status().toLowerCase())
                .params(Map.of(
                        "actorName", event.actorName(),
                        "eventTitle", event.eventTitle()))
                .href(resolveHref(event.actorRole(), event.eventId(), event.reservationId()))
                .read(false)
                .build();
    }

    /**
     * Construit la notification d'ajout au feed (note ou document), dans les deux sens — même
     * dérivation de direction que {@link #toNotification(ReservationStatusChangedEvent)}.
     * {@code itemType} détermine le {@link NotificationType} et la clé i18n ; un envoi de plusieurs
     * documents a sa propre clé ({@code documents_added}), avec leur nombre en paramètre
     * ({@code documentCount}) — pas de pluriel à gérer dans les libellés ni dans les mails.
     *
     * @param event les faits bruts de l'ajout au feed
     * @return la notification à persister
     */
    public Notification toNotification(ReservationFeedItemAddedEvent event) {
        boolean isNote = "NOTE".equals(event.itemType());
        int documentCount = event.itemCount() != null ? event.itemCount() : 1;
        boolean severalDocuments = !isNote && documentCount > 1;

        Map<String, String> params = severalDocuments
                ? Map.of(
                        "actorName", event.actorName(),
                        "eventTitle", event.eventTitle(),
                        "documentCount", String.valueOf(documentCount))
                : Map.of(
                        "actorName", event.actorName(),
                        "eventTitle", event.eventTitle());

        return Notification.builder()
                .recipientEmail(event.recipientEmail())
                .recipientUserId(event.recipientUserId())
                .type(isNote ? NotificationType.NEW_NOTE : NotificationType.NEW_DOCUMENT)
                .messageKey(isNote ? MESSAGE_KEY_NOTE_ADDED
                        : severalDocuments ? MESSAGE_KEY_DOCUMENTS_ADDED : MESSAGE_KEY_DOCUMENT_ADDED)
                .params(params)
                .href(resolveHref(event.actorRole(), event.eventId(), event.reservationId()))
                .read(false)
                .build();
    }

    /**
     * Construit la notification de rappel au client d'une réservation restée en attente de contact.
     * Toujours destinée au client — lien vers sa page de réservation.
     *
     * @param event les faits bruts du rappel
     * @return la notification à persister
     */
    public Notification toNotification(ReservationContactReminderDueEvent event) {
        return Notification.builder()
                .recipientEmail(event.recipientEmail())
                .recipientUserId(event.recipientUserId())
                .type(NotificationType.REMINDER)
                .messageKey(MESSAGE_KEY_CONTACT_REMINDER)
                .params(Map.of(
                        "recipientFirstName", event.recipientFirstName(),
                        "prestataireName", event.prestataireName(),
                        "eventTitle", event.eventTitle()))
                .href("/app/events/" + event.eventId() + "/reservations/" + event.reservationId())
                .read(false)
                .build();
    }

    /**
     * Construit la notification « réservation ajoutée par Sgilt » pour le prestataire : une
     * réservation créée directement confirmée depuis le BO, pour un client invité, dans laquelle il
     * peut déposer ses documents. Toujours destinée au prestataire — lien vers sa fiche de réservation.
     *
     * @param event les faits bruts de la réservation ajoutée
     * @return la notification à persister
     */
    public Notification toNotification(ReservationAddedBySgiltEvent event) {
        return Notification.builder()
                .recipientEmail(event.recipientEmail())
                .recipientUserId(event.recipientUserId())
                .type(NotificationType.STATE_CHANGE)
                .messageKey(MESSAGE_KEY_ADDED_BY_SGILT)
                .params(Map.of(
                        "clientFirstName", event.clientFirstName(),
                        "clientLastName", event.clientLastName(),
                        "eventDate", event.eventDate().format(EVENT_DATE_FORMATTER)))
                .href("/pro/reservations/" + event.reservationId())
                .read(false)
                .build();
    }

    /**
     * Résout le lien de navigation selon la direction — le client a une page de réservation imbriquée
     * sous l'évènement, pas le prestataire.
     *
     * @param actorRole     le rôle de la personne à l'origine de l'action ({@code "PRO"} si le client
     *                      est le destinataire)
     * @param eventId       l'identifiant de l'évènement concerné
     * @param reservationId l'identifiant de la réservation concernée
     * @return le lien de navigation
     */
    private String resolveHref(String actorRole, UUID eventId, UUID reservationId) {
        boolean clientNotified = "PRO".equals(actorRole);
        return clientNotified
                ? "/app/events/" + eventId + "/reservations/" + reservationId
                : "/pro/reservations/" + reservationId;
    }
}
