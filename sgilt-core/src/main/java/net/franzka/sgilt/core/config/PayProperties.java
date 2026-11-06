package net.franzka.sgilt.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propriétés de configuration pour l'appel à sgilt-pay.
 *
 * @param url l'URL de sgilt-pay sur le réseau interne
 */
@ConfigurationProperties(prefix = "sgilt.pay")
public record PayProperties(String url) {}
