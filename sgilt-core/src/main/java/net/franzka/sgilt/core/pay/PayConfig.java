package net.franzka.sgilt.core.pay;

import net.franzka.sgilt.core.config.PayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configuration Spring du client HTTP déclaratif vers sgilt-pay.
 */
@Configuration
public class PayConfig {

    /**
     * Crée le bean {@link PayClient} pointant vers sgilt-pay.
     *
     * @param properties les propriétés de configuration de sgilt-pay
     * @return le proxy HTTP vers sgilt-pay
     */
    @Bean
    public PayClient payClient(PayProperties properties) {
        RestClient restClient = RestClient.create(properties.url());
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(PayClient.class);
    }
}
