package net.franzka.sgilt.core.reservation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Représente un élément du feed d'une réservation (note ou document).
 * Le champ {@code type} détermine le type de l'item ("note" | "document").
 * Les champs non applicables au type sont null.
 */
public record FeedItemDto(

        // ── Commun ──────────────────────────────────────────────────────────────
        String type,
        UUID id,
        String authorId,
        String authorName,
        String authorPhoto,
        String authorRole,
        LocalDateTime createdAt,
        String title,

        // ── Note ────────────────────────────────────────────────────────────────
        String content,
        Boolean isPersonal,
        Boolean isMessageInitial,

        // ── Document ─────────────────────────────────────────────────────────────
        String name,
        String fileType,
        String url
) {}
