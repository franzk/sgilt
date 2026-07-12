package net.franzka.sgilt.core.notifier;

import net.franzka.sgilt.core.reservation.event.ReservationCreatedEvent;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DomainEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private DomainEventPublisher domainEventPublisher;

    @Nested
    class Publish {

        @Test
        void givenEventAndRoutingKey_whenPublish_thenConvertAndSendOnDomainEventsExchange() {
            ReservationCreatedEvent event = new ReservationCreatedEvent(
                    UUID.randomUUID(), UUID.randomUUID(), "presta@example.com",
                    "Sophie", "Leroy", "Anniversaire de Paul", LocalDate.now());

            domainEventPublisher.publish(ReservationCreatedEvent.ROUTING_KEY, event);

            verify(rabbitTemplate).convertAndSend("domain-events", "reservation.created", event);
        }
    }
}
