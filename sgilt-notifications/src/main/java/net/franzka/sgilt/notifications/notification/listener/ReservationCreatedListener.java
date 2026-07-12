package net.franzka.sgilt.notifications.notification.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.notifications.notification.event.ReservationCreatedEvent;
import net.franzka.sgilt.notifications.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static net.franzka.sgilt.notifications.config.RabbitConfig.RESERVATION_CREATED_QUEUE;

/**
 * Consomme les évènements {@value net.franzka.sgilt.notifications.config.RabbitConfig#RESERVATION_CREATED_RK}
 * publiés par sgilt-core et délègue la création de la notification in-app à {@link NotificationService}.
 * Comme côté sgilt-mailer : le retry configuré sur le listener s'applique à toute exception avant
 * dead-letter, pas de court-circuit pour les erreurs déterministes.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationCreatedListener {

    private final NotificationService notificationService;

    /**
     * Traite un évènement de création de réservation en créant la notification in-app associée.
     *
     * @param event les faits bruts publiés par sgilt-core
     */
    @RabbitListener(queues = RESERVATION_CREATED_QUEUE)
    public void onReservationCreated(ReservationCreatedEvent event) {
        log.info("onReservationCreated : reservationId={} recipientEmail={}", event.reservationId(), event.recipientEmail());
        notificationService.createReservationRequestNotification(event);
    }
}
