package net.franzka.sgilt.core.evenement.dto;

public record EventPatchDto(
        String lieu,
        String sharedNote,
        String eventType,
        String ambiance,
        String ville,
        String nbInvites,
        String description,
        String momentCle
) {}
