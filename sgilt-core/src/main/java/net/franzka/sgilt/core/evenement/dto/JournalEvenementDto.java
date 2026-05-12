package net.franzka.sgilt.core.evenement.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record JournalEvenementDto(
        UUID id,
        LocalDateTime createdAt,
        List<ModificationChamp> modifications
) {}
