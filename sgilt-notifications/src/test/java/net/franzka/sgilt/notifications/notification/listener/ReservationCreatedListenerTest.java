package net.franzka.sgilt.notifications.notification.listener;

import net.franzka.sgilt.notifications.notification.event.ReservationCreatedEvent;
import net.franzka.sgilt.notifications.notification.service.NotificationService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationCreatedListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ReservationCreatedListener listener;

    @Nested
    class OnReservationCreated {

        @Test
        void givenEvent_whenOnReservationCreated_thenDelegatesToNotificationService() {
            ReservationCreatedEvent event = new ReservationCreatedEvent(
                    UUID.randomUUID(), UUID.randomUUID(), "presta@example.com",
                    "Sophie", "Leroy", "Anniversaire de Paul", LocalDate.now());

            listener.onReservationCreated(event);

            verify(notificationService).createReservationRequestNotification(event);
        }
    }
}
