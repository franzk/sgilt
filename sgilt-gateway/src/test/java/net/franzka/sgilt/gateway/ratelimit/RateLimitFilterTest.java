package net.franzka.sgilt.gateway.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    private static final String CLIENT_IP = "203.0.113.42";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    // -------------------------------------------------------------------------
    // shouldNotFilter — règles réelles de RateLimitRules
    // -------------------------------------------------------------------------

    @Nested
    class ShouldNotFilter {

        private final RateLimitFilter filter = new RateLimitFilter();

        private void givenRequest(String method, String uri) {
            when(request.getMethod()).thenReturn(method);
            when(request.getRequestURI()).thenReturn(uri);
        }

        @Test
        void givenPostToInitOnboarding_whenShouldNotFilter_thenReturnsFalse() {
            givenRequest("POST", "/api/v1/onboarding");

            assertThat(filter.shouldNotFilter(request)).isFalse();
        }

        @Test
        void givenPostToInitOnboardingDemande_whenShouldNotFilter_thenReturnsFalse() {
            givenRequest("POST", "/api/v1/onboarding/demande");

            assertThat(filter.shouldNotFilter(request)).isFalse();
        }

        @Test
        void givenPostToConfirmAccount_whenShouldNotFilter_thenReturnsTrue() {
            givenRequest("POST", "/api/v1/onboarding/confirm-account");

            assertThat(filter.shouldNotFilter(request)).isTrue();
        }

        @Test
        void givenPostToCommandes_whenShouldNotFilter_thenReturnsFalse() {
            givenRequest("POST", "/api/v1/commandes");

            assertThat(filter.shouldNotFilter(request)).isFalse();
        }

        @Test
        void givenGetOfCommande_whenShouldNotFilter_thenReturnsTrue() {
            // la page de confirmation relit la commande toutes les 2 s : jamais limitée
            when(request.getMethod()).thenReturn("GET");

            assertThat(filter.shouldNotFilter(request)).isTrue();
        }

        @Test
        void givenGetToLimitedPath_whenShouldNotFilter_thenReturnsTrueWithoutReadingUri() {
            // même chemin exact qu'une règle : seule la méthode HTTP doit suffire à exclure la
            // requête, sans même lire l'URI (court-circuit du &&)
            when(request.getMethod()).thenReturn("GET");

            boolean result = filter.shouldNotFilter(request);

            assertThat(result).isTrue();
            verify(request, never()).getRequestURI();
        }
    }

    // -------------------------------------------------------------------------
    // doFilterInternal — règles de test, à petits seuils
    // -------------------------------------------------------------------------

    @Nested
    class DoFilterInternal {

        private static final RateLimitRule SHARED = new RateLimitRule(
                "shared", "POST", Set.of("/a", "/b"), 3, Duration.ofMinutes(10));
        private static final RateLimitRule OTHER = new RateLimitRule(
                "other", "POST", Set.of("/c"), 2, Duration.ofMinutes(10));

        private final RateLimitFilter filter = new RateLimitFilter(List.of(SHARED, OTHER));

        private void givenPost(String uri) {
            when(request.getMethod()).thenReturn("POST");
            when(request.getRequestURI()).thenReturn(uri);
        }

        @Test
        void givenRequestsWithinThreshold_whenDoFilterInternal_thenAllPassThrough() throws Exception {
            givenPost("/a");
            when(request.getHeader("X-Real-IP")).thenReturn(CLIENT_IP);

            for (int i = 0; i < 3; i++) {
                filter.doFilterInternal(request, response, filterChain);
            }

            verify(filterChain, times(3)).doFilter(request, response);
            verify(response, never()).setStatus(429);
        }

        @Test
        void givenRequestsBeyondThreshold_whenDoFilterInternal_thenExtraRequestIsRejectedWith429() throws Exception {
            givenPost("/a");
            when(request.getHeader("X-Real-IP")).thenReturn(CLIENT_IP);

            for (int i = 0; i < 4; i++) {
                filter.doFilterInternal(request, response, filterChain);
            }

            verify(filterChain, times(3)).doFilter(any(), any());
            verify(response).setStatus(429);
            verify(response).setHeader(eq("Retry-After"), any());
        }

        @Test
        void givenPathsOfSameRule_whenDoFilterInternal_thenTheyShareOneCounter() throws Exception {
            when(request.getMethod()).thenReturn("POST");
            when(request.getRequestURI()).thenReturn("/a", "/b", "/a", "/b");
            when(request.getHeader("X-Real-IP")).thenReturn(CLIENT_IP);

            for (int i = 0; i < 4; i++) {
                filter.doFilterInternal(request, response, filterChain);
            }

            verify(filterChain, times(3)).doFilter(any(), any());
            verify(response).setStatus(429);
        }

        @Test
        void givenRuleAtItsLimit_whenDoFilterInternalOnOtherRule_thenOtherRuleHasItsOwnCounter() throws Exception {
            when(request.getMethod()).thenReturn("POST");
            when(request.getRequestURI()).thenReturn("/a", "/a", "/a", "/c");
            when(request.getHeader("X-Real-IP")).thenReturn(CLIENT_IP);

            for (int i = 0; i < 4; i++) {
                filter.doFilterInternal(request, response, filterChain);
            }

            verify(filterChain, times(4)).doFilter(request, response);
            verify(response, never()).setStatus(429);
        }

        @Test
        void givenDifferentIpAddresses_whenDoFilterInternal_thenEachHasIndependentLimit() throws Exception {
            givenPost("/a");
            when(request.getHeader("X-Real-IP")).thenReturn(CLIENT_IP, CLIENT_IP, CLIENT_IP, "203.0.113.99");

            for (int i = 0; i < 4; i++) {
                filter.doFilterInternal(request, response, filterChain);
            }

            // 3 pour la première IP + 1 pour la seconde, aucune n'atteint le seuil
            verify(filterChain, times(4)).doFilter(request, response);
            verify(response, never()).setStatus(429);
        }

        @Test
        void givenNoXRealIpHeader_whenDoFilterInternal_thenFallsBackToRemoteAddr() throws Exception {
            givenPost("/a");
            when(request.getHeader("X-Real-IP")).thenReturn(null);
            when(request.getRemoteAddr()).thenReturn(CLIENT_IP);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            verify(response, never()).setStatus(429);
        }
    }
}
