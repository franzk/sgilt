package net.franzka.sgilt.core.reservation.event.reservationstatuschanged;

import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.event.ActorRole;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Évènement Spring in-process générique publié pour toute transition de statut d'une réservation,
 * dans les deux sens : prestataire agit (prise de contact, confirmation, refus, annulation
 * post-confirmation → client notifié) ou client agit (annulation → prestataire notifié) — faits
 * bruts, pas de texte pré-rendu. Republié sur RabbitMQ par {@link ReservationStatusChangedEventListener}
 * une fois la transaction commitée.
 * <p>
 * Un seul évènement pour les deux sens plutôt qu'un par transition ou par direction : {@code status}
 * porte la valeur cible (dérive la clé i18n côté sgilt-notifications), {@code actorRole} identifie
 * explicitement qui a agi (déduit du statut, mais porté en clair pour rester lisible sans avoir à
 * relire la machine à états). {@code actorName} est calculé par l'appelant ({@code ReservationService})
 * plutôt que par le mapper — {@code Prestataire.name} est déjà un champ unique, mais le nom du client
 * nécessite une concaténation prénom/nom que MapStruct n'exprime pas proprement en déclaratif.
 *
 * @param reservationId    l'identifiant de la réservation
 * @param eventId          l'identifiant de l'évènement concerné — nécessaire à la construction du
 *                          lien de navigation côté client ({@code /app/events/{eventId}/reservations/{reservationId}})
 * @param recipientUserId  l'identifiant {@code Utilisateur} du destinataire à notifier (traçabilité)
 * @param recipientEmail   l'email du destinataire à notifier — clé de résolution du destinataire
 * @param status           le nouveau statut de la réservation
 * @param actorName        le nom de la personne à l'origine du changement
 * @param actorRole        le rôle de la personne à l'origine du changement
 * @param eventTitle       le titre de l'évènement concerné
 * @param eventDate        la date de prestation
 */
public record ReservationStatusChangedEvent(
        UUID reservationId,
        UUID eventId,
        UUID recipientUserId,
        String recipientEmail,
        ReservationStatus status,
        String actorName,
        ActorRole actorRole,
        String eventTitle,
        LocalDate eventDate
) {

    /**
     * Routing key utilisée pour publier cet évènement sur l'exchange
     * {@value net.franzka.sgilt.core.notifier.DomainEventsConfig#DOMAIN_EVENTS_EXCHANGE}.
     */
    public static final String ROUTING_KEY = "reservation.status_changed";
}
