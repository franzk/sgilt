package net.franzka.sgilt.sgilt_mailer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ de sgilt-mailer.
 * Déclare la queue {@value #MAIL_SEND_QUEUE} consommée par {@link net.franzka.sgilt.sgilt_mailer.listener.MailListener}
 * et sa dead-letter queue {@value #MAIL_SEND_DLQ}.
 */
@Configuration
public class RabbitConfig {

    /**
     * Nom de la queue d'envoi de mail, alimentée par sgilt-core, sgilt-notifications et sgilt-smtp-bridge.
     * Les arguments de cette déclaration (durable + dead-letter) doivent rester identiques à ceux
     * déclarés côté ces trois producteurs — un mismatch fait échouer RabbitMQ au démarrage
     * (406 inequivalent arg).
     */
    public static final String MAIL_SEND_QUEUE = "mail.send";
    public static final String MAIL_SEND_DLQ = "mail.send.dlq";

    /**
     * Déclare la queue {@value #MAIL_SEND_QUEUE}, avec dead-lettering vers {@value #MAIL_SEND_DLQ}
     * (via l'échange par défaut) une fois les tentatives de consommation épuisées.
     *
     * @return la queue d'envoi de mail
     */
    @Bean
    public Queue mailSendQueue() {
        return QueueBuilder.durable(MAIL_SEND_QUEUE)
                .deadLetterExchange("")
                .deadLetterRoutingKey(MAIL_SEND_DLQ)
                .build();
    }

    /**
     * Déclare la dead-letter queue {@value #MAIL_SEND_DLQ}. Seul sgilt-mailer la déclare :
     * aucun producteur n'y publie directement, RabbitMQ y route automatiquement les messages
     * rejetés par {@value #MAIL_SEND_QUEUE}.
     *
     * @return la dead-letter queue de l'envoi de mail
     */
    @Bean
    public Queue mailSendDlq() {
        return QueueBuilder.durable(MAIL_SEND_DLQ).build();
    }

    /**
     * Convertisseur JSON pour les messages RabbitMQ. Précédence de type explicitement laissée sur
     * {@code INFERRED} (comportement par défaut) : la désérialisation se base sur le type du
     * paramètre du {@code @RabbitListener}, pas sur un header {@code __TypeId__} qui référencerait
     * le FQCN du DTO producteur (différent selon que le message vient de sgilt-core).
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
