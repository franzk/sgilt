package net.franzka.sgilt.gateway.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Rate limiter à fenêtre fixe, scopé à {@code POST /api/v1/onboarding} (init-onboarding)
 * uniquement — pas les autres routes du préfixe {@code /api/v1/onboarding/**} (verify,
 * confirm-account).
 * Rejette en {@code 429} avant tout appel au routing vers sgilt-core : en cas de dépassement,
 * aucune requête n'atteint le backend, donc aucun effet de bord (ligne DB, envoi d'email).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InitOnboardingRateLimitFilter extends OncePerRequestFilter {

    private static final String LIMITED_PATH = "/api/v1/onboarding";
    private static final int MAX_REQUESTS_PER_WINDOW = 5;
    private static final Duration WINDOW_DURATION = Duration.ofMinutes(10);

    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    private record WindowCounter(Instant windowStart, int count) {}

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equalsIgnoreCase(request.getMethod()) && LIMITED_PATH.equals(request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String key = clientKey(request);
        Instant now = Instant.now();

        WindowCounter updated = counters.compute(key, (k, existing) -> {
            if (existing == null || Duration.between(existing.windowStart(), now).compareTo(WINDOW_DURATION) >= 0) {
                return new WindowCounter(now, 1);
            }
            return new WindowCounter(existing.windowStart(), existing.count() + 1);
        });

        if (updated.count() > MAX_REQUESTS_PER_WINDOW) {
            long retryAfterSeconds = WINDOW_DURATION.minus(Duration.between(updated.windowStart(), now)).toSeconds();
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(Math.max(retryAfterSeconds, 1)));
            return;
        }

        filterChain.doFilter(request, response);
    }

    // nginx résout l'IP réelle via le module real_ip (Cloudflare CF-Connecting-IP) et la
    // transmet en X-Real-IP — seul hop de confiance entre le client et cette gateway, donc
    // source fiable pour la clé (X-Forwarded-For serait falsifiable par le client avant nginx).
    private String clientKey(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        return ip + ":init-onboarding";
    }

    /**
     * Purge périodiquement les fenêtres expirées pour borner la taille de la map en mémoire.
     */
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    void cleanupExpiredWindows() {
        Instant now = Instant.now();
        counters.entrySet().removeIf(entry ->
                Duration.between(entry.getValue().windowStart(), now).compareTo(WINDOW_DURATION) >= 0);
    }
}
