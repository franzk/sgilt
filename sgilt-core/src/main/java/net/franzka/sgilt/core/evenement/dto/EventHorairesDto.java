package net.franzka.sgilt.core.evenement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

/**
 * Horaires d'un événement saisis par son organisateur, remplacés tous les deux à la fois : une heure
 * à null est effacée.
 *
 * @param startTime l'heure de début, ou null
 * @param endTime   l'heure de fin (le lendemain si avant le début), ou null
 */
public record EventHorairesDto(
        // Sans secondes ('19:00').
        @JsonFormat(pattern = "HH:mm") LocalTime startTime,
        @JsonFormat(pattern = "HH:mm") LocalTime endTime
) {}
