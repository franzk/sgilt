package net.franzka.sgilt.core.onboarding.mapper;

import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.dto.OnboardingPendingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OnboardingMapper {

    @Mapping(source = "prestataire.name", target = "prestataireName")
    OnboardingPendingDto toPendingDto(Onboarding onboarding);
}
