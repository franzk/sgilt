package net.franzka.sgilt.core.reservation.event.reservationcontactreminderdue;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Évènement Spring in-process publié par {@code ReservationReminderService} pour chaque réservation
 * restée NEW (en attente de contact) depuis 7 jours — faits bruts, pas de texte pré-rendu. Republié
 * sur RabbitMQ par {@link ReservationContactReminderDueEventListener} une fois la transaction
 * commitée. Destiné au client : l'inviter à mettre à jour le statut, ou à signaler un prestataire
 * qui ne l'a pas contacté.
 *
 * @param reservationId      l'identifiant de la réservation
 * @param eventId            l'identifiant de l'évènement concerné — nécessaire à la construction du
 *                           lien de navigation côté client
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
) {

    /**
     * Routing key utilisée pour publier cet évènement sur l'exchange
     * {@value net.franzka.sgilt.core.notifier.DomainEventsConfig#DOMAIN_EVENTS_EXCHANGE}.
     */
    public static final String ROUTING_KEY = "reservation.contact_reminder_due";
}
