package net.franzka.sgilt.sgilt_mailer.service;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import net.franzka.sgilt.sgilt_mailer.dto.MailRequest;
import net.franzka.sgilt.sgilt_mailer.template.MailTemplateRegistry;
import net.franzka.sgilt.sgilt_mailer.template.MailTemplateRenderer;
import net.franzka.sgilt.sgilt_mailer.template.MailType;
import net.franzka.sgilt.sgilt_mailer.template.MissingTemplateVariableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private MailService mailService;

    @BeforeEach
    void setUp() {
        mailService = new MailService(mailSender, new MailTemplateRegistry(new ObjectMapper()), new MailTemplateRenderer());
        ReflectionTestUtils.setField(mailService, "defaultFromAddress", "noreply@sgilt.fr");
    }

    @Nested
    class SendMail {

        @BeforeEach
        void setUp() {
            when(mailSender.createMimeMessage())
                    .thenReturn(new MimeMessage(Session.getDefaultInstance(new Properties())));
        }

        @Test
        void givenValidRequest_whenSendMail_thenMailSenderSendCalled() throws MessagingException, UnsupportedEncodingException {
            MailRequest request = new MailRequest();
            request.setTo("recipient@example.com");
            request.setMailType(MailType.WELCOME_EMAIL);
            request.setContext(Map.of("appUrl", "https://app.sgilt.fr/app"));

            mailService.sendMail(request);

            verify(mailSender, times(1)).send(any(MimeMessage.class));
        }

        @Test
        void givenValidRequest_whenSendMail_thenMessageHasCorrectRecipient() throws MessagingException, UnsupportedEncodingException {
            MailRequest request = new MailRequest();
            request.setTo("recipient@example.com");
            request.setMailType(MailType.WELCOME_EMAIL);
            request.setContext(Map.of("appUrl", "https://app.sgilt.fr/app"));

            ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
            mailService.sendMail(request);

            verify(mailSender).send(captor.capture());
            MimeMessage sent = captor.getValue();
            assertThat(sent.getAllRecipients()).isNotEmpty();
            assertThat(sent.getAllRecipients()[0].toString()).hasToString("recipient@example.com");
        }

        @Test
        void givenValidRequest_whenSendMail_thenFromIsDefaultAddress() throws MessagingException, UnsupportedEncodingException {
            MailRequest request = new MailRequest();
            request.setTo("recipient@example.com");
            request.setMailType(MailType.WELCOME_EMAIL);
            request.setContext(Map.of("appUrl", "https://app.sgilt.fr/app"));

            ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
            mailService.sendMail(request);

            verify(mailSender).send(captor.capture());
            MimeMessage sent = captor.getValue();
            assertThat(sent.getFrom()[0].toString()).contains("noreply@sgilt.fr");
        }

        @Test
        void givenValidRequest_whenSendMail_thenSubjectMatchesTemplate() throws MessagingException, UnsupportedEncodingException {
            MailRequest request = new MailRequest();
            request.setTo("recipient@example.com");
            request.setMailType(MailType.WELCOME_EMAIL);
            request.setContext(Map.of("appUrl", "https://app.sgilt.fr/app"));

            ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
            mailService.sendMail(request);

            verify(mailSender).send(captor.capture());
            assertThat(captor.getValue().getSubject()).isEqualTo("Bienvenue sur Sgilt");
        }
    }

    @Nested
    class MissingContextVariable {

        @Test
        void givenMissingRequiredVariable_whenSendMail_thenThrowsAndDoesNotSend() {
            MailRequest request = new MailRequest();
            request.setTo("recipient@example.com");
            request.setMailType(MailType.WELCOME_EMAIL);
            request.setContext(Map.of());

            assertThatThrownBy(() -> mailService.sendMail(request))
                    .isInstanceOf(MissingTemplateVariableException.class);

            verify(mailSender, never()).send(any(MimeMessage.class));
        }

        @Test
        void givenNullContext_whenSendMail_thenThrowsAndDoesNotSend() {
            MailRequest request = new MailRequest();
            request.setTo("recipient@example.com");
            request.setMailType(MailType.WELCOME_EMAIL);
            request.setContext(null);

            assertThatThrownBy(() -> mailService.sendMail(request))
                    .isInstanceOf(MissingTemplateVariableException.class);

            verify(mailSender, never()).send(any(MimeMessage.class));
        }
    }
}
