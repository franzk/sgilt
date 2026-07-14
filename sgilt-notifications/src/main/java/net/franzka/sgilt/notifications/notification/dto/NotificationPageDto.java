package net.franzka.sgilt.notifications.notification.dto;

import java.util.List;

/**
 * Une page de notifications pour l'appelant authentifié.
 *
 * @param items   les notifications de la page, plus récentes d'abord
 * @param hasMore s'il existe une page suivante
 */
public record NotificationPageDto(
        List<NotificationDto> items,
        boolean hasMore
) {}
