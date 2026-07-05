package net.franzka.sgilt.core.admin.api;

import jakarta.validation.Valid;
import net.franzka.sgilt.core.admin.dto.ProvisionPrestataireRequest;
import net.franzka.sgilt.core.admin.dto.ProvisionPrestataireResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/v1/admin")
public interface AdminApi {

    // Etape 1/4 : provisionnement DB + Keycloak + token de confirmation
    @PostMapping("/prestataires")
    ResponseEntity<ProvisionPrestataireResponse> provisionPrestataire(@RequestBody @Valid ProvisionPrestataireRequest request);
}
