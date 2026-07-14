package net.franzka.sgilt.notifications.notification.event;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Faits bruts publiés par sgilt-core pour toute transition de statut d'une réservation, dans les
 * deux sens (prestataire agit → client notifié, ou client agit → prestataire notifié — voir
 * {@code actorRole}). {@code status} et {@code actorRole} sont de simples {@code String} plutôt que
 * des enums dupliqués — la seule utilisation de {@code status} est de dériver la clé i18n
 * ({@code notification.reservation.status.<status en minuscules>}), {@code actorRole} n'est pas
 * encore consommé (porté pour un futur usage admin, cf. chantier notifications point C).
 *
 * @param reservationId    l'identifiant de la réservation
 * @param eventId          l'identifiant de l'évènement concerné
 * @param recipientUserId  l'identifiant {@code Utilisateur} du destinataire à notifier (traçabilité)
 * @param recipientEmail   l'email du destinataire à notifier — clé de résolution du destinataire
 * @param status           le nouveau statut de la réservation
 * @param actorName        le nom de la personne à l'origine du changement
 * @param actorRole        le rôle de la personne à l'origine du changement ({@code USER}/{@code PRO}/{@code ADMIN})
 * @param eventTitle       le titre de l'évènement concerné
 * @param eventDate        la date de prestation
 */
public record ReservationStatusChangedEvent(
        UUID reservationId,
        UUID eventId,
        UUID recipientUserId,
        String recipientEmail,
        String status,
        String actorName,
        String actorRole,
        String eventTitle,
        LocalDate eventDate
) {}
