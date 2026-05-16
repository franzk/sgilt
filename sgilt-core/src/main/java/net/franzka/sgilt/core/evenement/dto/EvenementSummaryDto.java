package net.franzka.sgilt.core.evenement.dto;

import java.time.LocalDate;
import java.util.UUID;

public record EvenementSummaryDto(
        UUID id,
        String title,
        LocalDate date,
        String ville,
        String imagePath,
        String eventType,
        int confirmedCount,
        int inDiscussionCount
) {}
