package net.franzka.sgilt.core.mailer;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Spring de la connexion RabbitMQ vers sgilt-mailer.
 * Déclare la queue {@value #MAIL_SEND_QUEUE} et le convertisseur JSON utilisés par {@link MailerClient}.
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

    /**
     * Convertisseur JSON pour les messages RabbitMQ, utilisé à la fois pour la publication
     * ({@link org.springframework.amqp.rabbit.core.RabbitTemplate}) et pour la désérialisation.
     * Précédence de type explicitement laissée sur {@code INFERRED} (comportement par défaut) :
     * la désérialisation se base sur le type du paramètre du {@code @RabbitListener} côté sgilt-mailer,
     * pas sur un header {@code __TypeId__} qui référencerait le FQCN de ce module (différent côté mailer).
     *
     * @return le convertisseur de message JSON
     */
    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setTypePrecedence(JacksonJavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }
}
