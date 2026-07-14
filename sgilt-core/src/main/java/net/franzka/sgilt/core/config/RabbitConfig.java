package net.franzka.sgilt.core.config;

import net.franzka.sgilt.core.reservation.event.reservationcreated.ReservationCreatedEvent;
import net.franzka.sgilt.core.reservation.event.reservationstatuschanged.ReservationStatusChangedEvent;
import org.springframework.amqp.support.converter.DefaultJacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration RabbitMQ partagée par tous les usages du broker dans sgilt-core (mail, évènements
 * de domaine...) — un seul bean {@link org.springframework.amqp.rabbit.core.RabbitTemplate} est
 * auto-configuré par Spring Boot, il ne doit exister qu'un seul convertisseur de message.
 */
@Configuration
public class RabbitConfig {

    /**
     * Convertisseur JSON pour les messages RabbitMQ, utilisé à la fois pour la publication
     * ({@link org.springframework.amqp.rabbit.core.RabbitTemplate}) et pour la désérialisation.
     * Précédence de type explicitement laissée sur {@code INFERRED} (comportement par défaut) :
     * la désérialisation se base sur le type du paramètre du {@code @RabbitListener} côté consommateur
     * quand ce type est connu (ex. {@code MailListener.onMailRequest(MailRequest)}).
     * <p>
     * Pour les évènements de domaine, le consommateur ({@code DomainEventListener} de
     * sgilt-notifications) route manuellement sur la routing key et reçoit un {@code Message} brut —
     * Spring ne peut alors pas déduire de type depuis la signature et retombe sur le header
     * {@code __TypeId__}, qui contiendrait par défaut le FQCN producteur (inexistant dans le classpath
     * du consommateur). {@link #idClassMapping()} fait porter à ce header la routing key plutôt que le
     * FQCN, pour découpler les deux services.
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
     * Fait correspondre chaque évènement de domaine à un identifiant portable (sa routing key) plutôt
     * qu'à son FQCN dans le header {@code __TypeId__} — sgilt-notifications ne partage pas le classpath
     * de sgilt-core. Un nouvel évènement ajoute une entrée ici.
     *
     * @return le mapper de type configuré avec la correspondance routing key / classe
     */
    @Bean
    public DefaultJacksonJavaTypeMapper idClassMapping() {
        DefaultJacksonJavaTypeMapper typeMapper = new DefaultJacksonJavaTypeMapper();
        typeMapper.setIdClassMapping(Map.of(
                ReservationCreatedEvent.ROUTING_KEY, ReservationCreatedEvent.class,
                ReservationStatusChangedEvent.ROUTING_KEY, ReservationStatusChangedEvent.class));
        return typeMapper;
    }
}
