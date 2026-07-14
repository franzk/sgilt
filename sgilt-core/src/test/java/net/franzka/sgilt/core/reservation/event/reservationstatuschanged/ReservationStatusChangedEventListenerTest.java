package net.franzka.sgilt.core.reservation.event.reservationstatuschanged;

import net.franzka.sgilt.core.notifier.DomainEventPublisher;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.event.ActorRole;
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
class ReservationStatusChangedEventListenerTest {

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private ReservationStatusChangedEventListener listener;

    @Nested
    class Handle {

        @Test
        void givenEvent_whenHandle_thenDelegatesToDomainEventPublisher() {
            ReservationStatusChangedEvent event = new ReservationStatusChangedEvent(
                    UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "client@example.com",
                    ReservationStatus.CONFIRMED, "Studio Fleur", ActorRole.PRO,
                    "Anniversaire de Paul", LocalDate.now());

            listener.handle(event);

            verify(domainEventPublisher).publish(ReservationStatusChangedEvent.ROUTING_KEY, event);
        }
    }
}
