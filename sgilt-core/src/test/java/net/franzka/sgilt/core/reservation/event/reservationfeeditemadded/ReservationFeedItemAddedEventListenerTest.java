package net.franzka.sgilt.core.reservation.event.reservationfeeditemadded;

import net.franzka.sgilt.core.notifier.DomainEventPublisher;
import net.franzka.sgilt.core.reservation.event.ActorRole;
import net.franzka.sgilt.core.reservation.event.FeedItemType;
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
class ReservationFeedItemAddedEventListenerTest {

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private ReservationFeedItemAddedEventListener listener;

    @Nested
    class Handle {

        @Test
        void givenEvent_whenHandle_thenDelegatesToDomainEventPublisher() {
            ReservationFeedItemAddedEvent event = new ReservationFeedItemAddedEvent(
                    UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "client@example.com",
                    FeedItemType.NOTE, "Studio Fleur", ActorRole.PRO,
                    "Anniversaire de Paul", LocalDate.now());

            listener.handle(event);

            verify(domainEventPublisher).publish(ReservationFeedItemAddedEvent.ROUTING_KEY, event);
        }
    }
}
