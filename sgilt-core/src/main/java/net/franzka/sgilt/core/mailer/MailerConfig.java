package net.franzka.sgilt.core.mailer;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Spring de la connexion RabbitMQ vers sgilt-mailer.
 * Déclare la queue {@value #MAIL_SEND_QUEUE} utilisée par {@link MailerClient}. Le convertisseur JSON
 * RabbitMQ, partagé par tous les usages du broker, vit dans {@link net.franzka.sgilt.core.config.RabbitConfig}.
 */
@Configuration
public class MailerConfig {

    /**
     * Nom de la queue d'envoi de mail, consommée par sgilt-mailer.
     * Doit rester identique à la déclaration équivalente côté sgilt-mailer et sgilt-smtp-bridge —
     * un mismatch d'arguments entre déclarations fait échouer RabbitMQ au démarrage (406 inequivalent arg).
     */
    public static final String MAIL_SEND_QUEUE = "mail.send";
    private static final String MAIL_SEND_DLQ = "mail.send.dlq";

    /**
     * Déclare la queue {@value #MAIL_SEND_QUEUE}, avec dead-lettering vers {@value #MAIL_SEND_DLQ}
     * (via l'échange par défaut) une fois les tentatives de consommation épuisées.
     *
     * @return la queue d'envoi de mail
     */
    @Bean
    public Queue mailSendQueue() {
        return QueueBuilder.durable(MAIL_SEND_QUEUE)
                .deadLetterExchange("")
                .deadLetterRoutingKey(MAIL_SEND_DLQ)
                .build();
    }
}
