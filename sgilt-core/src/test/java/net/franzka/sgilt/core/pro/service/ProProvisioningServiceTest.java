package net.franzka.sgilt.core.pro.service;

import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.security.CurrentUserService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProProvisioningServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private PrestataireService prestataireService;

    @InjectMocks
    private ProProvisioningService proProvisioningService;

    private Jwt jwtWithClaims(String bootstrapSlug, String email) {
        var builder = Jwt.withTokenValue("token-value").header("alg", "none")
                .claim("email", email)
                .claim("given_name", "Jean")
                .claim("family_name", "Dupont")
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(3600));
        if (bootstrapSlug != null) {
            builder.claim("bootstrap_prestataire_slug", bootstrapSlug);
        }
        return builder.build();
    }

    @Nested
    class ProvisionIfNeeded {

        @Test
        void givenNoBootstrapSlugClaim_whenProvisionIfNeeded_thenDoesNothing() {
            when(currentUserService.getJwt()).thenReturn(jwtWithClaims(null, "pro@sgilt.fr"));

            proProvisioningService.provisionIfNeeded();

            verify(utilisateurService, never()).existsByEmail(any());
        }

        @Test
        void givenBlankBootstrapSlugClaim_whenProvisionIfNeeded_thenDoesNothing() {
            Jwt jwt = Jwt.withTokenValue("token-value").header("alg", "none")
                    .claim("email", "pro@sgilt.fr").claim("bootstrap_prestataire_slug", "  ")
                    .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(3600)).build();
            when(currentUserService.getJwt()).thenReturn(jwt);

            proProvisioningService.provisionIfNeeded();

            verify(utilisateurService, never()).existsByEmail(any());
        }

        @Test
        void givenBootstrapSlugAndExistingEmail_whenProvisionIfNeeded_thenDoesNotCreateUtilisateur() {
            when(currentUserService.getJwt()).thenReturn(jwtWithClaims("studio-fleur", "pro@sgilt.fr"));
            when(utilisateurService.existsByEmail("pro@sgilt.fr")).thenReturn(true);

            proProvisioningService.provisionIfNeeded();

            verify(utilisateurService, never()).createUtilisateur(any(), any(), any(), any());
        }

        @Test
        void givenBootstrapSlugAndNewEmail_whenProvisionIfNeeded_thenCreatesUtilisateurAndLinksPrestataire() {
            when(currentUserService.getJwt()).thenReturn(jwtWithClaims("studio-fleur", "pro@sgilt.fr"));
            when(utilisateurService.existsByEmail("pro@sgilt.fr")).thenReturn(false);
            Utilisateur utilisateur = Utilisateur.builder().email("pro@sgilt.fr").build();
            when(utilisateurService.createUtilisateur("Jean", "Dupont", "pro@sgilt.fr", null)).thenReturn(utilisateur);

            proProvisioningService.provisionIfNeeded();

            verify(prestataireService).linkBootstrapUtilisateur("studio-fleur", utilisateur);
        }
    }
}
