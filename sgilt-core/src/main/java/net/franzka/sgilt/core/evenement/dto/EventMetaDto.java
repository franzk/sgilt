package net.franzka.sgilt.core.evenement.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Métadonnées d'un événement pour l'EventBoard : ses données, sa dernière modification et les
 * coordonnées de son propriétaire.
 */
public record EventMetaDto(
        UUID id,
        String title,
        LocalDate date,
        String eventType,
        String ambiance,
        String ville,
        String lieu,
        String nbInvites,
        String imagePath,
        String sharedNote,
        String description,
        String momentCle,
        LocalDateTime lastUpdateDate,
        ClientInfoDto clientInfo
) {}
