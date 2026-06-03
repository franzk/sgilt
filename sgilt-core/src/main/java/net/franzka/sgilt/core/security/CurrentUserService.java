package net.franzka.sgilt.core.security;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Résout l'utilisateur authentifié à partir du contexte de sécurité courant.
 * Centralise l'extraction JWT pour tous les controllers.
 */
@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UtilisateurService utilisateurService;

    /**
     * Retourne l'entité {@link Utilisateur} de l'utilisateur connecté.
     *
     * @return l'utilisateur courant
     */
    public Utilisateur get() {
        return utilisateurService.getByEmail(getEmail());
    }

    /**
     * Retourne l'identifiant de l'utilisateur connecté.
     *
     * @return l'UUID de l'utilisateur courant
     */
    public UUID getId() {
        return get().getId();
    }

    /**
     * Retourne l'email de l'utilisateur connecté extrait du JWT.
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
     * Retourne le JWT de l'utilisateur connecté.
     *
     * @return le JWT
     * @throws IllegalStateException si aucun JWT valide n'est trouvé dans le contexte de sécurité
     */
    public Jwt getJwt() {
        return extractJwt();
    }

    private Jwt extractJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof Authentication a) || !(a.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No JWT authentication found in security context");
        }
        return jwt;
    }
}
