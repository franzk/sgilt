package net.franzka.sgilt.core.onboarding.mapper;

import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.domain.OnboardingState;
import net.franzka.sgilt.core.onboarding.dto.OnboardingPendingDto;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OnboardingMapperTest {

    private final OnboardingMapper mapper = new OnboardingMapperImpl();

    @Nested
    class ToPendingDto {

        @Test
        void givenOnboardingWithPrestataire_whenToPendingDto_thenMapsAllFieldsIncludingPrestataireName() {
            UUID id = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.of(2027, 1, 1, 10, 0);
            LocalDateTime expiresAt = LocalDateTime.of(2027, 1, 8, 10, 0);
            Prestataire prestataire = Prestataire.builder().name("Studio Fleur").build();
            Onboarding onboarding = Onboarding.builder()
                    .id(id).email("client@sgilt.fr").state(OnboardingState.OPEN)
                    .createdAt(createdAt).expiresAt(expiresAt).prestataire(prestataire).build();

            OnboardingPendingDto dto = mapper.toPendingDto(onboarding);

            assertThat(dto).isEqualTo(new OnboardingPendingDto(
                    id, "client@sgilt.fr", "Studio Fleur", OnboardingState.OPEN, createdAt, expiresAt));
        }
    }
}
