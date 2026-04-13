package net.franzka.sgilt.core.jwt;

import net.franzka.sgilt.core.config.ConfirmationTokenProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration Spring des beans {@link TokenJwtService}.
 * Déclare deux instances différenciées par {@link Qualifier} :
 * {@code confirmationTokenJwtService} et {@code setPasswordTokenJwtService}.
 */
@Configuration
public class JwtConfig {

    /**
     * Bean JWT pour les tokens de confirmation d'email.
     * Sel : {@code "confirmation"} — expiration depuis {@link ConfirmationTokenProperties}.
     *
     * @param jwtService le service JWT technique de bas niveau
     * @param props      les propriétés de configuration des tokens
     * @return le {@link TokenJwtService} configuré pour les tokens de confirmation
     */
    @Bean
    @Qualifier("confirmationTokenJwtService")
    public TokenJwtService confirmationTokenJwtService(JwtService jwtService, ConfirmationTokenProperties props) {
        return new TokenJwtService(
                jwtService,
                props.confirmationSecret(),
                "confirmation",
                Duration.ofHours(props.confirmationExpirationHours())
        );
    }

    /**
     * Bean JWT pour les tokens de définition de mot de passe.
     * Sel : {@code "set-password"} — expiration fixe de 5 minutes.
     *
     * @param jwtService le service JWT technique de bas niveau
     * @param props      les propriétés de configuration (secret partagé)
     * @return le {@link TokenJwtService} configuré pour les tokens set-password
     */
    @Bean
    @Qualifier("setPasswordTokenJwtService")
    public TokenJwtService setPasswordTokenJwtService(JwtService jwtService, ConfirmationTokenProperties props) {
        return new TokenJwtService(
                jwtService,
                props.confirmationSecret(),
                "set-password",
                Duration.ofMinutes(5)
        );
    }
}
