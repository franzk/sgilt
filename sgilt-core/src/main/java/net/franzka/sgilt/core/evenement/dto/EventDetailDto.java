package net.franzka.sgilt.core.evenement.dto;

import java.time.LocalDate;
import java.util.UUID;

public record EventDetailDto(
        UUID id,
        String title,
        LocalDate date,
        String eventType,
        String ambiance,
        String ville,
        String lieu,
        String nbInvites,
        String coverUrl,
        String sharedNote,
        String countdown,
        ClientInfoDto clientInfo
) {}
