package net.franzka.sgilt.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propriétés de configuration pour l'intégration OpenAI.
 */
@ConfigurationProperties(prefix = "sgilt.openai")
public record OpenAiProperties(String apiKey) {}
