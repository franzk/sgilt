package net.franzka.sgilt.core.evenement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Demande à un prestataire initiée dans un événement pas encore créé : elle devient une vraie
 * réservation à la création de l'événement.
 *
 * @param prestataireId le prestataire visé
 * @param message       le message qui lui est adressé
 */
public record DemandeInitieeDto(
        @NotNull UUID prestataireId,
        @Size(max = 1000) String message
) {}
