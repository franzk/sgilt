package net.franzka.sgilt.core.onboarding.api;

import jakarta.validation.Valid;
import net.franzka.sgilt.core.onboarding.dto.DemandeInitialeResponse;
import net.franzka.sgilt.core.onboarding.dto.NewEvenementRequest;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountRequest;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountResponse;
import net.franzka.sgilt.core.onboarding.dto.VerifyTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/onboarding")
public interface OnboardingApi {

    @PostMapping()
    ResponseEntity<DemandeInitialeResponse> submitDemandeTunnel(
            @RequestBody @Valid NewEvenementRequest request
    );

    @GetMapping("/verify-token")
    ResponseEntity<VerifyTokenResponse> verifyToken(
            @RequestParam String token
    );

    @PostMapping("/confirm-account")
    ResponseEntity<ConfirmAccountResponse> confirmAccount(
            @RequestBody @Valid ConfirmAccountRequest request
    );
}
