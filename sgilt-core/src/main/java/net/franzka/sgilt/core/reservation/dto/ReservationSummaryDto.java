package net.franzka.sgilt.core.reservation.dto;

import java.util.UUID;

public record ReservationSummaryDto(
        UUID id,
        UUID prestataireId,
        String prestataireName,
        String prestatairePhoto,
        String category,
        String status,
        int unreadNotesCount
) {}
