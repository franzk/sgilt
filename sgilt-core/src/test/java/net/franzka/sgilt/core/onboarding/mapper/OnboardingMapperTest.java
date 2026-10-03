package net.franzka.sgilt.core.onboarding.mapper;

import net.franzka.sgilt.core.evenement.dto.DemandeInitieeDto;
import net.franzka.sgilt.core.evenement.dto.EvenementDto;
import net.franzka.sgilt.core.evenement.dto.RubriqueDto;
import net.franzka.sgilt.core.onboarding.domain.Onboarding;
import net.franzka.sgilt.core.onboarding.domain.OnboardingState;
import net.franzka.sgilt.core.onboarding.dto.InitOnboardingRequest;
import net.franzka.sgilt.core.onboarding.dto.OnboardingPendingDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OnboardingMapperTest {

    private final OnboardingMapper mapper = new OnboardingMapperImpl();

    @Nested
    class ToPendingDto {

        @Test
        void givenOnboardingAndItsEvent_whenToPendingDto_thenMapsTheSessionAndTheEvent() {
            UUID id = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.of(2027, 1, 1, 10, 0);
            LocalDateTime expiresAt = LocalDateTime.of(2027, 1, 8, 10, 0);
            LocalDate date = LocalDate.of(2027, 6, 12);
            Onboarding onboarding = Onboarding.builder()
                    .id(id).email("client@sgilt.fr").state(OnboardingState.OPEN)
                    .createdAt(createdAt).expiresAt(expiresAt).build();
            DemandeInitieeDto demande = new DemandeInitieeDto(UUID.randomUUID(), null);
            EvenementDto evenement = new EvenementDto("mariage", null, null, null, date, "Lyon", null, null,
                    List.of(new RubriqueDto("lieu", List.of(demande)),
                            new RubriqueDto("musique-animation", List.of(demande, demande)),
                            new RubriqueDto("decoration", null)));
            InitOnboardingRequest content = new InitOnboardingRequest("Jeanne", "Martin", "client@sgilt.fr", null, evenement);

            OnboardingPendingDto dto = mapper.toPendingDto(onboarding, content);

            assertThat(dto).isEqualTo(new OnboardingPendingDto(
                    id, "client@sgilt.fr", "mariage", date, 3, OnboardingState.OPEN, createdAt, expiresAt));
        }
    }
}
