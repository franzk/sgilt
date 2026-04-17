package net.franzka.sgilt.core.mailer;

import net.franzka.sgilt.core.config.MailerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configuration Spring du bean {@link MailerClient}.
 * Crée le proxy HTTP déclaratif pointant vers l'URL de sgilt-mailer.
 */
@Configuration
public class MailerConfig {

    /**
     * Crée le bean {@link MailerClient} via {@link HttpServiceProxyFactory},
     * configuré avec l'URL de sgilt-mailer issue de {@link MailerProperties}.
     *
     * @param mailerProperties les propriétés contenant l'URL de sgilt-mailer
     * @return le client HTTP proxy pour sgilt-mailer
     */
    @Bean
    public MailerClient mailerClient(MailerProperties mailerProperties) {
        RestClient restClient = RestClient.create(mailerProperties.url());
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(MailerClient.class);
    }
}
