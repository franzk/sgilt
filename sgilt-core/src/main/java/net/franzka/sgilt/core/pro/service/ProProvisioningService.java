package net.franzka.sgilt.core.pro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.security.CurrentUserService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Gère le bootstrap du compte PRO lors du premier accès à l'espace pro.
 * Crée l'Utilisateur en DB s'il est absent, puis lie le prestataire
 * si l'attribut KC {@code bootstrap_prestataire_slug} est présent.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProProvisioningService {

    private final CurrentUserService currentUserService;
    private final UtilisateurService utilisateurService;
    private final PrestataireService prestataireService;

    /**
     * Crée l'utilisateur PRO et lie son prestataire
     * si le claim {@code bootstrap_prestataire_slug} est présent
     * et si l'email absent en base.
     */
    public void provisionIfNeeded() {
        Jwt jwt = currentUserService.getJwt();

        // est-ce qu'on est dans le cas de création d'un prestataire ?
        String bootstrapSlug = jwt.getClaimAsString("bootstrap_prestataire_slug");
        if (bootstrapSlug == null || bootstrapSlug.isBlank()) return;

        // est-ce que l'email est bien absent en base ?
        String email = jwt.getClaimAsString("email");
        if (utilisateurService.existsByEmail(email)) return;

        // Création de l'utilisateur et lien avec le prestataire
        log.info("PRO bootstrap — email={} slug={}", email, bootstrapSlug);
        Utilisateur utilisateur = utilisateurService.createUtilisateur(
                jwt.getClaimAsString("given_name"),
                jwt.getClaimAsString("family_name"),
                email,
                null
        );
        prestataireService.linkBootstrapUtilisateur(bootstrapSlug, utilisateur);
    }
}
