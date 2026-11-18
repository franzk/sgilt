package net.franzka.sgilt.core.admin.mailer;

import net.franzka.sgilt.core.mailer.MailRequest;
import net.franzka.sgilt.core.mailer.MailType;
import net.franzka.sgilt.core.mailer.MailerClient;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClientInvitationMailerServiceTest {

    private static final String ACTION_URL = "https://sgilt.fr/onboarding/verify?token=abc";

    @Mock
    private MailerClient mailerClient;

    @InjectMocks
    private ClientInvitationMailerService clientInvitationMailerService;

    // -------------------------------------------------------------------------
    // sendClientInvitationEmail
    // -------------------------------------------------------------------------

    @Nested
    class SendClientInvitationEmail {

        @Test
        void givenSuccessfulSend_whenSendClientInvitationEmail_thenReturnsTrueWithDateFormattedInFrench() {
            boolean result = clientInvitationMailerService.sendClientInvitationEmail(
                    "julie@example.com", "Julie", "Le Taennel", LocalDate.of(2027, 6, 12), ACTION_URL);

            assertThat(result).isTrue();
            verify(mailerClient).sendMail(new MailRequest(
                    "julie@example.com", MailType.CLIENT_INVITATION_EMAIL,
                    Map.of("firstName", "Julie", "prestataireName", "Le Taennel",
                            "eventDate", "12 juin 2027", "actionUrl", ACTION_URL),
                    null));
        }

        @Test
        void givenMailerFailure_whenSendClientInvitationEmail_thenReturnsFalse() {
            doThrow(new AmqpException("broker down")).when(mailerClient).sendMail(any());

            boolean result = clientInvitationMailerService.sendClientInvitationEmail(
                    "julie@example.com", "Julie", "Le Taennel", LocalDate.of(2027, 6, 12), ACTION_URL);

            assertThat(result).isFalse();
        }
    }
}
