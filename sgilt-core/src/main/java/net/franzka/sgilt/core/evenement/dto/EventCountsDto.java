package net.franzka.sgilt.core.evenement.dto;

public record EventCountsDto(
        String mood,
        int confirmedCount,
        int inDiscussionCount,
        int nouvelleCount,
        int refuseeCount,
        int annuleeCount,
        int realiseeCount
) {}
