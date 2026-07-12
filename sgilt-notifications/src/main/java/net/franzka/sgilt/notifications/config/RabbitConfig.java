package net.franzka.sgilt.notifications.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ de sgilt-notifications. Déclare la queue consommant les évènements de
 * domaine {@value #RESERVATION_CREATED_RK} publiés par sgilt-core sur l'exchange
 * {@value #DOMAIN_EVENTS_EXCHANGE}, et sa dead-letter queue.
 */
@Configuration
public class RabbitConfig {

    /**
     * Nom de l'exchange topic des évènements de domaine. Doit rester identique — même nom, même
     * type durable — à la déclaration équivalente côté sgilt-core, sous peine d'échec au démarrage
     * (406 inequivalent arg côté RabbitMQ).
     */
    public static final String DOMAIN_EVENTS_EXCHANGE = "domain-events";
    public static final String RESERVATION_CREATED_RK = "reservation.created";
    public static final String RESERVATION_CREATED_QUEUE = "notifications.reservation-created";

    private static final String RESERVATION_CREATED_DLQ = "notifications.reservation-created.dlq";

    /**
     * Déclare l'exchange topic des évènements de domaine (sgilt-core publie dessus sans savoir qui
     * écoute).
     *
     * @return l'exchange topic
     */
    @Bean
    public TopicExchange domainEventsExchange() {
        return new TopicExchange(DOMAIN_EVENTS_EXCHANGE, true, false);
    }

    /**
     * Déclare la queue de consommation des évènements {@value #RESERVATION_CREATED_RK}, avec
     * dead-lettering vers {@value #RESERVATION_CREATED_DLQ} (via l'échange par défaut) une fois les
     * tentatives de consommation épuisées.
     *
     * @return la queue de consommation
     */
    @Bean
    public Queue reservationCreatedQueue() {
        return QueueBuilder.durable(RESERVATION_CREATED_QUEUE)
                .deadLetterExchange("")
                .deadLetterRoutingKey(RESERVATION_CREATED_DLQ)
                .build();
    }

    /**
     * Déclare la dead-letter queue associée à {@value #RESERVATION_CREATED_QUEUE}.
     *
     * @return la dead-letter queue
     */
    @Bean
    public Queue reservationCreatedDlq() {
        return QueueBuilder.durable(RESERVATION_CREATED_DLQ).build();
    }

    /**
     * Lie la queue de consommation à l'exchange des évènements de domaine sur la routing key
     * {@value #RESERVATION_CREATED_RK}.
     *
     * @return le binding
     */
    @Bean
    public Binding reservationCreatedBinding() {
        return BindingBuilder.bind(reservationCreatedQueue())
                .to(domainEventsExchange())
                .with(RESERVATION_CREATED_RK);
    }

    /**
     * Convertisseur JSON pour les messages RabbitMQ. Précédence de type explicitement laissée sur
     * {@code INFERRED} (comportement par défaut) : la désérialisation se base sur le type du
     * paramètre du {@code @RabbitListener}, pas sur un header {@code __TypeId__} qui référencerait
     * le FQCN du DTO producteur (différent côté sgilt-core).
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
