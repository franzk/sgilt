package net.franzka.sgilt.core.utilisateur.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.security.CurrentUserService;
import net.franzka.sgilt.core.utilisateur.api.UtilisateurApi;
import net.franzka.sgilt.core.utilisateur.dto.UtilisateurProfileDto;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UtilisateurController implements UtilisateurApi {

    private final UtilisateurService utilisateurService;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<UtilisateurProfileDto> getMe() {
        String email = currentUserService.getEmail();
        log.info("GET /users/me — email={}", email);
        return ResponseEntity.ok(utilisateurService.getProfile(email));
    }
}
