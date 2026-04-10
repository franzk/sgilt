package net.franzka.sgilt.core.onboarding.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.onboarding.api.OnboardingApi;
import net.franzka.sgilt.core.onboarding.api.dto.DemandeInitialeResponse;
import net.franzka.sgilt.core.onboarding.api.dto.NewEvenementRequest;
import net.franzka.sgilt.core.onboarding.service.OnboardingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OnboardingController implements OnboardingApi {

    private final OnboardingService onboardingService;

    @Override
    public ResponseEntity<DemandeInitialeResponse> submitDemandeTunnel(
            @RequestBody @Valid NewEvenementRequest request
    ) {
        return ResponseEntity.accepted().body(
                onboardingService.submitDemandeTunnel(request)
        );
    }
}
