package net.franzka.sgilt.core.onboarding.mapper;

import net.franzka.sgilt.core.evenement.dto.EvenementDto;
import net.franzka.sgilt.core.evenement.dto.RubriqueDto;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingRequest;
import net.franzka.sgilt.core.onboarding.dto.OnboardingPendingDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OnboardingMapper {

    /**
     * Mappe une session en attente et son événement vers le DTO de suivi admin.
     *
     * @param onboarding la session
     * @param content    les coordonnées et l'événement de la session
     * @return le DTO de suivi
     */
    default OnboardingPendingDto toPendingDto(Onboarding onboarding, InitOnboardingRequest content) {
        EvenementDto evenement = content.evenement();
        return new OnboardingPendingDto(
                onboarding.getId(),
                onboarding.getEmail(),
                evenement.eventType(),
                evenement.date(),
                RubriqueDto.demandesInitiees(evenement.rubriques()).size(),
                onboarding.getState(),
                onboarding.getCreatedAt(),
                onboarding.getExpiresAt());
    }
}
