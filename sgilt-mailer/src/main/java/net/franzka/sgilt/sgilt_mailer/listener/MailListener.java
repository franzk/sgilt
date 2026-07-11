package net.franzka.sgilt.sgilt_mailer.listener;

import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.sgilt_mailer.dto.MailRequest;
import net.franzka.sgilt.sgilt_mailer.service.MailService;
import net.franzka.sgilt.sgilt_mailer.template.MissingTemplateVariableException;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.stream.Collectors;

import static net.franzka.sgilt.sgilt_mailer.config.RabbitConfig.MAIL_SEND_QUEUE;

/**
 * Consomme la queue {@value net.franzka.sgilt.sgilt_mailer.config.RabbitConfig#MAIL_SEND_QUEUE}
 * et délègue l'envoi effectif à {@link MailService}. Remplace l'ancien endpoint REST {@code /api/v1/mail}.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MailListener {

    private final MailService mailService;
    private final Validator validator;

    /**
     * Traite un message d'envoi de mail. Les erreurs déterministes (requête invalide, variable de
     * gabarit manquante) sont explicitement rejetées via {@link AmqpRejectAndDontRequeueException}.
     * En pratique, l'intercepteur de retry configuré sur le listener ({@code spring.rabbitmq.listener.simple.retry.*})
     * s'applique uniformément à toute exception, y compris celle-ci — un message invalide consomme
     * donc le même budget de retry qu'un échec transitoire avant d'atterrir en dead-letter. Distinguer
     * les deux nécessiterait une politique de retry avec classification par type d'exception, jugée
     * disproportionnée pour cette pré-tâche (voir le plan de migration RabbitMQ) ; le lancer explicite
     * de {@link AmqpRejectAndDontRequeueException} documente l'intention même si son effet de
     * court-circuit n'est actuellement pas garanti.
     *
     * @param request les données du mail à envoyer
     * @throws MessagingException           si l'envoi SMTP échoue
     * @throws UnsupportedEncodingException si l'adresse d'expédition est mal formée
     */
    @RabbitListener(queues = MAIL_SEND_QUEUE)
    public void onMailRequest(MailRequest request) throws MessagingException, UnsupportedEncodingException {
        log.info("onMailRequest : to {} with mailType {}", request.getTo(), request.getMailType());

        Set<ConstraintViolation<MailRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new AmqpRejectAndDontRequeueException("Invalid mail request: " + message);
        }

        try {
            mailService.sendMail(request);
        } catch (MissingTemplateVariableException e) {
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }
    }
}
