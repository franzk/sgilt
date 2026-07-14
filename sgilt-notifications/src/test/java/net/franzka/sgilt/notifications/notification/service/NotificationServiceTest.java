package net.franzka.sgilt.notifications.notification.service;

import net.franzka.sgilt.notifications.notification.domain.Notification;
import net.franzka.sgilt.notifications.notification.domain.NotificationType;
import net.franzka.sgilt.notifications.notification.event.ReservationCreatedEvent;
import net.franzka.sgilt.notifications.notification.event.ReservationFeedItemAddedEvent;
import net.franzka.sgilt.notifications.notification.event.ReservationStatusChangedEvent;
import net.franzka.sgilt.notifications.notification.exception.NotificationAccessDeniedException;
import net.franzka.sgilt.notifications.notification.exception.NotificationNotFoundException;
import net.franzka.sgilt.notifications.notification.mapper.NotificationEventMapper;
import net.franzka.sgilt.notifications.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Mock
    private NotificationEventMapper notificationEventMapper;

    @InjectMocks
    private NotificationService notificationService;

    @Nested
    class CreateFromEvent {

        @Test
        void givenReservationCreatedEvent_whenCreateFromEvent_thenSavesMappedNotification() {
            ReservationCreatedEvent event = new ReservationCreatedEvent(
                    UUID.randomUUID(), UUID.randomUUID(), "presta@example.com",
                    "Sophie", "Leroy", "Anniversaire de Paul", LocalDate.now());
            Notification mapped = Notification.builder().type(NotificationType.NEW_REQUEST).build();
            when(notificationEventMapper.toNotification(event)).thenReturn(mapped);

            notificationService.createFromEvent(event);

            verify(notificationRepository).save(mapped);
        }

        @Test
        void givenReservationStatusChangedEvent_whenCreateFromEvent_thenSavesMappedNotification() {
            ReservationStatusChangedEvent event = new ReservationStatusChangedEvent(
                    UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "client@example.com",
                    "CONFIRMED", "Studio Fleur", "PRO", "Anniversaire de Paul", LocalDate.now());
            Notification mapped = Notification.builder().type(NotificationType.STATE_CHANGE).build();
            when(notificationEventMapper.toNotification(event)).thenReturn(mapped);

            notificationService.createFromEvent(event);

            verify(notificationRepository).save(mapped);
        }

        @Test
        void givenReservationFeedItemAddedEvent_whenCreateFromEvent_thenSavesMappedNotification() {
            ReservationFeedItemAddedEvent event = new ReservationFeedItemAddedEvent(
                    UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "client@example.com",
                    "NOTE", "Studio Fleur", "PRO", "Anniversaire de Paul", LocalDate.now());
            Notification mapped = Notification.builder().type(NotificationType.NEW_NOTE).build();
            when(notificationEventMapper.toNotification(event)).thenReturn(mapped);

            notificationService.createFromEvent(event);

            verify(notificationRepository).save(mapped);
        }

        @Test
        void givenUnknownEventType_whenCreateFromEvent_thenThrowsIllegalArgument() {
            assertThatThrownBy(() -> notificationService.createFromEvent("not an event"))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(notificationRepository, never()).save(any());
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
