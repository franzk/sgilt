package net.franzka.sgilt.pay.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ de sgilt-pay : publication des paiements réussis vers sgilt-core.
 */
@Configuration
public class RabbitConfig {

    /**
     * Nom de la queue des paiements réussis, consommée par sgilt-core. Les arguments de cette
     * déclaration (durable + dead-letter) doivent rester identiques à ceux déclarés côté sgilt-core
     * — un mismatch fait échouer RabbitMQ au démarrage (406 inequivalent arg).
     */
    public static final String PAYMENT_SUCCEEDED_QUEUE = "payment.succeeded";
    private static final String PAYMENT_SUCCEEDED_DLQ = "payment.succeeded.dlq";

    /**
     * Déclare la queue {@value #PAYMENT_SUCCEEDED_QUEUE}, avec dead-lettering vers
     * {@value #PAYMENT_SUCCEEDED_DLQ}. La dead-letter queue elle-même est déclarée par sgilt-core.
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
     * Convertisseur JSON pour les messages RabbitMQ. Côté sgilt-core, la désérialisation se base sur
     * le type du paramètre du {@code @RabbitListener} (précédence {@code INFERRED}), pas sur le header
     * {@code __TypeId__} qui porte le FQCN de l'événement côté sgilt-pay.
     *
     * @return le convertisseur de message JSON
     */
    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setTypePrecedence(JacksonJavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }
}
