package net.franzka.sgilt.core.reservation.event.reservationconfirmed;

import net.franzka.sgilt.core.reservation.event.reservationcreated.ReservationCreatedEvent;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Évènement Spring in-process publié quand une réservation passe à CONFIRMED — faits bruts, pas de
 * texte pré-rendu. Republié sur RabbitMQ par {@link ReservationConfirmedEventListener}
 * une fois la transaction commitée. Structure identique (mêmes noms de champs) à l'enregistrement
 * consommé côté {@code sgilt-notifications}. Contrairement à {@link ReservationCreatedEvent}, le
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
) {

    /**
     * Routing key utilisée pour publier cet évènement sur l'exchange
     * {@value net.franzka.sgilt.core.notifier.DomainEventsConfig#DOMAIN_EVENTS_EXCHANGE}.
     */
    public static final String ROUTING_KEY = "reservation.confirmed";
}
