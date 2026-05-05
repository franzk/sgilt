package net.franzka.sgilt.core.evenement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.api.EvenementApi;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EvenementController implements EvenementApi {

    private final EvenementService evenementService;
    private final UtilisateurService utilisateurService;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<EvenementSummaryDto>> getMyEvents(Jwt jwt) {
        String email = jwt.getClaimAsString("email");

        log.info("GET /events — email={}", email);

        Utilisateur currentUser = utilisateurService.getByEmail(email);
        //TODO : vérifier le rôle du user
        return ResponseEntity.ok(evenementService.getUserEvents(currentUser.getId()));
    }
}
