package net.franzka.sgilt.core.evenement.dto;

import java.time.LocalDate;
import java.util.UUID;

public record EvenementSummaryDto(
        UUID id,
        String name,
        LocalDate date,
        String ville,
        String coverUrl,
        String eventType,
        int confirmedCount,
        int inDiscussionCount
) {}
