package net.franzka.sgilt.core.jwt.config;

import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import net.franzka.sgilt.core.jwt.service.JwtService;
import net.franzka.sgilt.core.jwt.service.TokenJwtService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration Spring du bean {@link TokenJwtService}.
 * Déclare une instance différenciée par {@link Qualifier} :
 * {@code setPasswordTokenJwtService}.
 */
@Configuration
public class JwtConfig {

    /**
     * Bean JWT pour les tokens de définition de mot de passe.
     * Sel : {@code "set-password"} — expiration alignée sur la durée de la session d'onboarding (24h).
     *
     * @param jwtService le service JWT technique de bas niveau
     * @param props      les propriétés de configuration (secret partagé, durée)
     * @return le {@link TokenJwtService} configuré pour les tokens set-password
     */
    @Bean
    @Qualifier("setPasswordTokenJwtService")
    public TokenJwtService setPasswordTokenJwtService(JwtService jwtService, ConfirmationTokenProperties props) {
        return new TokenJwtService(
                jwtService,
                props.confirmationSecret(),
                "set-password",
                Duration.ofHours(props.confirmationExpirationHours())
        );
    }
}
