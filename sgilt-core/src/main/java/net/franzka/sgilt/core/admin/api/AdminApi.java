package net.franzka.sgilt.core.admin.api;

import jakarta.validation.Valid;
import net.franzka.sgilt.core.admin.dto.ProvisionPrestataireRequest;
import net.franzka.sgilt.core.admin.dto.ProvisionPrestataireResponse;
import net.franzka.sgilt.core.prestataire.dto.PrestataireAdminListItemDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireOnboardingPendingDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping("api/v1/admin")
public interface AdminApi {

    // Etape 1/4 : provisionnement DB + Keycloak + token de confirmation
    @PostMapping("/prestataires")
    ResponseEntity<ProvisionPrestataireResponse> provisionPrestataire(@RequestBody @Valid ProvisionPrestataireRequest request);

    @GetMapping("/prestataires")
    ResponseEntity<List<PrestataireAdminListItemDto>> listPrestataires();

    @PostMapping("/prestataires/{id}/publish")
    ResponseEntity<Void> publishPrestataire(@PathVariable UUID id);

    @PostMapping("/prestataires/{id}/send-to-review")
    ResponseEntity<Void> sendPrestataireBackToReview(@PathVariable UUID id);

    @GetMapping("/prestataires/onboarding-pending")
    ResponseEntity<List<PrestataireOnboardingPendingDto>> listPendingOnboardings();

    @PostMapping("/prestataires/{id}/resend-onboarding-email")
    ResponseEntity<Void> resendOnboardingEmail(@PathVariable UUID id);
}
