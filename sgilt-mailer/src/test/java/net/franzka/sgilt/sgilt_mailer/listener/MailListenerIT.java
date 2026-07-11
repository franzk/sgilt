package net.franzka.sgilt.sgilt_mailer.listener;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import net.franzka.sgilt.sgilt_mailer.config.RabbitConfig;
import net.franzka.sgilt.sgilt_mailer.dto.MailRequest;
import net.franzka.sgilt.sgilt_mailer.template.MailType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.RabbitMQContainer;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

/**
 * Vérifie de bout en bout le chemin {@link MailListener} : publication sur la queue
 * {@value RabbitConfig#MAIL_SEND_QUEUE}, consommation et remise à {@code JavaMailSender} (mocké),
 * ou dead-lettering vers {@value RabbitConfig#MAIL_SEND_DLQ} pour les cas d'erreur.
 * Remplace l'ancienne {@code MailControllerIT} pilotée par {@code MockMvc} — il n'y a plus de
 * réponse HTTP synchrone à observer, seulement le sort du message (traité ou dead-letter).
 */
@Tag("integration")
@SpringBootTest(
        properties = {
                "spring.mail.password=test",
                "spring.mail.username=noreply@sgilt.fr",
                // Retry accéléré pour ces tests (la config par défaut de production, 1s/2s/4s de
                // backoff, rendrait chaque cas d'erreur inutilement lent à vérifier).
                "spring.rabbitmq.listener.simple.retry.max-retries=2",
                "spring.rabbitmq.listener.simple.retry.initial-interval=20ms",
                "spring.rabbitmq.listener.simple.retry.max-interval=20ms"
        }
)
class MailListenerIT {

    static final RabbitMQContainer rabbitmq;

    static {
        rabbitmq = new RabbitMQContainer("rabbitmq:4-management-alpine");
        rabbitmq.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @MockitoBean
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage())
                .thenReturn(new MimeMessage(Session.getDefaultInstance(new Properties())));
        // Les tests de ce fichier partagent le même conteneur RabbitMQ (et donc les mêmes queues) —
        // on repart d'un état propre à chaque test pour éviter qu'un message laissé par un test
        // précédent ne fausse une assertion sur la DLQ.
        purge(RabbitConfig.MAIL_SEND_QUEUE);
        purge(RabbitConfig.MAIL_SEND_DLQ);
    }

    private void purge(String queueName) {
        while (rabbitTemplate.receive(queueName) != null) {
            // drain
        }
    }

    private MailRequest validRequest() {
        MailRequest request = new MailRequest();
        request.setTo("recipient@example.com");
        request.setMailType(MailType.WELCOME_EMAIL);
        request.setContext(Map.of("appUrl", "https://app.sgilt.fr/app"));
        return request;
    }

    @Nested
    class OnMailRequest {

        @Test
        void givenValidRequest_whenPublished_thenMailIsSent() {
            rabbitTemplate.convertAndSend(RabbitConfig.MAIL_SEND_QUEUE, validRequest());

            await().atMost(Duration.ofSeconds(5))
                    .untilAsserted(() -> verify(mailSender, times(1)).send(any(MimeMessage.class)));
        }

        @Test
        void givenMissingMandatoryField_whenPublished_thenDeadLettered() {
            MailRequest request = validRequest();
            request.setTo("  ");
            rabbitTemplate.convertAndSend(RabbitConfig.MAIL_SEND_QUEUE, request);

            await().atMost(Duration.ofSeconds(5))
                    .untilAsserted(() -> assertThat(rabbitTemplate.receive(RabbitConfig.MAIL_SEND_DLQ, 100)).isNotNull());
            verify(mailSender, never()).send(any(MimeMessage.class));
        }

        @Test
        void givenMissingTemplateVariable_whenPublished_thenDeadLettered() {
            MailRequest request = validRequest();
            request.setContext(Map.of());
            rabbitTemplate.convertAndSend(RabbitConfig.MAIL_SEND_QUEUE, request);

            await().atMost(Duration.ofSeconds(5))
                    .untilAsserted(() -> assertThat(rabbitTemplate.receive(RabbitConfig.MAIL_SEND_DLQ, 100)).isNotNull());
            verify(mailSender, never()).send(any(MimeMessage.class));
        }

        @Test
        void givenPersistentSmtpFailure_whenPublished_thenRetriedThenDeadLettered() {
            doThrow(new MailSendException("SMTP down")).when(mailSender).send(any(MimeMessage.class));

            rabbitTemplate.convertAndSend(RabbitConfig.MAIL_SEND_QUEUE, validRequest());

            await().atMost(Duration.ofSeconds(5))
                    .untilAsserted(() -> assertThat(rabbitTemplate.receive(RabbitConfig.MAIL_SEND_DLQ, 100)).isNotNull());
            verify(mailSender, atLeast(2)).send(any(MimeMessage.class));
        }
    }
}
