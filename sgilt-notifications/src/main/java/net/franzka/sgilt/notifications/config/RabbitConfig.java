package net.franzka.sgilt.notifications.config;

import net.franzka.sgilt.notifications.notification.event.ReservationConfirmedEvent;
import net.franzka.sgilt.notifications.notification.event.ReservationCreatedEvent;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration RabbitMQ de sgilt-notifications. Tous les évènements de domaine consommés par ce
 * service partagent une seule queue {@value #DOMAIN_EVENTS_QUEUE} (et sa DLQ) — pas une paire par
 * type d'évènement, pour ne pas multiplier queues/DLQ/constantes à chaque nouvel évènement ajouté.
 * Le dispatch par type se fait par routing key dans
 * {@link net.franzka.sgilt.notifications.notification.listener.DomainEventListener#onMessage}. Un
 * nouvel évènement n'ajoute donc qu'un binding (routing key) ici, une entrée dans
 * {@link #idClassMapping()} et un cas dans le switch du listener — pas de nouvelle queue.
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

    /** Routing key publiée par {@code sgilt-core} pour une réservation confirmée par le prestataire. */
    public static final String RESERVATION_CONFIRMED_RK = "reservation.confirmed";

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
     * Lie la queue partagée à l'exchange des évènements de domaine sur la routing key
     * {@value #RESERVATION_CONFIRMED_RK}.
     *
     * @return le binding
     */
    @Bean
    public Binding reservationConfirmedBinding() {
        return BindingBuilder.bind(domainEventsQueue())
                .to(domainEventsExchange())
                .with(RESERVATION_CONFIRMED_RK);
    }

    /**
     * Convertisseur JSON pour les messages RabbitMQ. Le dispatch se fait manuellement sur la routing
     * key dans {@code DomainEventListener.onMessage(Message, ...)}, qui reçoit un {@code Message} brut
     * — Spring ne peut donc pas déduire de type depuis la signature de la méthode et retombe sur le
     * header {@code __TypeId__} pour la conversion. {@link #idClassMapping()} fait porter ce header à
     * la routing key plutôt qu'au FQCN producteur (inexistant dans ce classpath).
     *
     * @return le convertisseur de message JSON
     */
    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setTypePrecedence(JacksonJavaTypeMapper.TypePrecedence.INFERRED);
        converter.setJavaTypeMapper(idClassMapping());
        return converter;
    }

    /**
     * Fait correspondre chaque évènement de domaine consommé à un identifiant portable (sa routing
     * key) plutôt qu'au FQCN écrit par sgilt-core dans le header {@code __TypeId__} — les deux
     * services ne partagent pas de classpath. Un nouvel évènement ajoute une entrée ici.
     *
     * @return le mapper de type configuré avec la correspondance routing key / classe
     */
    @Bean
    public DefaultJacksonJavaTypeMapper idClassMapping() {
        DefaultJacksonJavaTypeMapper typeMapper = new DefaultJacksonJavaTypeMapper();
        typeMapper.setIdClassMapping(Map.of(
                RESERVATION_CREATED_RK, ReservationCreatedEvent.class,
                RESERVATION_CONFIRMED_RK, ReservationConfirmedEvent.class));
        return typeMapper;
    }
}
