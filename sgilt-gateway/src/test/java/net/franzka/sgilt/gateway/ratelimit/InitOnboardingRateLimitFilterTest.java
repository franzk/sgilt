package net.franzka.sgilt.gateway.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitOnboardingRateLimitFilterTest {

    private static final String LIMITED_PATH = "/api/v1/onboarding";
    private static final String CLIENT_IP = "203.0.113.42";

    private final InitOnboardingRateLimitFilter filter = new InitOnboardingRateLimitFilter();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    // -------------------------------------------------------------------------
    // shouldNotFilter
    // -------------------------------------------------------------------------

    @Nested
    class ShouldNotFilter {

        @Test
        void givenPostToInitOnboarding_whenShouldNotFilter_thenReturnsFalse() {
            when(request.getMethod()).thenReturn("POST");
            when(request.getRequestURI()).thenReturn(LIMITED_PATH);

            assertThat(filter.shouldNotFilter(request)).isFalse();
        }

        @Test
        void givenGetToVerifyEndpoint_whenShouldNotFilter_thenReturnsTrue() {
            // le court-circuit du && sur la méthode HTTP évite même de lire l'URI
            when(request.getMethod()).thenReturn("GET");

            assertThat(filter.shouldNotFilter(request)).isTrue();
        }

        @Test
        void givenPostToConfirmAccount_whenShouldNotFilter_thenReturnsTrue() {
            when(request.getMethod()).thenReturn("POST");
            when(request.getRequestURI()).thenReturn(LIMITED_PATH + "/confirm-account");

            assertThat(filter.shouldNotFilter(request)).isTrue();
        }

        @Test
        void givenGetToInitOnboarding_whenShouldNotFilter_thenReturnsTrueWithoutReadingUri() {
            // même chemin exact que la route ciblée : seule la méthode HTTP doit suffire à
            // exclure la requête, sans même lire l'URI (court-circuit du &&)
            when(request.getMethod()).thenReturn("GET");

            boolean result = filter.shouldNotFilter(request);

            assertThat(result).isTrue();
            verify(request, never()).getRequestURI();
        }
    }

    // -------------------------------------------------------------------------
    // doFilterInternal
    // -------------------------------------------------------------------------

    @Nested
    class DoFilterInternal {

        @Test
        void givenRequestsWithinThreshold_whenDoFilterInternal_thenAllPassThrough() throws Exception {
            when(request.getHeader("X-Real-IP")).thenReturn(CLIENT_IP);

            for (int i = 0; i < 5; i++) {
                filter.doFilterInternal(request, response, filterChain);
            }

            verify(filterChain, times(5)).doFilter(request, response);
            verify(response, never()).setStatus(429);
        }

        @Test
        void givenRequestsBeyondThreshold_whenDoFilterInternal_thenExtraRequestIsRejectedWith429() throws Exception {
            when(request.getHeader("X-Real-IP")).thenReturn(CLIENT_IP);

            for (int i = 0; i < 5; i++) {
                filter.doFilterInternal(request, response, filterChain);
            }
            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain, times(5)).doFilter(any(), any());
            verify(response).setStatus(429);
            verify(response).setHeader(eq("Retry-After"), any());
        }

        @Test
        void givenDifferentIpAddresses_whenDoFilterInternal_thenEachHasIndependentLimit() throws Exception {
            when(request.getHeader("X-Real-IP")).thenReturn(CLIENT_IP, CLIENT_IP, CLIENT_IP, CLIENT_IP, CLIENT_IP, "203.0.113.99");

            for (int i = 0; i < 6; i++) {
                filter.doFilterInternal(request, response, filterChain);
            }

            // 5 pour la première IP + 1 pour la seconde, aucune n'atteint le seuil
            verify(filterChain, times(6)).doFilter(request, response);
            verify(response, never()).setStatus(429);
        }

        @Test
        void givenNoXRealIpHeader_whenDoFilterInternal_thenFallsBackToRemoteAddr() throws Exception {
            when(request.getHeader("X-Real-IP")).thenReturn(null);
            when(request.getRemoteAddr()).thenReturn(CLIENT_IP);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            verify(response, never()).setStatus(429);
        }
    }
}
