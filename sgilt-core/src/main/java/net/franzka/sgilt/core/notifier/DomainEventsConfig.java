package net.franzka.sgilt.core.notifier;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Déclare l'exchange topic des évènements de domaine. Le convertisseur JSON RabbitMQ est déjà
 * fourni par {@link net.franzka.sgilt.core.config.RabbitConfig#jsonMessageConverter()} — ne pas en
 * redéclarer un second ici (ambiguïté de bean).
 */
@Configuration
public class DomainEventsConfig {

    /**
     * Nom de l'exchange topic des évènements de domaine. Doit rester identique — même nom, même
     * type durable — à la déclaration équivalente côté sgilt-notifications, sous peine d'échec au
     * démarrage (406 inequivalent arg côté RabbitMQ).
     */
    public static final String DOMAIN_EVENTS_EXCHANGE = "domain-events";

    /**
     * Déclare l'exchange topic des évènements de domaine. sgilt-core publie dessus sans savoir qui
     * écoute — c'est au(x) consommateur(s) de déclarer leur propre queue liée.
     *
     * @return l'exchange topic
     */
    @Bean
    public TopicExchange domainEventsExchange() {
        return new TopicExchange(DOMAIN_EVENTS_EXCHANGE, true, false);
    }
}
