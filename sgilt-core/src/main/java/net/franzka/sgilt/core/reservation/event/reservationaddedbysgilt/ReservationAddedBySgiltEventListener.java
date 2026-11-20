package net.franzka.sgilt.core.reservation.event.reservationaddedbysgilt;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.notifier.DomainEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Republie sur RabbitMQ, via {@link DomainEventPublisher}, les évènements
 * {@link ReservationAddedBySgiltEvent} publiés en interne par {@code ReservationService.createConfirmed(...)}
 * — uniquement une fois la transaction englobante commitée ({@link TransactionPhase#AFTER_COMMIT}),
 * pour ne jamais notifier le prestataire d'une invitation dont la création aurait échoué (la
 * transaction est ouverte par le controller admin via un {@code TransactionTemplate}). Ne pas
 * passer en {@code fallbackExecution=true} : ça republierait même en cas de rollback.
 */
@Component
@RequiredArgsConstructor
public class ReservationAddedBySgiltEventListener {

    private final DomainEventPublisher domainEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReservationAddedBySgiltEvent event) {
        domainEventPublisher.publish(ReservationAddedBySgiltEvent.ROUTING_KEY, event);
    }
}
