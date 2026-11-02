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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Rate limiter par IP à fenêtre fixe, qui applique les règles de {@link RateLimitRules} : chaque
 * règle a ses propres compteurs, une requête qui ne tombe sous aucune règle passe sans compter.
 * Rejette en {@code 429} avant tout appel au routing vers le backend : en cas de dépassement,
 * aucune requête ne l'atteint, donc aucun effet de bord (ligne DB, envoi d'email, PaymentIntent).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter extends OncePerRequestFilter {

    private final List<RateLimitRule> rules;
    private final Map<CounterKey, WindowCounter> counters = new ConcurrentHashMap<>();

    private record CounterKey(RateLimitRule rule, String ip) {}

    private record WindowCounter(Instant windowStart, int count) {}

    /**
     * Construit le filtre avec les règles de {@link RateLimitRules}.
     */
    public RateLimitFilter() {
        this(RateLimitRules.RULES);
    }

    // règles fournies par les tests
    RateLimitFilter(List<RateLimitRule> rules) {
        this.rules = rules;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return rules.stream().noneMatch(rule -> rule.matches(request));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        RateLimitRule rule = rules.stream().filter(r -> r.matches(request)).findFirst().orElseThrow();
        Duration window = rule.window();
        Instant now = Instant.now();

        WindowCounter updated = counters.compute(new CounterKey(rule, clientIp(request)), (_, existing) -> {
            if (existing == null || Duration.between(existing.windowStart(), now).compareTo(window) >= 0) {
                return new WindowCounter(now, 1);
            }
            return new WindowCounter(existing.windowStart(), existing.count() + 1);
        });

        if (updated.count() > rule.maxRequests()) {
            long retryAfterSeconds = window.minus(Duration.between(updated.windowStart(), now)).toSeconds();
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(Math.max(retryAfterSeconds, 1)));
            return;
        }

        filterChain.doFilter(request, response);
    }

    // nginx résout l'IP réelle via le module real_ip (Cloudflare CF-Connecting-IP) et la
    // transmet en X-Real-IP — seul hop de confiance entre le client et cette gateway, donc
    // source fiable pour la clé (X-Forwarded-For serait falsifiable par le client avant nginx).
    private String clientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * Purge périodiquement les fenêtres expirées pour borner la taille de la map en mémoire.
     */
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    void cleanupExpiredWindows() {
        Instant now = Instant.now();
        counters.entrySet().removeIf(entry ->
                Duration.between(entry.getValue().windowStart(), now).compareTo(entry.getKey().rule().window()) >= 0);
    }
}
