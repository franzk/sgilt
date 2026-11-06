package net.franzka.sgilt.pay.config;

import com.stripe.StripeClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Spring du client de l'API Stripe.
 */
@Configuration
public class StripeConfig {

    /**
     * Crée le client de l'API Stripe, authentifié par la clé secrète.
     *
     * @param properties les propriétés de configuration Stripe
     * @return le client Stripe
     */
    @Bean
    public StripeClient stripeClient(StripeProperties properties) {
        return new StripeClient(properties.secretKey());
    }
}
