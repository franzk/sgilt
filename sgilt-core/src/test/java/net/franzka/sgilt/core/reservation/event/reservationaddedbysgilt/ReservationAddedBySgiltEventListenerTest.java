package net.franzka.sgilt.core.reservation.event.reservationaddedbysgilt;

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
class ReservationAddedBySgiltEventListenerTest {

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private ReservationAddedBySgiltEventListener listener;

    @Nested
    class Handle {

        @Test
        void givenEvent_whenHandle_thenDelegatesToDomainEventPublisher() {
            ReservationAddedBySgiltEvent event = new ReservationAddedBySgiltEvent(
                    UUID.randomUUID(), UUID.randomUUID(), "presta@example.com",
                    "Julie", "Martin", LocalDate.of(2027, 6, 12));

            listener.handle(event);

            verify(domainEventPublisher).publish(ReservationAddedBySgiltEvent.ROUTING_KEY, event);
        }
    }
}
