package net.franzka.sgilt.core.onboarding.api;

import jakarta.validation.Valid;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountRequest;
import net.franzka.sgilt.core.onboarding.dto.ConfirmAccountResponse;
import net.franzka.sgilt.core.onboarding.dto.DemandeInitialeRequest;
import net.franzka.sgilt.core.onboarding.dto.DemandeInitialeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/v1/onboarding")
public interface OnboardingApi {

    @PostMapping()
    ResponseEntity<DemandeInitialeResponse> submitDemandeInitiale(@RequestBody @Valid DemandeInitialeRequest request);

    @PostMapping("/confirm-account")
    ResponseEntity<ConfirmAccountResponse> confirmAccount(@RequestBody @Valid ConfirmAccountRequest request);
}
