package net.franzka.sgilt.pay.webhook.event;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static net.franzka.sgilt.pay.config.RabbitConfig.PAYMENT_SUCCEEDED_QUEUE;

/**
 * Publie les paiements réussis sur la queue {@value net.franzka.sgilt.pay.config.RabbitConfig#PAYMENT_SUCCEEDED_QUEUE},
 * consommée par sgilt-core.
 */
@Service
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publie un paiement réussi. En cas d'échec persistant après épuisement du retry du
     * {@link RabbitTemplate}, une {@link org.springframework.amqp.AmqpException} est levée : le
     * webhook répond alors en erreur et Stripe le renverra.
     *
     * @param event le paiement réussi
     */
    public void publishPaymentSucceeded(PaymentSucceededEvent event) {
        rabbitTemplate.convertAndSend(PAYMENT_SUCCEEDED_QUEUE, event);
    }
}
