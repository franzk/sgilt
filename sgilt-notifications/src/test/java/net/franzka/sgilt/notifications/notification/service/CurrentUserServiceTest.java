package net.franzka.sgilt.notifications.notification.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrentUserServiceTest {

    private final CurrentUserService currentUserService = new CurrentUserService();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Jwt jwtWithClaims(Map<String, Object> claims) {
        return new Jwt("token", Instant.now(), Instant.now().plusSeconds(60),
                Map.of("alg", "none"), claims);
    }

    @Nested
    class GetEmail {

        @Test
        void givenJwtWithEmailClaim_whenGetEmail_thenReturnsEmail() {
            Jwt jwt = jwtWithClaims(Map.of("email", "presta@example.com", "sub", "abc"));
            SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(jwt, null));

            assertThat(currentUserService.getEmail()).isEqualTo("presta@example.com");
        }

        @Test
        void givenNoAuthentication_whenGetEmail_thenThrowsIllegalState() {
            assertThatThrownBy(currentUserService::getEmail).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void givenJwtWithoutEmailClaim_whenGetEmail_thenThrowsIllegalState() {
            Jwt jwt = jwtWithClaims(Map.of("sub", "abc"));
            SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(jwt, null));

            assertThatThrownBy(currentUserService::getEmail).isInstanceOf(IllegalStateException.class);
        }
    }
}
