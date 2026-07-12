package net.franzka.sgilt.notifications.notification.mapper;

import net.franzka.sgilt.notifications.notification.domain.Notification;
import net.franzka.sgilt.notifications.notification.domain.NotificationType;
import net.franzka.sgilt.notifications.notification.dto.NotificationDto;
import org.mapstruct.Mapper;

/**
 * Mapping entité JPA ↔ DTO exposé par l'API pour le domaine notification.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationDto toDto(Notification notification);

    /**
     * Convertit le type Java (ex. {@code NEW_REQUEST}) vers la forme snake_case attendue par le
     * front (ex. {@code "new_request"}). Utilisée automatiquement par MapStruct pour le champ
     * {@code type} de {@link NotificationDto}.
     *
     * @param type le type de notification
     * @return la représentation snake_case du type
     */
    default String map(NotificationType type) {
        return type.name().toLowerCase();
    }
}
