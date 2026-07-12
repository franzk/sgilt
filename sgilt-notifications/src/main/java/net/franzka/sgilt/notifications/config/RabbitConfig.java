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
 * Configuration RabbitMQ de sgilt-notifications. Tous les évènements de domaine consommés par ce
 * service partagent une seule queue {@value #DOMAIN_EVENTS_QUEUE} (et sa DLQ) — pas une paire par
 * type d'évènement, pour ne pas multiplier queues/DLQ/constantes à chaque nouvel évènement ajouté.
 * Le dispatch par type se fait via plusieurs méthodes {@code @RabbitHandler} dans
 * {@link net.franzka.sgilt.notifications.notification.listener.DomainEventListener}. Un nouvel
 * évènement n'ajoute donc qu'un binding (routing key) ici et une méthode côté listener — pas de
 * nouvelle queue.
 */
@Configuration
public class RabbitConfig {

    /**
     * Nom de l'exchange topic des évènements de domaine. Doit rester identique — même nom, même
     * type durable — à la déclaration équivalente côté sgilt-core, sous peine d'échec au démarrage
     * (406 inequivalent arg côté RabbitMQ).
     */
    public static final String DOMAIN_EVENTS_EXCHANGE = "domain-events";
    public static final String DOMAIN_EVENTS_QUEUE = "notifications.domain-events";

    /** Routing key publiée par {@code sgilt-core} pour une réservation nouvellement créée. */
    public static final String RESERVATION_CREATED_RK = "reservation.created";

    private static final String DOMAIN_EVENTS_DLQ = "notifications.domain-events.dlq";

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
     * Déclare la queue partagée de consommation des évènements de domaine, avec dead-lettering vers
     * {@value #DOMAIN_EVENTS_DLQ} (via l'échange par défaut) une fois les tentatives épuisées.
     *
     * @return la queue de consommation
     */
    @Bean
    public Queue domainEventsQueue() {
        return QueueBuilder.durable(DOMAIN_EVENTS_QUEUE)
                .deadLetterExchange("")
                .deadLetterRoutingKey(DOMAIN_EVENTS_DLQ)
                .build();
    }

    /**
     * Déclare la dead-letter queue associée à {@value #DOMAIN_EVENTS_QUEUE}.
     *
     * @return la dead-letter queue
     */
    @Bean
    public Queue domainEventsDlq() {
        return QueueBuilder.durable(DOMAIN_EVENTS_DLQ).build();
    }

    /**
     * Lie la queue partagée à l'exchange des évènements de domaine sur la routing key
     * {@value #RESERVATION_CREATED_RK}. Un futur évènement ajoute un binding du même genre ici,
     * pas une nouvelle queue.
     *
     * @return le binding
     */
    @Bean
    public Binding reservationCreatedBinding() {
        return BindingBuilder.bind(domainEventsQueue())
                .to(domainEventsExchange())
                .with(RESERVATION_CREATED_RK);
    }

    /**
     * Convertisseur JSON pour les messages RabbitMQ. Précédence de type explicitement laissée sur
     * {@code INFERRED} (comportement par défaut) : la désérialisation se base sur le type du
     * paramètre du {@code @RabbitHandler}, pas sur un header {@code __TypeId__} qui référencerait
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
