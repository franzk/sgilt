package net.franzka.sgilt.gateway.ratelimit;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Duration;
import java.util.Set;

/**
 * Règle de limitation de débit, par IP et à fenêtre fixe : au plus {@code maxRequests} requêtes
 * {@code method} sur {@code paths} pendant {@code window}. Les chemins d'une même règle partagent
 * le même compteur.
 *
 * @param name        le nom de la règle, qui sépare ses compteurs de ceux des autres règles
 * @param method      la méthode HTTP limitée
 * @param paths       les chemins exacts limités
 * @param maxRequests le nombre de requêtes autorisées par fenêtre
 * @param window      la durée de la fenêtre
 */
public record RateLimitRule(String name, String method, Set<String> paths, int maxRequests, Duration window) {

    /**
     * Indique si la requête tombe sous cette règle. La méthode HTTP est testée d'abord : une
     * requête d'une autre méthode est écartée sans lire son URI.
     *
     * @param request la requête entrante
     * @return {@code true} si la méthode et le chemin correspondent
     */
    public boolean matches(HttpServletRequest request) {
        return method.equalsIgnoreCase(request.getMethod()) && paths.contains(request.getRequestURI());
    }
}
