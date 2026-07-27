package net.franzka.sgilt.core.reservation.dto;

import net.franzka.sgilt.core.reservation.domain.ReservationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO allégé pour l'affichage en liste dans le back-office admin.
 */
public record AdminReservationListItemDto(
        UUID id,
        String eventTitle,
        String organizerEmail,
        String providerEmail,
        String providerSlug,
        ReservationStatus status,
        LocalDateTime createdAt
) {}
