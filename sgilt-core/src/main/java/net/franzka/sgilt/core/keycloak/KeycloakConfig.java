package net.franzka.sgilt.core.keycloak;

import net.franzka.sgilt.core.config.KeycloakProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configuration Spring des beans HTTP déclaratifs pour Keycloak.
 */
@Configuration
public class KeycloakConfig {

    /**
     * Crée le bean {@link KeycloakTokenClient} pointant vers l'URL admin Keycloak.
     *
     * @param properties les propriétés de configuration Keycloak
     * @return le proxy HTTP pour le token endpoint Keycloak
     */
    @Bean
    public KeycloakTokenClient keycloakTokenClient(KeycloakProperties properties) {
        return buildProxy(properties.adminUrl(), KeycloakTokenClient.class);
    }

    /**
     * Crée le bean {@link KeycloakAdminClient} pointant vers l'URL admin Keycloak.
     *
     * @param properties les propriétés de configuration Keycloak
     * @return le proxy HTTP pour l'API Admin Keycloak
     */
    @Bean
    public KeycloakAdminClient keycloakAdminClient(KeycloakProperties properties) {
        return buildProxy(properties.adminUrl(), KeycloakAdminClient.class);
    }

    private <T> T buildProxy(String baseUrl, Class<T> clientClass) {
        RestClient restClient = RestClient.create(baseUrl);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(clientClass);
    }
}
