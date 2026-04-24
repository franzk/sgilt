package net.franzka.sgilt.core.onboarding.api;

import jakarta.validation.Valid;
import net.franzka.sgilt.core.onboarding.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/onboarding")
public interface OnboardingApi {

    // Etape 1 : Demande initiale, soumission du tunnel de demande de réservation
    @PostMapping()
    ResponseEntity<InitOnboardingResponse> initOnboarding(@RequestBody @Valid InitOnboardingRequest request);

    // Etape 2 : vérification de l'email du client
    @GetMapping("/verify")
    ResponseEntity<SetPasswordTokenDto> verifyToken(@RequestParam String token);

    // Etape 3 : confirmation de la création du compte
    @PostMapping("/confirm-account")
    ResponseEntity<ConfirmAccountResponse> confirmAccount(@RequestBody @Valid ConfirmAccountRequest request);
}
