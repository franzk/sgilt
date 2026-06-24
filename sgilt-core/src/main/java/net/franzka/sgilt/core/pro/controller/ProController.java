package net.franzka.sgilt.core.pro.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.pro.api.ProApi;
import net.franzka.sgilt.core.pro.dto.ProMeDto;
import net.franzka.sgilt.core.pro.service.ProProvisioningService;
import net.franzka.sgilt.core.security.CurrentUserService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ROLE_PRO')")
public class ProController implements ProApi {

    private final CurrentUserService currentUserService;
    private final ProProvisioningService proProvisioningService;
    private final PrestataireService prestataireService;

    @Override
    @Transactional
    public ResponseEntity<ProMeDto> getMe() {
        // 1 - check du jwt pour voir si on est dans le cas d'une création de prestataire (claim bootstrap_prestataire_slug)
        proProvisioningService.provisionIfNeeded();

        // 2 - récupération de l'utilisateur
        Utilisateur utilisateur = currentUserService.get();
        log.info("GET /pro/me — email={}", utilisateur.getEmail());

        String slug = prestataireService.getSlugByUtilisateur(utilisateur);

        return ResponseEntity.ok(new ProMeDto(
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getFirstName(),
                utilisateur.getLastName(),
                slug
        ));
    }
}
