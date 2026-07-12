package net.franzka.sgilt.notifications.notification.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Représentation d'une notification exposée à l'API — correspond au type {@code AppNotification}
 * côté front (`sgilt-front/app/types/notification.ts`).
 *
 * @param id        l'identifiant de la notification
 * @param type      le type, en snake_case (ex. {@code "new_request"})
 * @param read      si la notification a été lue
 * @param createdAt la date de création (sérialisée en ISO-8601)
 * @param title     le titre affiché
 * @param body      le corps affiché, optionnel
 * @param href      la cible de navigation au clic
 */
public record NotificationDto(
        UUID id,
        String type,
        boolean read,
        Instant createdAt,
        String title,
        String body,
        String href
) {}
