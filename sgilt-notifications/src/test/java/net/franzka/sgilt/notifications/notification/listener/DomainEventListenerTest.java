package net.franzka.sgilt.notifications.notification.listener;

import net.franzka.sgilt.notifications.config.RabbitConfig;
import net.franzka.sgilt.notifications.notification.event.ReservationConfirmedEvent;
import net.franzka.sgilt.notifications.notification.event.ReservationCreatedEvent;
import net.franzka.sgilt.notifications.notification.service.NotificationService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.SmartMessageConverter;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DomainEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private SmartMessageConverter messageConverter;

    @InjectMocks
    private DomainEventListener listener;

    @Nested
    class OnMessage {

        @Test
        void givenReservationCreatedRoutingKey_whenOnMessage_thenDelegatesToNotificationService() {
            ReservationCreatedEvent event = new ReservationCreatedEvent(
                    UUID.randomUUID(), UUID.randomUUID(), "presta@example.com",
                    "Sophie", "Leroy", "Anniversaire de Paul", LocalDate.now());
            Message message = mock(Message.class);
            when(messageConverter.fromMessage(eq(message), any())).thenReturn(event);

            listener.onMessage(message, RabbitConfig.RESERVATION_CREATED_RK);

            verify(notificationService).createFromEvent(event);
        }

        @Test
        void givenReservationConfirmedRoutingKey_whenOnMessage_thenDelegatesToNotificationService() {
            ReservationConfirmedEvent event = new ReservationConfirmedEvent(
                    UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "client@example.com",
                    "Studio Fleur", "Anniversaire de Paul", LocalDate.now());
            Message message = mock(Message.class);
            when(messageConverter.fromMessage(eq(message), any())).thenReturn(event);

            listener.onMessage(message, RabbitConfig.RESERVATION_CONFIRMED_RK);

            verify(notificationService).createFromEvent(event);
        }

        @Test
        void givenUnknownRoutingKey_whenOnMessage_thenThrowsRejectAndDontRequeue() {
            Message message = mock(Message.class);

            assertThatThrownBy(() -> listener.onMessage(message, "unknown.routing.key"))
                    .isInstanceOf(AmqpRejectAndDontRequeueException.class);
        }
    }
}
