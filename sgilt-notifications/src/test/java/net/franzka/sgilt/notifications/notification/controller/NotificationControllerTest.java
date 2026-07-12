package net.franzka.sgilt.notifications.notification.controller;

import net.franzka.sgilt.notifications.notification.domain.Notification;
import net.franzka.sgilt.notifications.notification.domain.NotificationType;
import net.franzka.sgilt.notifications.notification.dto.NotificationDto;
import net.franzka.sgilt.notifications.notification.dto.NotificationPageDto;
import net.franzka.sgilt.notifications.notification.mapper.NotificationMapper;
import net.franzka.sgilt.notifications.notification.service.CurrentUserService;
import net.franzka.sgilt.notifications.notification.service.NotificationService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private static final String EMAIL = "presta@example.com";

    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private NotificationController controller;

    @Nested
    class GetPage {

        @Test
        void givenNotifications_whenGetPage_thenReturnsMappedPage() {
            Notification notification = Notification.builder()
                    .id(UUID.randomUUID())
                    .recipientEmail(EMAIL)
                    .type(NotificationType.NEW_REQUEST)
                    .title("Nouvelle demande")
                    .href("/pro/reservations/x")
                    .read(false)
                    .createdAt(Instant.now())
                    .build();
            NotificationDto dto = new NotificationDto(notification.getId(), "new_request", false, notification.getCreatedAt(), "Nouvelle demande", null, "/pro/reservations/x");
            Page<Notification> page = new PageImpl<>(List.of(notification), PageRequest.of(0, 10), 1);

            when(currentUserService.getEmail()).thenReturn(EMAIL);
            when(notificationService.getPageForRecipient(EMAIL, 0)).thenReturn(page);
            when(notificationMapper.toDto(notification)).thenReturn(dto);

            ResponseEntity<NotificationPageDto> response = controller.getPage(0);

            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().items()).containsExactly(dto);
            assertThat(response.getBody().hasMore()).isFalse();
        }
    }

    @Nested
    class MarkRead {

        @Test
        void givenNotificationId_whenMarkRead_thenDelegatesToServiceWithCallerEmail() {
            UUID id = UUID.randomUUID();
            when(currentUserService.getEmail()).thenReturn(EMAIL);

            ResponseEntity<Void> response = controller.markRead(id);

            verify(notificationService).markRead(EMAIL, id);
            assertThat(response.getStatusCode().value()).isEqualTo(204);
        }
    }

    @Nested
    class MarkAllRead {

        @Test
        void whenMarkAllRead_thenDelegatesToServiceWithCallerEmail() {
            when(currentUserService.getEmail()).thenReturn(EMAIL);

            ResponseEntity<Void> response = controller.markAllRead();

            verify(notificationService).markAllRead(EMAIL);
            assertThat(response.getStatusCode().value()).isEqualTo(204);
        }
    }
}
