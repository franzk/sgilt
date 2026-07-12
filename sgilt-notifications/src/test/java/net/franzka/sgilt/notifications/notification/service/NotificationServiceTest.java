package net.franzka.sgilt.notifications.notification.service;

import net.franzka.sgilt.notifications.notification.domain.Notification;
import net.franzka.sgilt.notifications.notification.domain.NotificationType;
import net.franzka.sgilt.notifications.notification.event.ReservationCreatedEvent;
import net.franzka.sgilt.notifications.notification.exception.NotificationAccessDeniedException;
import net.franzka.sgilt.notifications.notification.exception.NotificationNotFoundException;
import net.franzka.sgilt.notifications.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Nested
    class CreateReservationRequestNotification {

        @Test
        void givenEvent_whenCreateReservationRequestNotification_thenSavesExpectedNotification() {
            UUID reservationId = UUID.randomUUID();
            ReservationCreatedEvent event = new ReservationCreatedEvent(
                    reservationId, UUID.randomUUID(), "presta@example.com",
                    "Sophie", "Leroy", "Anniversaire de Paul", LocalDate.now());

            notificationService.createReservationRequestNotification(event);

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());
            Notification saved = captor.getValue();

            assertThat(saved.getRecipientEmail()).isEqualTo("presta@example.com");
            assertThat(saved.getRecipientUserId()).isEqualTo(event.recipientUserId());
            assertThat(saved.getType()).isEqualTo(NotificationType.NEW_REQUEST);
            assertThat(saved.isRead()).isFalse();
            assertThat(saved.getHref()).isEqualTo("/pro/reservations/" + reservationId);
            assertThat(saved.getBody()).contains("Sophie L.").contains("Anniversaire de Paul");
        }
    }

    @Nested
    class GetPageForRecipient {

        @Test
        void givenRecipient_whenGetPageForRecipient_thenQueriesByRecipientEmailOrderedByCreatedAtDesc() {
            String email = "presta@example.com";
            Page<Notification> page = Page.empty();
            when(notificationRepository.findByRecipientEmailOrderByCreatedAtDesc(eq(email), any(PageRequest.class)))
                    .thenReturn(page);

            Page<Notification> result = notificationService.getPageForRecipient(email, 0);

            assertThat(result).isSameAs(page);
        }
    }

    @Nested
    class MarkRead {

        @Test
        void givenNotificationNotFound_whenMarkRead_thenThrowsNotFound() {
            UUID id = UUID.randomUUID();
            when(notificationRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> notificationService.markRead("presta@example.com", id))
                    .isInstanceOf(NotificationNotFoundException.class);

            verify(notificationRepository, never()).save(any());
        }

        @Test
        void givenNotificationBelongsToSomeoneElse_whenMarkRead_thenThrowsAccessDenied() {
            UUID id = UUID.randomUUID();
            Notification notification = Notification.builder().id(id).recipientEmail("other@example.com").build();
            when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));

            assertThatThrownBy(() -> notificationService.markRead("presta@example.com", id))
                    .isInstanceOf(NotificationAccessDeniedException.class);

            verify(notificationRepository, never()).save(any());
        }

        @Test
        void givenOwnNotification_whenMarkRead_thenMarksAsReadAndSaves() {
            UUID id = UUID.randomUUID();
            Notification notification = Notification.builder().id(id).recipientEmail("presta@example.com").read(false).build();
            when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));

            notificationService.markRead("presta@example.com", id);

            assertThat(notification.isRead()).isTrue();
            verify(notificationRepository).save(notification);
        }
    }

    @Nested
    class MarkAllRead {

        @Test
        void givenUnreadNotifications_whenMarkAllRead_thenAllMarkedAsReadAndSaved() {
            String email = "presta@example.com";
            Notification n1 = Notification.builder().id(UUID.randomUUID()).recipientEmail(email).read(false).build();
            Notification n2 = Notification.builder().id(UUID.randomUUID()).recipientEmail(email).read(false).build();
            when(notificationRepository.findByRecipientEmailAndReadFalse(email)).thenReturn(List.of(n1, n2));

            notificationService.markAllRead(email);

            assertThat(n1.isRead()).isTrue();
            assertThat(n2.isRead()).isTrue();
            verify(notificationRepository, times(1)).saveAll(List.of(n1, n2));
        }
    }
}
