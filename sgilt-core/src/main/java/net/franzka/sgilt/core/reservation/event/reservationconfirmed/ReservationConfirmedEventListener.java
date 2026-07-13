package net.franzka.sgilt.core.reservation.event.reservationconfirmed;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.notifier.DomainEventPublisher;
import net.franzka.sgilt.core.reservation.event.reservationcreated.ReservationCreatedEventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Republie sur RabbitMQ, via {@link DomainEventPublisher}, les évènements {@link ReservationConfirmedEvent}
 * publiés en interne par {@code ReservationService.confirm(...)} — uniquement une fois la transaction
 * englobante commitée ({@link TransactionPhase#AFTER_COMMIT}). Même précaution que
 * {@link ReservationCreatedEventListener} : ne pas passer en {@code fallbackExecution=true}.
 */
@Component
@RequiredArgsConstructor
public class ReservationConfirmedEventListener {

    private final DomainEventPublisher domainEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReservationConfirmedEvent event) {
        domainEventPublisher.publish(ReservationConfirmedEvent.ROUTING_KEY, event);
    }
}
