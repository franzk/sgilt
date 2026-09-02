package net.franzka.sgilt.core.onboarding.mailer;

import net.franzka.sgilt.core.mailer.MailRequest;
import net.franzka.sgilt.core.mailer.MailType;
import net.franzka.sgilt.core.mailer.MailerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OnboardingMailerServiceTest {

    private static final String FRONTEND_URL = "https://sgilt.fr";

    @Mock
    private MailerClient mailerClient;

    private OnboardingMailerService onboardingMailerService;

    @BeforeEach
    void setUp() {
        onboardingMailerService = new OnboardingMailerService(mailerClient, FRONTEND_URL);
    }

    // -------------------------------------------------------------------------
    // sendVerificationEmail
    // -------------------------------------------------------------------------

    @Nested
    class SendVerificationEmail {

        @Test
        void givenEmailAndToken_whenSendVerificationEmail_thenBuildsConfirmationUrl() {
            onboardingMailerService.sendVerificationEmail("client@sgilt.fr", "token-abc");

            verify(mailerClient).sendMail(new MailRequest(
                    "client@sgilt.fr", MailType.VERIFICATION_EMAIL,
                    Map.of("confirmationUrl", "https://sgilt.fr/onboarding/verify?token=token-abc"), null));
        }
    }

    // -------------------------------------------------------------------------
    // sendSecurityAlertEmail
    // -------------------------------------------------------------------------

    @Nested
    class SendSecurityAlertEmail {

        @Test
        void givenEmail_whenSendSecurityAlertEmail_thenBuildsAppUrl() {
            onboardingMailerService.sendSecurityAlertEmail("client@sgilt.fr");

            verify(mailerClient).sendMail(new MailRequest(
                    "client@sgilt.fr", MailType.SECURITY_ALERT_EMAIL,
                    Map.of("appUrl", "https://sgilt.fr/app"), null));
        }
    }

    // -------------------------------------------------------------------------
    // sendWelcomeEmail
    // -------------------------------------------------------------------------

    @Nested
    class SendWelcomeEmail {

        @Test
        void givenEmail_whenSendWelcomeEmail_thenBuildsAppUrl() {
            onboardingMailerService.sendWelcomeEmail("client@sgilt.fr");

            verify(mailerClient).sendMail(new MailRequest(
                    "client@sgilt.fr", MailType.WELCOME_EMAIL,
                    Map.of("appUrl", "https://sgilt.fr/app"), null));
        }
    }
}
