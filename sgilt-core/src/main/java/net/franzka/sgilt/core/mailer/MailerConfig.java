package net.franzka.sgilt.core.mailer;

import org.springframework.beans.factory.annotation.Value;
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
     * configuré avec l'URL de sgilt-mailer.
     *
     * @param mailerUrl l'URL de base de sgilt-mailer
     * @return le client HTTP proxy pour sgilt-mailer
     */
    @Bean
    public MailerClient mailerClient(@Value("${sgilt.mailer.url}") String mailerUrl) {
        RestClient restClient = RestClient.create(mailerUrl);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(MailerClient.class);
    }
}
