package net.franzka.sgilt.notifications.notification.event;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Faits bruts publiés par sgilt-core quand une note ou un document est ajouté au feed d'une
 * réservation, dans les deux sens (voir {@code actorRole}) — jamais publié pour un élément personnel.
 * {@code itemType} est une {@code String} ({@code "NOTE"}/{@code "DOCUMENT"}) plutôt qu'un enum
 * dupliqué — détermine le {@code NotificationType} et la clé i18n côté
 * {@link net.franzka.sgilt.notifications.notification.mapper.NotificationEventMapper}.
 *
 * @param reservationId    l'identifiant de la réservation
 * @param eventId          l'identifiant de l'évènement concerné
 * @param recipientUserId  l'identifiant {@code Utilisateur} du destinataire à notifier (traçabilité)
 * @param recipientEmail   l'email du destinataire à notifier — clé de résolution du destinataire
 * @param itemType         le type d'élément ajouté ({@code "NOTE"} ou {@code "DOCUMENT"})
 * @param actorName        le nom de la personne à l'origine de l'ajout
 * @param actorRole        le rôle de la personne à l'origine de l'ajout ({@code USER}/{@code PRO}/{@code ADMIN})
 * @param eventTitle       le titre de l'évènement concerné
 * @param eventDate        la date de prestation
 */
public record ReservationFeedItemAddedEvent(
        UUID reservationId,
        UUID eventId,
        UUID recipientUserId,
        String recipientEmail,
        String itemType,
        String actorName,
        String actorRole,
        String eventTitle,
        LocalDate eventDate
) {}
