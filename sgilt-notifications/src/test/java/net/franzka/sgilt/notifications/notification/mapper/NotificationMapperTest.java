package net.franzka.sgilt.notifications.notification.mapper;

import net.franzka.sgilt.notifications.notification.domain.Notification;
import net.franzka.sgilt.notifications.notification.domain.NotificationType;
import net.franzka.sgilt.notifications.notification.dto.NotificationDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationMapperTest {

    private final NotificationMapper mapper = new NotificationMapperImpl();

    @Nested
    class ToDto {

        @Test
        void givenNotification_whenToDto_thenMapsAllFields() {
            UUID id = UUID.randomUUID();
            Instant createdAt = Instant.now();
            Map<String, String> params = Map.of("clientFirstName", "Sophie", "eventTitle", "Anniversaire de Paul");
            Notification notification = Notification.builder()
                    .id(id)
                    .recipientEmail("presta@example.com")
                    .type(NotificationType.NEW_REQUEST)
                    .messageKey("notification.reservation.new_request")
                    .params(params)
                    .href("/pro/reservations/" + id)
                    .read(false)
                    .createdAt(createdAt)
                    .build();

            NotificationDto dto = mapper.toDto(notification);

            assertThat(dto.id()).isEqualTo(id);
            assertThat(dto.type()).isEqualTo("new_request");
            assertThat(dto.read()).isFalse();
            assertThat(dto.createdAt()).isEqualTo(createdAt);
            assertThat(dto.messageKey()).isEqualTo("notification.reservation.new_request");
            assertThat(dto.params()).isEqualTo(params);
            assertThat(dto.href()).isEqualTo("/pro/reservations/" + id);
        }
    }
}
