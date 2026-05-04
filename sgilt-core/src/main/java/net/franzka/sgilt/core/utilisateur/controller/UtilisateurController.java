package net.franzka.sgilt.core.utilisateur.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.utilisateur.api.UtilisateurApi;
import net.franzka.sgilt.core.utilisateur.dto.UtilisateurProfileDto;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UtilisateurController implements UtilisateurApi {

    private final UtilisateurService utilisateurService;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<UtilisateurProfileDto> getMe(Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        log.info("GET /users/me — email={}", email);
        return ResponseEntity.ok(utilisateurService.getProfile(email));
    }
}
