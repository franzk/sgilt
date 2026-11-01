package net.franzka.sgilt.core.evenement.billetterie.commande.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.billetterie.commande.service.CommandeService;
import net.franzka.sgilt.core.pay.PaymentSucceededEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static net.franzka.sgilt.core.pay.PaymentEventsConfig.PAYMENT_SUCCEEDED_QUEUE;

/**
 * Consomme les paiements réussis publiés par sgilt-pay et marque les commandes payées. Comme côté
 * sgilt-mailer : le retry configuré sur le listener s'applique à toute exception avant dead-letter.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentSucceededListener {

    private final CommandeService commandeService;

    /**
     * Marque payée la commande d'un paiement réussi. {@code @Transactional} porté ici faute de
     * controller appelant, comme {@code ReservationReminderService.sendContactPendingReminders}.
     *
     * @param event le paiement réussi
     */
    @Transactional
    @RabbitListener(queues = PAYMENT_SUCCEEDED_QUEUE)
    public void onPaymentSucceeded(PaymentSucceededEvent event) {
        log.info("onPaymentSucceeded : commandeId={} paymentIntentId={}", event.commandeId(), event.paymentIntentId());
        commandeService.markPaid(event.commandeId());
    }
}
