package net.franzka.sgilt.core.notifier;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Publie des évènements de domaine sur l'exchange {@value DomainEventsConfig#DOMAIN_EVENTS_EXCHANGE}.
 * Générique et sans connaissance du domaine (comme {@code mailer/} pour le mail) : chaque domaine
 * fournit sa routing key et son propre type d'évènement, ce service se contente de les publier.
 */
@Service
@RequiredArgsConstructor
public class DomainEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publie un évènement de domaine.
     *
     * @param routingKey la routing key associée à ce type d'évènement (portée par le domaine émetteur)
     * @param event      l'évènement à publier
     */
    public void publish(String routingKey, Object event) {
        rabbitTemplate.convertAndSend(DomainEventsConfig.DOMAIN_EVENTS_EXCHANGE, routingKey, event);
    }
}
