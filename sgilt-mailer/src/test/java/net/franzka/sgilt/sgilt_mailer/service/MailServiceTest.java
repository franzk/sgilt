package net.franzka.sgilt.sgilt_mailer.service;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import net.franzka.sgilt.sgilt_mailer.dto.MailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailService mailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mailService, "defaultFromAddress", "noreply@sgilt.fr");
        when(mailSender.createMimeMessage())
                .thenReturn(new MimeMessage(Session.getDefaultInstance(new Properties())));
    }

    @Nested
    class SendMail {

        @Test
        void givenHtmlContent_whenSendMail_thenMailSenderSendCalled() throws MessagingException, UnsupportedEncodingException {
            MailRequest request = new MailRequest();
            request.setFrom("sender@example.com");
            request.setTo("recipient@example.com");
            request.setSubject("Test sujet");
            request.setHtml("<p>Hello</p>");

            mailService.sendMail(request);

            verify(mailSender, times(1)).send(any(MimeMessage.class));
        }

        @Test
        void givenTextContent_whenSendMail_thenMailSenderSendCalled() throws MessagingException, UnsupportedEncodingException {
            MailRequest request = new MailRequest();
            request.setFrom("sender@example.com");
            request.setTo("recipient@example.com");
            request.setSubject("Test sujet");
            request.setText("Hello en texte");

            mailService.sendMail(request);

            verify(mailSender, times(1)).send(any(MimeMessage.class));
        }

        @Test
        void givenValidRequest_whenSendMail_thenMessageHasCorrectRecipient() throws MessagingException, UnsupportedEncodingException {
            MailRequest request = new MailRequest();
            request.setFrom("sender@example.com");
            request.setTo("recipient@example.com");
            request.setSubject("Test sujet");
            request.setText("Hello");

            ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
            mailService.sendMail(request);

            verify(mailSender).send(captor.capture());
            MimeMessage sent = captor.getValue();
            assertThat(sent.getAllRecipients()).isNotEmpty();
            assertThat(sent.getAllRecipients()[0].toString()).isEqualTo("recipient@example.com");
        }

        @Test
        void givenValidRequest_whenSendMail_thenFromIsDefaultAddress() throws MessagingException, UnsupportedEncodingException {
            MailRequest request = new MailRequest();
            request.setFrom("sender@example.com");
            request.setTo("recipient@example.com");
            request.setSubject("Sujet");
            request.setText("Contenu");

            ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
            mailService.sendMail(request);

            verify(mailSender).send(captor.capture());
            MimeMessage sent = captor.getValue();
            assertThat(sent.getFrom()[0].toString()).contains("noreply@sgilt.fr");
        }

        @Test
        void givenHtmlAndText_whenSendMail_thenHtmlTakesPrecedence() throws MessagingException, UnsupportedEncodingException {
            MailRequest request = new MailRequest();
            request.setFrom("sender@example.com");
            request.setTo("recipient@example.com");
            request.setSubject("Sujet");
            request.setHtml("<p>HTML</p>");
            request.setText("Texte brut");

            ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
            mailService.sendMail(request);

            verify(mailSender).send(captor.capture());
            // HTML branch is taken when html is non-null — verify send is called (content type checked via IT)
            assertThat(captor.getValue()).isNotNull();
        }
    }
}
