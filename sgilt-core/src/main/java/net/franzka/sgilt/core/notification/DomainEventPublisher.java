package net.franzka.sgilt.core.notification;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.notification.event.ReservationCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Publie les évènements de domaine sur l'exchange {@value DomainEventsConfig#DOMAIN_EVENTS_EXCHANGE}.
 */
@Service
@RequiredArgsConstructor
public class DomainEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publie l'évènement de création de réservation avec la routing key {@code reservation.created}.
     *
     * @param event les faits bruts de la réservation créée
     */
    public void publishReservationCreated(ReservationCreatedEvent event) {
        rabbitTemplate.convertAndSend(DomainEventsConfig.DOMAIN_EVENTS_EXCHANGE, "reservation.created", event);
    }
}
