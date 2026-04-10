package net.franzka.sgilt.core.onboarding.api;

import jakarta.validation.Valid;
import net.franzka.sgilt.core.onboarding.api.dto.DemandeInitialeResponse;
import net.franzka.sgilt.core.onboarding.api.dto.NewEvenementRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/v1/onboarding")
public interface OnboardingApi {

    @PostMapping()
    ResponseEntity<DemandeInitialeResponse> submitDemandeTunnel(
            @RequestBody @Valid NewEvenementRequest request
    );
}
