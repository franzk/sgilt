package net.franzka.sgilt.core.evenement.dto;

import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;

import java.util.List;

/**
 * Rubrique d'un événement pour l'EventBoard, avec les réservations qui y sont rangées.
 *
 * @param key          la clé de la rubrique ('lieu', 'autre'…)
 * @param reservations les réservations de la rubrique
 */
public record EventRubriqueDto(
        String key,
        List<ReservationSummaryDto> reservations
) {}
