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

    // Fenêtre courte et distincte de la validité du lien email (confirmationExpirationHours, 24h) :
    // une fois le lien vérifié, le mot de passe doit être soumis rapidement.
    private static final Duration SET_PASSWORD_TOKEN_TTL = Duration.ofMinutes(5);

    /**
     * Bean JWT pour les tokens de définition de mot de passe.
     * Sel : {@code "set-password"} — expiration courte ({@link #SET_PASSWORD_TOKEN_TTL}),
     * volontairement indépendante de la durée de validité du lien email.
     *
     * @param jwtService le service JWT technique de bas niveau
     * @param props      les propriétés de configuration (secret partagé)
     * @return le {@link TokenJwtService} configuré pour les tokens set-password
     */
    @Bean
    public TokenJwtService setPasswordTokenJwtService(JwtService jwtService, ConfirmationTokenProperties props) {
        return new TokenJwtService(
                jwtService,
                props.confirmationSecret(),
                "set-password",
                SET_PASSWORD_TOKEN_TTL
        );
    }
}
