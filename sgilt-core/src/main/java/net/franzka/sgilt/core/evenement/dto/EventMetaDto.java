package net.franzka.sgilt.core.evenement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.franzka.sgilt.core.evenement.page.domain.PublicPageStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Métadonnées d'un événement pour l'EventBoard : ses données (horaires compris), sa dernière
 * modification, les coordonnées de son propriétaire, l'état de sa page publique (null si le
 * back-office ne l'a pas activée) et l'interrupteur de sa billetterie.
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
        ClientInfoDto clientInfo,
        PublicPageStatus publicPageStatus,
        // Interrupteur du back-office : la billetterie est activée.
        boolean billetterieEnabled,
        // Sans secondes ('19:00') ; null si l'événement n'en a pas.
        @JsonFormat(pattern = "HH:mm") LocalTime startTime,
        @JsonFormat(pattern = "HH:mm") LocalTime endTime
) {}
