package net.franzka.sgilt.core.reservation.event.reservationaddedbysgilt;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Évènement Spring in-process publié quand Sgilt crée depuis le BO une réservation directement
 * confirmée, pour un client invité dont le deal s'est conclu hors Sgilt — faits bruts, pas de texte
 * pré-rendu. Seul le prestataire est notifié. Republié sur RabbitMQ par
 * {@link ReservationAddedBySgiltEventListener} une fois la transaction commitée. Structure identique
 * (mêmes noms de champs) à l'enregistrement consommé côté {@code sgilt-notifications}.
 *
 * @param reservationId   l'identifiant de la réservation créée
 * @param recipientUserId l'identifiant {@code Utilisateur} du prestataire à notifier (traçabilité)
 * @param recipientEmail  l'email du prestataire à notifier — clé de résolution du destinataire
 * @param clientFirstName le prénom du client invité
 * @param clientLastName  le nom du client invité
 * @param eventDate       la date de la prestation
 */
public record ReservationAddedBySgiltEvent(
        UUID reservationId,
        UUID recipientUserId,
        String recipientEmail,
        String clientFirstName,
        String clientLastName,
        LocalDate eventDate
) {

    /**
     * Routing key utilisée pour publier cet évènement sur l'exchange
     * {@value net.franzka.sgilt.core.notifier.DomainEventsConfig#DOMAIN_EVENTS_EXCHANGE}.
     */
    public static final String ROUTING_KEY = "reservation.added_by_sgilt";
}
