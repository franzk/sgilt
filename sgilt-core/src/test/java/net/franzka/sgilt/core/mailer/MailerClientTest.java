package net.franzka.sgilt.core.mailer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailerClientTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private MailerClient mailerClient;

    @Test
    void givenMailRequest_whenSendMail_thenPublishesToMailSendQueue() {
        MailRequest request = new MailRequest("pro@sgilt.fr", MailType.WELCOME_EMAIL, Map.of("appUrl", "https://sgilt.fr/app"), null);

        mailerClient.sendMail(request);

        verify(rabbitTemplate).convertAndSend(MailerConfig.MAIL_SEND_QUEUE, request);
    }
}
