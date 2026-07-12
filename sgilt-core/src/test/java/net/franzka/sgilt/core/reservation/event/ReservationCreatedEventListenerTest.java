package net.franzka.sgilt.core.reservation.event;

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
class ReservationCreatedEventListenerTest {

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private ReservationCreatedEventListener listener;

    @Nested
    class Handle {

        @Test
        void givenEvent_whenHandle_thenDelegatesToDomainEventPublisher() {
            ReservationCreatedEvent event = new ReservationCreatedEvent(
                    UUID.randomUUID(), UUID.randomUUID(), "presta@example.com",
                    "Sophie", "Leroy", "Anniversaire de Paul", LocalDate.now());

            listener.handle(event);

            verify(domainEventPublisher).publish(ReservationCreatedEvent.ROUTING_KEY, event);
        }
    }
}
