package net.franzka.sgilt.keycloak;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

/**
 * Factory SPI KC pour le {@link MagicLinkAuthenticator}.
 * Enregistré via META-INF/services.
 * Le secret HMAC est lu depuis la variable d'environnement {@code MAGIC_LINK_SECRET}
 * ou via la config SPI {@code --spi-authenticator-magic-link-secret=...}.
 */
public class MagicLinkAuthenticatorFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "magic-link";

    private String secret;

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Sgilt Magic Link";
    }

    @Override
    public String getHelpText() {
        return "Authentifie l'utilisateur via un token magique one-shot signé HMAC-SHA256.";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[]{
                AuthenticationExecutionModel.Requirement.REQUIRED,
                AuthenticationExecutionModel.Requirement.ALTERNATIVE,
                AuthenticationExecutionModel.Requirement.DISABLED
        };
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return List.of();
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new MagicLinkAuthenticator(secret);
    }

    @Override
    public void init(Config.Scope config) {
        secret = config.get("secret", System.getenv("MAGIC_LINK_SECRET"));
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}
}
