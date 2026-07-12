package net.franzka.sgilt.core.notification.event;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Évènement Spring in-process publié quand une réservation est créée — faits bruts, pas de texte
 * pré-rendu. Republié sur RabbitMQ par {@link net.franzka.sgilt.core.notification.ReservationCreatedEventListener}
 * une fois la transaction commitée. Structure identique (mêmes noms de champs) à l'enregistrement
 * consommé côté {@code sgilt-notifications}.
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
