package net.franzka.sgilt.core.reservation.event.reservationconfirmed;

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
class ReservationConfirmedEventListenerTest {

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private ReservationConfirmedEventListener listener;

    @Nested
    class Handle {

        @Test
        void givenEvent_whenHandle_thenDelegatesToDomainEventPublisher() {
            ReservationConfirmedEvent event = new ReservationConfirmedEvent(
                    UUID.randomUUID(), UUID.randomUUID(), "client@example.com",
                    "Studio Fleur", "Anniversaire de Paul", LocalDate.now());

            listener.handle(event);

            verify(domainEventPublisher).publish(ReservationConfirmedEvent.ROUTING_KEY, event);
        }
    }
}
