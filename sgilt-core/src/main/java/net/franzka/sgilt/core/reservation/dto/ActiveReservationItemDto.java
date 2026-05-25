package net.franzka.sgilt.core.reservation.dto;

import java.util.UUID;

public record ActiveReservationItemDto(
        UUID reservationId,
        String status,
        UUID evenementId,
        String evenementTitle,
        String prestataireSlug,
        String prestataireName,
        String prestataireAvatar
) {}
