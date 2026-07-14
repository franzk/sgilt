package net.franzka.sgilt.notifications.notification.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Représentation d'une notification exposée à l'API
 * Pas de texte pré-rendu : {@code messageKey} est une clé i18n résolue côté front,
 * {@code params} porte les faits bruts nécessaires à son
 * interpolation (ex. {@code eventTitle}, {@code prestataireName}).
 *
 * @param id         l'identifiant de la notification
 * @param type       le type, en snake_case (ex. {@code "new_request"})
 * @param read       si la notification a été lue
 * @param createdAt  la date de création (sérialisée en ISO-8601)
 * @param messageKey la clé i18n du message affiché
 * @param params     les paramètres d'interpolation du message
 * @param href       la cible de navigation au clic
 */
public record NotificationDto(
        UUID id,
        String type,
        boolean read,
        Instant createdAt,
        String messageKey,
        Map<String, String> params,
        String href
) {}
