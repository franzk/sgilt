package net.franzka.sgilt.notifications.notification.mapper;

import net.franzka.sgilt.notifications.notification.domain.Notification;
import net.franzka.sgilt.notifications.notification.domain.NotificationType;
import net.franzka.sgilt.notifications.notification.event.ReservationConfirmedEvent;
import net.franzka.sgilt.notifications.notification.event.ReservationCreatedEvent;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationEventMapperTest {

    private final NotificationEventMapper mapper = new NotificationEventMapper();

    @Nested
    class ToNotificationFromReservationCreated {

        @Test
        void givenEvent_whenToNotification_thenBuildsExpectedNotification() {
            UUID reservationId = UUID.randomUUID();
            UUID recipientUserId = UUID.randomUUID();
            ReservationCreatedEvent event = new ReservationCreatedEvent(
                    reservationId, recipientUserId, "presta@example.com",
                    "Sophie", "Leroy", "Anniversaire de Paul", LocalDate.now());

            Notification notification = mapper.toNotification(event);

            assertThat(notification.getRecipientEmail()).isEqualTo("presta@example.com");
            assertThat(notification.getRecipientUserId()).isEqualTo(recipientUserId);
            assertThat(notification.getType()).isEqualTo(NotificationType.NEW_REQUEST);
            assertThat(notification.getMessageKey()).isEqualTo("notification.reservation.new_request");
            assertThat(notification.getParams()).containsEntry("clientFirstName", "Sophie")
                    .containsEntry("clientLastName", "Leroy")
                    .containsEntry("eventTitle", "Anniversaire de Paul");
            assertThat(notification.getHref()).isEqualTo("/pro/reservations/" + reservationId);
            assertThat(notification.isRead()).isFalse();
        }
    }

    @Nested
    class ToNotificationFromReservationConfirmed {

        @Test
        void givenEvent_whenToNotification_thenBuildsExpectedNotification() {
            UUID reservationId = UUID.randomUUID();
            UUID recipientUserId = UUID.randomUUID();
            ReservationConfirmedEvent event = new ReservationConfirmedEvent(
                    reservationId, recipientUserId, "client@example.com",
                    "Studio Fleur", "Anniversaire de Paul", LocalDate.now());

            Notification notification = mapper.toNotification(event);

            assertThat(notification.getRecipientEmail()).isEqualTo("client@example.com");
            assertThat(notification.getRecipientUserId()).isEqualTo(recipientUserId);
            assertThat(notification.getType()).isEqualTo(NotificationType.STATE_CHANGE);
            assertThat(notification.getMessageKey()).isEqualTo("notification.reservation.confirmed");
            assertThat(notification.getParams()).containsEntry("prestataireName", "Studio Fleur")
                    .containsEntry("eventTitle", "Anniversaire de Paul");
            assertThat(notification.getHref()).isEqualTo("/user/reservations/" + reservationId);
            assertThat(notification.isRead()).isFalse();
        }
    }
}
