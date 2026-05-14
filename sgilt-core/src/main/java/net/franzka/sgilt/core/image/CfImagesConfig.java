package net.franzka.sgilt.core.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Configuration du client HTTP vers l'API Cloudflare Images.
 */
@Configuration
public class CfImagesConfig {

    /**
     * Crée le client déclaratif {@link CfImagesClient} configuré avec l'URL de base,
     * le token d'authentification et Apache HttpClient 5 pour les uploads multipart.
     *
     * @param apiUrl   URL de base de l'API (ex. {@code http://localhost:5029/images/v1})
     * @param apiToken token Bearer CF Images
     * @return le proxy implémentant {@link CfImagesClient}
     */
    @Bean
    public CfImagesClient cfImagesClient(
            @Value("${sgilt.cf-images.api-url}") String apiUrl,
            @Value("${sgilt.cf-images.api-token}") String apiToken
    ) {
        var httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        RestClient restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .defaultHeader("Authorization", "Bearer " + apiToken)
                .build();

        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(CfImagesClient.class);
    }
}
