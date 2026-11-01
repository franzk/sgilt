package net.franzka.sgilt.core.pay;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Déclare la queue des paiements réussis, alimentée par sgilt-pay et consommée par sgilt-core. Le
 * convertisseur JSON RabbitMQ est déjà fourni par
 * {@link net.franzka.sgilt.core.config.RabbitConfig#jsonMessageConverter()}.
 */
@Configuration
public class PaymentEventsConfig {

    /**
     * Nom de la queue des paiements réussis. Les arguments de cette déclaration (durable +
     * dead-letter) doivent rester identiques à ceux déclarés côté sgilt-pay — un mismatch fait
     * échouer RabbitMQ au démarrage (406 inequivalent arg).
     */
    public static final String PAYMENT_SUCCEEDED_QUEUE = "payment.succeeded";
    private static final String PAYMENT_SUCCEEDED_DLQ = "payment.succeeded.dlq";

    /**
     * Déclare la queue {@value #PAYMENT_SUCCEEDED_QUEUE}, avec dead-lettering vers
     * {@value #PAYMENT_SUCCEEDED_DLQ} (via l'échange par défaut) une fois les tentatives de
     * consommation épuisées.
     *
     * @return la queue des paiements réussis
     */
    @Bean
    public Queue paymentSucceededQueue() {
        return QueueBuilder.durable(PAYMENT_SUCCEEDED_QUEUE)
                .deadLetterExchange("")
                .deadLetterRoutingKey(PAYMENT_SUCCEEDED_DLQ)
                .build();
    }

    /**
     * Déclare la dead-letter queue {@value #PAYMENT_SUCCEEDED_DLQ}. Seul sgilt-core, consommateur, la
     * déclare : RabbitMQ y route les messages rejetés par {@value #PAYMENT_SUCCEEDED_QUEUE}. Un
     * message qui y atterrit est un paiement encaissé sur une commande restée non payée : à traiter
     * à la main.
     *
     * @return la dead-letter queue des paiements réussis
     */
    @Bean
    public Queue paymentSucceededDlq() {
        return QueueBuilder.durable(PAYMENT_SUCCEEDED_DLQ).build();
    }
}
