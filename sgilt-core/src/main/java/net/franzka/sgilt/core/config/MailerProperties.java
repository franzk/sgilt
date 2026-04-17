package net.franzka.sgilt.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propriétés de configuration du client mailer (sgilt-mailer).
 */
@ConfigurationProperties(prefix = "sgilt.mailer")
public record MailerProperties(
        String url,
        String from
) {}
