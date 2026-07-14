package net.franzka.sgilt.core.reservation.event.reservationfeeditemadded;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.notifier.DomainEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Republie sur RabbitMQ, via {@link DomainEventPublisher}, les évènements {@link ReservationFeedItemAddedEvent}
 * publiés en interne par {@code ReservationFeedService} (addNote/addDocument) — uniquement une fois la
 * transaction englobante commitée ({@link TransactionPhase#AFTER_COMMIT}). Ne pas passer en
 * {@code fallbackExecution=true} : ça republierait même en cas de rollback.
 */
@Component
@RequiredArgsConstructor
public class ReservationFeedItemAddedEventListener {

    private final DomainEventPublisher domainEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReservationFeedItemAddedEvent event) {
        domainEventPublisher.publish(ReservationFeedItemAddedEvent.ROUTING_KEY, event);
    }
}
