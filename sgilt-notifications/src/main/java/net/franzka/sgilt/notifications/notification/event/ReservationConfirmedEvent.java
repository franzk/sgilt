package net.franzka.sgilt.notifications.notification.event;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Faits bruts publiés par sgilt-core quand une réservation passe à CONFIRMED — pas de texte
 * pré-rendu, c'est à {@link net.franzka.sgilt.notifications.notification.service.NotificationService}
 * de construire le titre/corps/lien affichés. Contrairement à {@link ReservationCreatedEvent}, le
 * destinataire ici est le client, pas le prestataire.
 *
 * @param reservationId    l'identifiant de la réservation confirmée
 * @param recipientUserId  l'identifiant {@code Utilisateur} du client à notifier (traçabilité)
 * @param recipientEmail   l'email du client à notifier — clé de résolution du destinataire
 * @param prestataireName  le nom du prestataire ayant confirmé
 * @param eventTitle       le titre de l'évènement concerné
 * @param eventDate        la date de prestation confirmée
 */
public record ReservationConfirmedEvent(
        UUID reservationId,
        UUID recipientUserId,
        String recipientEmail,
        String prestataireName,
        String eventTitle,
        LocalDate eventDate
) {}
