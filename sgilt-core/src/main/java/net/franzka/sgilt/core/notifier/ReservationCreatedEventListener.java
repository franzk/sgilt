package net.franzka.sgilt.core.notifier;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.notifier.event.ReservationCreatedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Republie sur RabbitMQ, via {@link DomainEventPublisher}, les évènements {@link ReservationCreatedEvent}
 * publiés en interne par {@code ReservationService.create(...)} — uniquement une fois la transaction
 * englobante commitée ({@link TransactionPhase#AFTER_COMMIT}), pour ne jamais notifier une réservation
 * dont la création aurait finalement échoué (ex. si la création de la note associée échoue).
 * <p>
 * Point d'attention : {@code AFTER_COMMIT} ne se déclenche que s'il existe une transaction active au
 * moment de la publication — aujourd'hui garanti par le {@code @Transactional} des 3 controllers
 * appelant {@code ReservationService.create(...)}. Un futur appelant hors transaction perdrait
 * silencieusement la notification. Ne pas passer en {@code fallbackExecution=true} : ça republierait
 * même en cas de rollback, ce que ce mécanisme cherche justement à éviter.
 */
@Component
@RequiredArgsConstructor
public class ReservationCreatedEventListener {

    private final DomainEventPublisher domainEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReservationCreatedEvent event) {
        domainEventPublisher.publishReservationCreated(event);
    }
}
