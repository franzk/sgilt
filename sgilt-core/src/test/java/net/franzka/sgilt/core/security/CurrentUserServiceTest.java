package net.franzka.sgilt.core.security;

import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private CurrentUserService currentUserService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private Jwt jwtWithEmail(String email) {
        return Jwt.withTokenValue("token-value")
                .header("alg", "none")
                .claim("email", email)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    private void authenticateWithJwt(Jwt jwt, String... authorities) {
        List<SimpleGrantedAuthority> granted = Stream.of(authorities).map(SimpleGrantedAuthority::new).toList();
        Authentication auth = new UsernamePasswordAuthenticationToken(jwt, null, granted);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // -------------------------------------------------------------------------
    // get / getId
    // -------------------------------------------------------------------------

    @Nested
    class Get {

        @Test
        void givenAuthenticatedJwt_whenGet_thenReturnsUtilisateurResolvedByEmail() {
            authenticateWithJwt(jwtWithEmail("pro@sgilt.fr"));
            Utilisateur utilisateur = Utilisateur.builder().id(UUID.randomUUID()).email("pro@sgilt.fr").build();
            when(utilisateurService.getByEmail("pro@sgilt.fr")).thenReturn(utilisateur);

            assertThat(currentUserService.get()).isEqualTo(utilisateur);
        }

        @Test
        void givenAuthenticatedJwt_whenGetId_thenReturnsUtilisateurId() {
            authenticateWithJwt(jwtWithEmail("pro@sgilt.fr"));
            UUID id = UUID.randomUUID();
            Utilisateur utilisateur = Utilisateur.builder().id(id).email("pro@sgilt.fr").build();
            when(utilisateurService.getByEmail("pro@sgilt.fr")).thenReturn(utilisateur);

            assertThat(currentUserService.getId()).isEqualTo(id);
        }
    }

    // -------------------------------------------------------------------------
    // getEmail
    // -------------------------------------------------------------------------

    @Nested
    class GetEmail {

        @Test
        void givenEmailClaim_whenGetEmail_thenReturnsIt() {
            authenticateWithJwt(jwtWithEmail("pro@sgilt.fr"));

            assertThat(currentUserService.getEmail()).isEqualTo("pro@sgilt.fr");
        }

        @Test
        void givenMissingEmailClaim_whenGetEmail_thenThrowsIllegalState() {
            Jwt jwt = Jwt.withTokenValue("token-value").header("alg", "none")
                    .claim("sub", "abc").issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(3600)).build();
            authenticateWithJwt(jwt);

            assertThatThrownBy(() -> currentUserService.getEmail()).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void givenBlankEmailClaim_whenGetEmail_thenThrowsIllegalState() {
            authenticateWithJwt(jwtWithEmail("  "));

            assertThatThrownBy(() -> currentUserService.getEmail()).isInstanceOf(IllegalStateException.class);
        }
    }

    // -------------------------------------------------------------------------
    // getJwt
    // -------------------------------------------------------------------------

    @Nested
    class GetJwt {

        @Test
        void givenJwtAuthentication_whenGetJwt_thenReturnsJwt() {
            Jwt jwt = jwtWithEmail("pro@sgilt.fr");
            authenticateWithJwt(jwt);

            assertThat(currentUserService.getJwt()).isEqualTo(jwt);
        }

        @Test
        void givenNoAuthentication_whenGetJwt_thenThrowsIllegalState() {
            assertThatThrownBy(() -> currentUserService.getJwt()).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void givenAuthenticationWithoutJwtPrincipal_whenGetJwt_thenThrowsIllegalState() {
            SecurityContextHolder.getContext().setAuthentication(
                    new TestingAuthenticationToken("user", "credentials"));

            assertThatThrownBy(() -> currentUserService.getJwt()).isInstanceOf(IllegalStateException.class);
        }
    }

    // -------------------------------------------------------------------------
    // isPro
    // -------------------------------------------------------------------------

    @Nested
    class IsPro {

        @Test
        void givenRolePro_whenIsPro_thenReturnsTrue() {
            authenticateWithJwt(jwtWithEmail("pro@sgilt.fr"), "ROLE_PRO");

            assertThat(currentUserService.isPro()).isTrue();
        }

        @Test
        void givenRoleUser_whenIsPro_thenReturnsFalse() {
            authenticateWithJwt(jwtWithEmail("client@sgilt.fr"), "ROLE_USER");

            assertThat(currentUserService.isPro()).isFalse();
        }

        @Test
        void givenNoAuthentication_whenIsPro_thenReturnsFalse() {
            assertThat(currentUserService.isPro()).isFalse();
        }
    }
}
