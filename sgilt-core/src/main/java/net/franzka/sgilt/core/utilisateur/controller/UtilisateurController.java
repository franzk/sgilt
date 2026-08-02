package net.franzka.sgilt.core.utilisateur.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.security.CurrentUserService;
import net.franzka.sgilt.core.utilisateur.api.UtilisateurApi;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.dto.UtilisateurEditDto;
import net.franzka.sgilt.core.utilisateur.dto.UtilisateurProfileDto;
import net.franzka.sgilt.core.utilisateur.dto.UtilisateurUpdateDto;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UtilisateurController implements UtilisateurApi {

    private final UtilisateurService utilisateurService;
    private final CurrentUserService currentUserService;

    @Override
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Transactional(readOnly = true)
    public ResponseEntity<UtilisateurProfileDto> getMe() {
        String email = currentUserService.getEmail();
        log.info("GET /users/me — email={}", email);
        return ResponseEntity.ok(utilisateurService.getProfile(email));
    }

    /**
     * Retourne les champs éditables (prénom, nom, téléphone, email) du profil de l'utilisateur connecté.
     * Accessible à tous les rôles (client, prestataire, admin) : ils éditent tous le même profil.
     *
     * @return le profil éditable
     */
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_PRO', 'ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<UtilisateurEditDto> getEditProfile() {
        Utilisateur utilisateur = currentUserService.get();
        log.info("GET /users/me/edit — email={}", utilisateur.getEmail());
        return ResponseEntity.ok(utilisateurService.getEditProfile(utilisateur));
    }

    /**
     * Met à jour le prénom et/ou le nom de l'utilisateur connecté.
     * Accessible à tous les rôles (client, prestataire, admin) : ils éditent tous le même profil.
     *
     * @param dto les champs à mettre à jour (null = non modifié)
     * @return 204 No Content
     */
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_PRO', 'ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<Void> updateMe(UtilisateurUpdateDto dto) {
        Utilisateur utilisateur = currentUserService.get();
        log.info("PATCH /users/me/edit — email={}", utilisateur.getEmail());
        utilisateurService.updateProfile(utilisateur, dto);
        return ResponseEntity.noContent().build();
    }
}
