package net.franzka.sgilt.core.prestataire.mailer;

import net.franzka.sgilt.core.mailer.MailRequest;
import net.franzka.sgilt.core.mailer.MailType;
import net.franzka.sgilt.core.mailer.MailerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PrestataireMailerServiceTest {

    private static final String FRONTEND_URL = "https://sgilt.fr";

    @Mock
    private MailerClient mailerClient;

    private PrestataireMailerService prestataireMailerService;

    @BeforeEach
    void setUp() {
        prestataireMailerService = new PrestataireMailerService(mailerClient, FRONTEND_URL);
    }

    // -------------------------------------------------------------------------
    // sendPrestataireOnboardingEmail
    // -------------------------------------------------------------------------

    @Nested
    class SendPrestataireOnboardingEmail {

        @Test
        void givenSuccessfulSend_whenSendPrestataireOnboardingEmail_thenReturnsTrueWithExpectedRequest() {
            boolean result = prestataireMailerService.sendPrestataireOnboardingEmail(
                    "pro@sgilt.fr", "Jean", "https://sgilt.fr/verify?token=abc");

            assertThat(result).isTrue();
            verify(mailerClient).sendMail(new MailRequest(
                    "pro@sgilt.fr", MailType.PRESTATAIRE_ONBOARDING_EMAIL,
                    Map.of("firstName", "Jean", "actionUrl", "https://sgilt.fr/verify?token=abc"), null));
        }

        @Test
        void givenMailerFailure_whenSendPrestataireOnboardingEmail_thenReturnsFalse() {
            doThrow(new AmqpException("broker down")).when(mailerClient).sendMail(any());

            boolean result = prestataireMailerService.sendPrestataireOnboardingEmail(
                    "pro@sgilt.fr", "Jean", "https://sgilt.fr/verify?token=abc");

            assertThat(result).isFalse();
        }
    }

    // -------------------------------------------------------------------------
    // sendPrestatairePublishedEmail
    // -------------------------------------------------------------------------

    @Nested
    class SendPrestatairePublishedEmail {

        @Test
        void givenSuccessfulSend_whenSendPrestatairePublishedEmail_thenReturnsTrueWithExpectedRequest() {
            boolean result = prestataireMailerService.sendPrestatairePublishedEmail("pro@sgilt.fr", "Jean", "studio-fleur");

            assertThat(result).isTrue();
            verify(mailerClient).sendMail(new MailRequest(
                    "pro@sgilt.fr", MailType.PRESTATAIRE_PUBLISHED_EMAIL,
                    Map.of("firstName", "Jean", "pageUrl", "https://sgilt.fr/studio-fleur"), null));
        }

        @Test
        void givenMailerFailure_whenSendPrestatairePublishedEmail_thenReturnsFalse() {
            doThrow(new AmqpException("broker down")).when(mailerClient).sendMail(any());

            boolean result = prestataireMailerService.sendPrestatairePublishedEmail("pro@sgilt.fr", "Jean", "studio-fleur");

            assertThat(result).isFalse();
        }
    }

    // -------------------------------------------------------------------------
    // sendPrestatairePageReadyEmail
    // -------------------------------------------------------------------------

    @Nested
    class SendPrestatairePageReadyEmail {

        @Test
        void givenSuccessfulSend_whenSendPrestatairePageReadyEmail_thenReturnsTrueWithExpectedRequest() {
            boolean result = prestataireMailerService.sendPrestatairePageReadyEmail(
                    "pro@sgilt.fr", "Jean", "https://sgilt.fr/verify?token=abc", "studio-fleur");

            assertThat(result).isTrue();
            verify(mailerClient).sendMail(new MailRequest(
                    "pro@sgilt.fr", MailType.PRESTATAIRE_PAGE_READY_EMAIL,
                    Map.of("firstName", "Jean", "actionUrl", "https://sgilt.fr/verify?token=abc",
                            "pageUrl", "https://sgilt.fr/studio-fleur"), null));
        }

        @Test
        void givenMailerFailure_whenSendPrestatairePageReadyEmail_thenReturnsFalse() {
            doThrow(new AmqpException("broker down")).when(mailerClient).sendMail(any());

            boolean result = prestataireMailerService.sendPrestatairePageReadyEmail(
                    "pro@sgilt.fr", "Jean", "https://sgilt.fr/verify?token=abc", "studio-fleur");

            assertThat(result).isFalse();
        }
    }
}
