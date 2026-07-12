package net.franzka.sgilt.core.config;

import org.springframework.amqp.support.converter.JacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ partagée par tous les usages du broker dans sgilt-core (mail, évènements
 * de domaine...) — un seul bean {@link org.springframework.amqp.rabbit.core.RabbitTemplate} est
 * auto-configuré par Spring Boot, il ne doit exister qu'un seul convertisseur de message.
 */
@Configuration
public class RabbitConfig {

    /**
     * Convertisseur JSON pour les messages RabbitMQ, utilisé à la fois pour la publication
     * ({@link org.springframework.amqp.rabbit.core.RabbitTemplate}) et pour la désérialisation.
     * Précédence de type explicitement laissée sur {@code INFERRED} (comportement par défaut) :
     * la désérialisation se base sur le type du paramètre du {@code @RabbitListener} côté consommateur,
     * pas sur un header {@code __TypeId__} qui référencerait le FQCN du module producteur.
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
