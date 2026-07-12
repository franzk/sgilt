package net.franzka.sgilt.notifications.notification.event;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Faits bruts publiés par sgilt-core quand une réservation est créée — pas de texte pré-rendu,
 * c'est à {@link net.franzka.sgilt.notifications.notification.service.NotificationService} de
 * construire le titre/corps/lien affichés.
 *
 * @param reservationId    l'identifiant de la réservation créée
 * @param recipientUserId  l'identifiant {@code Utilisateur} du prestataire à notifier (traçabilité)
 * @param recipientEmail   l'email du prestataire à notifier — clé de résolution du destinataire
 * @param clientFirstName  le prénom du client à l'origine de la demande
 * @param clientLastName   le nom du client à l'origine de la demande
 * @param eventTitle       le titre de l'évènement concerné
 * @param eventDate        la date de prestation demandée
 */
public record ReservationCreatedEvent(
        UUID reservationId,
        UUID recipientUserId,
        String recipientEmail,
        String clientFirstName,
        String clientLastName,
        String eventTitle,
        LocalDate eventDate
) {}
