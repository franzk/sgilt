package net.franzka.sgilt.notifications.notification.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Résout l'identité de l'appelant authentifié à partir du contexte de sécurité courant.
 * Notifications étant génériques tous rôles, l'email JWT est la seule clé de résolution nécessaire
 * (pas de table {@code Utilisateur} dans ce service — cf. plan de la pré-tâche).
 */
@Service
public class CurrentUserService {

    /**
     * Retourne l'email de l'appelant connecté extrait du JWT.
     *
     * @return l'email
     * @throws IllegalStateException si le contexte de sécurité ne contient pas de JWT valide
     *                               ou si le claim {@code email} est absent
     */
    public String getEmail() {
        Jwt jwt = getJwt();
        String email = jwt.getClaimAsString("email");
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("JWT missing required claim: 'email'");
        }
        return email;
    }

    /**
     * Retourne le JWT de l'appelant connecté.
     *
     * @return le JWT
     * @throws IllegalStateException si aucun JWT valide n'est trouvé dans le contexte de sécurité
     */
    public Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof Authentication a) || !(a.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No JWT authentication found in security context");
        }
        return jwt;
    }
}
