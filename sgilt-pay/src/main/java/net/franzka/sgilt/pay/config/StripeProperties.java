package net.franzka.sgilt.pay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propriétés de configuration pour l'intégration Stripe.
 *
 * @param secretKey     la clé secrète de l'API Stripe (variable d'environnement STRIPE_SECRET_KEY)
 * @param webhookSecret le secret de signature des webhooks (variable d'environnement STRIPE_WEBHOOK_SECRET)
 */
@ConfigurationProperties(prefix = "sgilt.stripe")
public record StripeProperties(
        String secretKey,
        String webhookSecret
) {}
