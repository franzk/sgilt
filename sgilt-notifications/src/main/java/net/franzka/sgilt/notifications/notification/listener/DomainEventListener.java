package net.franzka.sgilt.notifications.notification.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.notifications.notification.event.ReservationCreatedEvent;
import net.franzka.sgilt.notifications.notification.event.ReservationFeedItemAddedEvent;
import net.franzka.sgilt.notifications.notification.event.ReservationStatusChangedEvent;
import net.franzka.sgilt.notifications.notification.service.NotificationService;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.amqp.support.converter.SmartMessageConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static net.franzka.sgilt.notifications.config.RabbitConfig.DOMAIN_EVENTS_QUEUE;
import static net.franzka.sgilt.notifications.config.RabbitConfig.RESERVATION_CREATED_RK;
import static net.franzka.sgilt.notifications.config.RabbitConfig.RESERVATION_FEED_ITEM_ADDED_RK;
import static net.franzka.sgilt.notifications.config.RabbitConfig.RESERVATION_STATUS_CHANGED_RK;

/**
 * Consomme tous les évènements de domaine publiés par sgilt-core, depuis la queue partagée
 * {@value net.franzka.sgilt.notifications.config.RabbitConfig#DOMAIN_EVENTS_QUEUE}. Dispatch manuel
 * par routing key plutôt que par les {@code @RabbitHandler} multiples de Spring AMQP : ce mécanisme
 * a besoin d'un header {@code __TypeId__} pour choisir entre plusieurs méthodes candidates, ce qui a
 * été délibérément évité (précédence {@code INFERRED} — pas de couplage sur le FQCN producteur, de
 * toute façon différent entre sgilt-core et sgilt-notifications). Un nouvel évènement ajoute donc un
 * cas dans le {@code switch} (routing key → classe à convertir), pas de nouvelle queue ni de nouveau
 * listener — {@link NotificationService#createFromEvent} route à son tour vers la bonne notification.
 * Comme côté sgilt-mailer : le retry configuré sur le listener s'applique à toute exception avant
 * dead-letter, pas de court-circuit pour les erreurs déterministes.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DomainEventListener {

    private final NotificationService notificationService;
    private final SmartMessageConverter messageConverter;

    @RabbitListener(queues = DOMAIN_EVENTS_QUEUE)
    public void onMessage(Message message, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        log.info("onMessage : routingKey={}", routingKey);
        switch (routingKey) {
            case RESERVATION_CREATED_RK -> notificationService.createFromEvent(convert(message, ReservationCreatedEvent.class));
            case RESERVATION_STATUS_CHANGED_RK -> notificationService.createFromEvent(convert(message, ReservationStatusChangedEvent.class));
            case RESERVATION_FEED_ITEM_ADDED_RK -> notificationService.createFromEvent(convert(message, ReservationFeedItemAddedEvent.class));
            default -> throw new AmqpRejectAndDontRequeueException("Routing key inconnue : " + routingKey);
        }
    }

    private <T> T convert(Message message, Class<T> targetType) {
        return targetType.cast(messageConverter.fromMessage(message, ParameterizedTypeReference.forType(targetType)));
    }
}
