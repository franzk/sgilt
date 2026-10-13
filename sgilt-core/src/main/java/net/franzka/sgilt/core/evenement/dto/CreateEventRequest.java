package net.franzka.sgilt.core.evenement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Événement à créer : ses données et, s'il naît d'une demande à un prestataire, cette première
 * demande. Ses rubriques ne sont jamais transmises : le back les déduit du template de son type
 * (et y range la demande).
 *
 * @param title   le nom choisi pour l'événement ; vide, il reçoit un nom par défaut
 * @param demande la première demande, {@code null} pour un événement créé sans prestataire
 */
public record CreateEventRequest(
        @Size(max = 255) String title,
        @Size(max = 100) String eventType,
        @Size(max = 100) String ambiance,
        @Size(max = 100) String momentCle,
        @Size(max = 2000) String description,
        LocalDate date,
        @Size(max = 100) String ville,
        @Size(max = 20) String nbInvites,
        @Size(max = 200) String lieu,
        @Valid DemandeInitieeDto demande
) {}
