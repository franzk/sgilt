package net.franzka.sgilt.core.reservation.event.reservationstatuschanged;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.notifier.DomainEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Republie sur RabbitMQ, via {@link DomainEventPublisher}, les évènements {@link ReservationStatusChangedEvent}
 * publiés en interne par {@code ReservationService} (markContacted/confirm/refuse/cancelByPro) —
 * uniquement une fois la transaction englobante commitée ({@link TransactionPhase#AFTER_COMMIT}). Ne
 * pas passer en {@code fallbackExecution=true} : ça republierait même en cas de rollback.
 */
@Component
@RequiredArgsConstructor
public class ReservationStatusChangedEventListener {

    private final DomainEventPublisher domainEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReservationStatusChangedEvent event) {
        domainEventPublisher.publish(ReservationStatusChangedEvent.ROUTING_KEY, event);
    }
}
