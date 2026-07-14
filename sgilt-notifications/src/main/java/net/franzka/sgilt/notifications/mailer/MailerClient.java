package net.franzka.sgilt.notifications.mailer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Client vers sgilt-mailer, publiant les demandes d'envoi de mail sur la queue RabbitMQ
 * {@value MailerConfig#MAIL_SEND_QUEUE} consommée par sgilt-mailer. Miroir de
 * {@code net.franzka.sgilt.core.mailer.MailerClient}.
 */
@Service
@RequiredArgsConstructor
public class MailerClient {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publie une demande d'envoi de mail sur la queue de sgilt-mailer.
     * En cas d'échec persistant après épuisement du retry du {@link RabbitTemplate}
     * (configuré via {@code spring.rabbitmq.template.retry.*}), une {@link org.springframework.amqp.AmqpException}
     * est levée — à l'appelant de décider s'il la laisse se propager ou la traite.
     *
     * @param request les données du mail à envoyer (destinataire, type, variables)
     */
    public void sendMail(MailRequest request) {
        rabbitTemplate.convertAndSend(MailerConfig.MAIL_SEND_QUEUE, request);
    }
}
