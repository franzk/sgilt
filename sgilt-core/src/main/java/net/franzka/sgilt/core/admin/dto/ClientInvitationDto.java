package net.franzka.sgilt.core.admin.dto;

import net.franzka.sgilt.core.admin.domain.ClientInvitationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Une invitation client du BO : le client invité, son événement, le prestataire réservé et l'état
 * de l'invitation.
 *
 * @param id              l'identifiant de l'utilisateur invité
 * @param firstName       le prénom du client invité
 * @param lastName        le nom du client invité
 * @param email           l'email du client invité
 * @param eventType       le type de son événement
 * @param eventDate       la date de son événement
 * @param prestataireName le nom du prestataire chez qui la réservation confirmée a été créée
 * @param status          l'état de l'invitation
 * @param linkSentAt      la date d'envoi du lien d'activation en attente, {@code null} si non envoyée
 * @param linkExpiresAt   l'expiration du lien d'activation en attente, {@code null} si non envoyée
 */
public record ClientInvitationDto(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String eventType,
        LocalDate eventDate,
        String prestataireName,
        ClientInvitationStatus status,
        LocalDateTime linkSentAt,
        LocalDateTime linkExpiresAt
) {}
