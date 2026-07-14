package net.franzka.sgilt.notifications.mailer;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ de la connexion vers sgilt-mailer. Déclare la queue {@value #MAIL_SEND_QUEUE},
 * déjà consommée par sgilt-mailer et alimentée par sgilt-core et sgilt-smtp-bridge — sgilt-notifications
 * en devient un troisième producteur. Le convertisseur JSON partagé (celui déjà utilisé pour les
 * évènements de domaine, {@link net.franzka.sgilt.notifications.config.RabbitConfig}) suffit : le
 * listener de sgilt-mailer a un paramètre typé, il désérialise par matching structurel, pas par le
 * header {@code __TypeId__}.
 */
@Configuration
public class MailerConfig {

    /**
     * Nom de la queue d'envoi de mail, consommée par sgilt-mailer.
     * Doit rester identique — mêmes arguments — aux déclarations équivalentes côté sgilt-core,
     * sgilt-mailer et sgilt-smtp-bridge, sous peine d'échec au démarrage (406 inequivalent arg).
     */
    public static final String MAIL_SEND_QUEUE = "mail.send";
    private static final String MAIL_SEND_DLQ = "mail.send.dlq";

    /**
     * Déclare la queue {@value #MAIL_SEND_QUEUE}, avec dead-lettering vers {@value #MAIL_SEND_DLQ}
     * (via l'échange par défaut) une fois les tentatives de consommation épuisées. La DLQ elle-même
     * n'est déclarée que côté sgilt-mailer, seul consommateur — un producteur n'a besoin de déclarer
     * que la queue qu'il publie.
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
