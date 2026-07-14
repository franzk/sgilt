package net.franzka.sgilt.core.reservation.event.reservationfeeditemadded;

import net.franzka.sgilt.core.reservation.event.ActorRole;
import net.franzka.sgilt.core.reservation.event.FeedItemType;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Évènement Spring in-process générique publié quand une note ou un document est ajouté au feed
 * d'une réservation, dans les deux sens (client ajoute → prestataire notifié, ou prestataire ajoute
 * → client notifié) — faits bruts, pas de texte pré-rendu. Republié sur RabbitMQ par
 * {@link ReservationFeedItemAddedEventListener} une fois la transaction commitée.
 * <p>
 * Un seul évènement pour les 4 cas (note/document × client/prestataire) plutôt que quatre : même
 * mécanique de normalisation que {@code ReservationStatusChangedEvent} ({@code itemType} au lieu de
 * {@code status}). Contrairement au statut, {@code itemType} détermine directement le
 * {@code NotificationType} côté sgilt-notifications (NEW_NOTE/NEW_DOCUMENT), pas seulement la clé i18n
 * — reste un évènement distinct de {@code ReservationStatusChangedEvent} pour cette raison.
 * <p>
 * N'est publié que pour les éléments non personnels — {@code ReservationFeedService} filtre avant
 * publication, un élément {@code isPersonal} n'est jamais notifié à l'autre partie.
 *
 * @param reservationId    l'identifiant de la réservation
 * @param eventId          l'identifiant de l'évènement concerné — nécessaire à la construction du
 *                          lien de navigation côté client ({@code /app/events/{eventId}/reservations/{reservationId}})
 * @param recipientUserId  l'identifiant {@code Utilisateur} du destinataire à notifier (traçabilité)
 * @param recipientEmail   l'email du destinataire à notifier — clé de résolution du destinataire
 * @param itemType         le type d'élément ajouté (note ou document)
 * @param actorName        le nom de la personne à l'origine de l'ajout
 * @param actorRole        le rôle de la personne à l'origine de l'ajout
 * @param eventTitle       le titre de l'évènement concerné
 * @param eventDate        la date de prestation
 */
public record ReservationFeedItemAddedEvent(
        UUID reservationId,
        UUID eventId,
        UUID recipientUserId,
        String recipientEmail,
        FeedItemType itemType,
        String actorName,
        ActorRole actorRole,
        String eventTitle,
        LocalDate eventDate
) {

    /**
     * Routing key utilisée pour publier cet évènement sur l'exchange
     * {@value net.franzka.sgilt.core.notifier.DomainEventsConfig#DOMAIN_EVENTS_EXCHANGE}.
     */
    public static final String ROUTING_KEY = "reservation.feed_item_added";
}
