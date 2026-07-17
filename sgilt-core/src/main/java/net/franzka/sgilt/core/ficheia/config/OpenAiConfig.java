package net.franzka.sgilt.core.ficheia.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import net.franzka.sgilt.core.config.OpenAiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration Spring du client OpenAI.
 */
@Configuration
public class OpenAiConfig {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(90);

    /**
     * Crée le bean {@link OpenAIClient} configuré avec la clé API et un timeout adapté au temps
     * de réponse du web search + de la génération.
     *
     * @param properties les propriétés de configuration OpenAI
     * @return le client OpenAI
     */
    @Bean
    public OpenAIClient openAIClient(OpenAiProperties properties) {
        return OpenAIOkHttpClient.builder()
                .apiKey(properties.apiKey())
                .timeout(REQUEST_TIMEOUT)
                .build();
    }
}
