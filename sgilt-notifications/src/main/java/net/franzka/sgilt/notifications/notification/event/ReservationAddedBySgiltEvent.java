package net.franzka.sgilt.notifications.notification.event;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Faits bruts publiés par sgilt-core quand Sgilt crée depuis le BO une réservation directement
 * confirmée, pour un client invité dont le deal s'est conclu hors Sgilt — pas de texte pré-rendu,
 * c'est à {@link net.franzka.sgilt.notifications.notification.service.NotificationService} de
 * construire le titre/corps/lien affichés au prestataire.
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
) {}
