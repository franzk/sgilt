package net.franzka.sgilt.core.reservation.event.reservationcontactreminderdue;

import net.franzka.sgilt.core.notifier.DomainEventPublisher;
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
class ReservationContactReminderDueEventListenerTest {

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private ReservationContactReminderDueEventListener listener;

    @Nested
    class Handle {

        @Test
        void givenEvent_whenHandle_thenDelegatesToDomainEventPublisher() {
            ReservationContactReminderDueEvent event = new ReservationContactReminderDueEvent(
                    UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "client@example.com",
                    "Sophie", "DJ Max", "Anniversaire de Paul", LocalDate.now());

            listener.handle(event);

            verify(domainEventPublisher).publish(ReservationContactReminderDueEvent.ROUTING_KEY, event);
        }
    }
}
