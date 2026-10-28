package net.franzka.sgilt.notifications.notification.event;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Faits bruts publiés par sgilt-core pour une réservation restée en attente de contact depuis
 * 7 jours — destinés au client, invité à mettre à jour le statut ou à signaler un prestataire qui
 * ne l'a pas contacté.
 *
 * @param reservationId      l'identifiant de la réservation
 * @param eventId            l'identifiant de l'évènement concerné
 * @param recipientUserId    l'identifiant {@code Utilisateur} du client à notifier (traçabilité)
 * @param recipientEmail     l'email du client à notifier — clé de résolution du destinataire
 * @param recipientFirstName le prénom du client, pour la salutation
 * @param prestataireName    le nom du prestataire sollicité
 * @param eventTitle         le titre de l'évènement concerné
 * @param eventDate          la date de prestation
 */
public record ReservationContactReminderDueEvent(
        UUID reservationId,
        UUID eventId,
        UUID recipientUserId,
        String recipientEmail,
        String recipientFirstName,
        String prestataireName,
        String eventTitle,
        LocalDate eventDate
) {}
