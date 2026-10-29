package net.franzka.sgilt.core.reservation.event.reservationcontactreminderdue;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.notifier.DomainEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Republie sur RabbitMQ, via {@link DomainEventPublisher}, les évènements
 * {@link ReservationContactReminderDueEvent} publiés en interne par {@code ReservationReminderService}
 * — uniquement une fois la transaction englobante commitée ({@link TransactionPhase#AFTER_COMMIT}),
 * pour ne jamais envoyer un rappel dont la trace n'aurait pas été enregistrée. Ne pas passer en
 * {@code fallbackExecution=true} : ça republierait même en cas de rollback.
 */
@Component
@RequiredArgsConstructor
public class ReservationContactReminderDueEventListener {

    private final DomainEventPublisher domainEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReservationContactReminderDueEvent event) {
        domainEventPublisher.publish(ReservationContactReminderDueEvent.ROUTING_KEY, event);
    }
}
