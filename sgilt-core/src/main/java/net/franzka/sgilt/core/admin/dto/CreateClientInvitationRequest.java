package net.franzka.sgilt.core.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO de la requête admin d'invitation d'un client dont le deal s'est conclu hors Sgilt : le
 * client, le type et la date de son événement, et le prestataire chez qui la réservation est
 * créée directement confirmée.
 */
public record CreateClientInvitationRequest(
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Size(max = 100) String eventType,
        @NotNull LocalDate eventDate,
        @NotNull UUID prestataireId
) {}
