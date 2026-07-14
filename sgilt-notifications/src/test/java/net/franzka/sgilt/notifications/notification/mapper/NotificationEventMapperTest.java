package net.franzka.sgilt.notifications.notification.mapper;

import net.franzka.sgilt.notifications.notification.domain.Notification;
import net.franzka.sgilt.notifications.notification.domain.NotificationType;
import net.franzka.sgilt.notifications.notification.event.ReservationCreatedEvent;
import net.franzka.sgilt.notifications.notification.event.ReservationStatusChangedEvent;
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
    class ToNotificationFromReservationStatusChanged {

        @Test
        void givenActorRolePro_whenToNotification_thenBuildsNotificationForClient() {
            UUID reservationId = UUID.randomUUID();
            UUID eventId = UUID.randomUUID();
            UUID recipientUserId = UUID.randomUUID();
            ReservationStatusChangedEvent event = new ReservationStatusChangedEvent(
                    reservationId, eventId, recipientUserId, "client@example.com",
                    "CONFIRMED", "Studio Fleur", "PRO", "Anniversaire de Paul", LocalDate.now());

            Notification notification = mapper.toNotification(event);

            assertThat(notification.getRecipientEmail()).isEqualTo("client@example.com");
            assertThat(notification.getRecipientUserId()).isEqualTo(recipientUserId);
            assertThat(notification.getType()).isEqualTo(NotificationType.STATE_CHANGE);
            assertThat(notification.getMessageKey()).isEqualTo("notification.reservation.status.confirmed");
            assertThat(notification.getParams()).containsEntry("actorName", "Studio Fleur")
                    .containsEntry("eventTitle", "Anniversaire de Paul");
            assertThat(notification.getHref()).isEqualTo("/app/events/" + eventId + "/reservations/" + reservationId);
            assertThat(notification.isRead()).isFalse();
        }

        @Test
        void givenActorRoleUser_whenToNotification_thenBuildsNotificationForPro() {
            UUID reservationId = UUID.randomUUID();
            UUID eventId = UUID.randomUUID();
            UUID recipientUserId = UUID.randomUUID();
            ReservationStatusChangedEvent event = new ReservationStatusChangedEvent(
                    reservationId, eventId, recipientUserId, "presta@example.com",
                    "CANCELED_BY_CLIENT_PRE_CONTACT", "Sophie Leroy", "USER", "Anniversaire de Paul", LocalDate.now());

            Notification notification = mapper.toNotification(event);

            assertThat(notification.getRecipientEmail()).isEqualTo("presta@example.com");
            assertThat(notification.getRecipientUserId()).isEqualTo(recipientUserId);
            assertThat(notification.getType()).isEqualTo(NotificationType.STATE_CHANGE);
            assertThat(notification.getMessageKey()).isEqualTo("notification.reservation.status.canceled_by_client_pre_contact");
            assertThat(notification.getParams()).containsEntry("actorName", "Sophie Leroy")
                    .containsEntry("eventTitle", "Anniversaire de Paul");
            assertThat(notification.getHref()).isEqualTo("/pro/reservations/" + reservationId);
            assertThat(notification.isRead()).isFalse();
        }
    }
}
