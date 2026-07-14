package net.franzka.sgilt.notifications.notification.mailer;

import net.franzka.sgilt.notifications.mailer.MailRequest;
import net.franzka.sgilt.notifications.mailer.MailType;
import net.franzka.sgilt.notifications.mailer.MailerClient;
import net.franzka.sgilt.notifications.notification.domain.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationMailerServiceTest {

    @Mock
    private MailerClient mailerClient;

    private NotificationMailerService service;

    @BeforeEach
    void setUp() {
        service = new NotificationMailerService(mailerClient, "https://sgilt.fr");
    }

    @Nested
    class SendNotificationEmail {

        @Test
        void givenKnownMessageKey_whenSendNotificationEmail_thenSendsMailWithContextAndActionUrl() {
            Notification notification = Notification.builder()
                    .recipientEmail("client@example.com")
                    .messageKey("notification.reservation.status.confirmed")
                    .params(Map.of("actorName", "Studio Fleur", "eventTitle", "Anniversaire de Paul"))
                    .href("/app/events/abc/reservations/def")
                    .build();

            service.sendNotificationEmail(notification);

            ArgumentCaptor<MailRequest> captor = ArgumentCaptor.forClass(MailRequest.class);
            verify(mailerClient).sendMail(captor.capture());
            MailRequest request = captor.getValue();

            assertThat(request.to()).isEqualTo("client@example.com");
            assertThat(request.mailType()).isEqualTo(MailType.RESERVATION_STATUS_CONFIRMED_EMAIL);
            assertThat(request.context()).containsEntry("actorName", "Studio Fleur")
                    .containsEntry("eventTitle", "Anniversaire de Paul")
                    .containsEntry("actionUrl", "https://sgilt.fr/app/events/abc/reservations/def");
        }

        @Test
        void givenUnknownMessageKey_whenSendNotificationEmail_thenDoesNotSendMail() {
            Notification notification = Notification.builder()
                    .recipientEmail("client@example.com")
                    .messageKey("notification.reservation.unknown")
                    .params(Map.of())
                    .href("/pro/reservations/def")
                    .build();

            service.sendNotificationEmail(notification);

            verify(mailerClient, never()).sendMail(any());
        }
    }
}
