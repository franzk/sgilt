package net.franzka.sgilt.core.ficheia.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.franzka.sgilt.core.ficheia.domain.GenerationIa;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationDto;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationResultDto;
import net.franzka.sgilt.core.ficheia.exception.FicheIaNoResultAvailableException;
import net.franzka.sgilt.core.ficheia.repository.GenerationIaRepository;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.dto.IdentityDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerationIaServiceTest {

    @Mock
    private GenerationIaRepository generationIaRepository;

    @InjectMocks
    private GenerationIaService generationIaService;

    private final Prestataire prestataire = Prestataire.builder().id(UUID.randomUUID()).build();

    private final FicheIaGenerationDto dto = new FicheIaGenerationDto(
            "Courte description", "Baseline", List.of("Offre 1"),
            new IdentityDto("Citation", "Bio"), List.of(), List.of(), List.of(), "Budget");

    // -------------------------------------------------------------------------
    // getOrCreateFor
    // -------------------------------------------------------------------------

    @Nested
    class GetOrCreateFor {

        @Test
        void givenExistingLine_whenGetOrCreateFor_thenReturnsExistingLine() {
            GenerationIa existing = GenerationIa.createFor(prestataire);
            when(generationIaRepository.findByPrestataire(prestataire)).thenReturn(Optional.of(existing));

            GenerationIa result = generationIaService.getOrCreateFor(prestataire);

            assertThat(result).isSameAs(existing);
        }

        @Test
        void givenNoLineYet_whenGetOrCreateFor_thenReturnsNewUnpersistedLine() {
            when(generationIaRepository.findByPrestataire(prestataire)).thenReturn(Optional.empty());

            GenerationIa result = generationIaService.getOrCreateFor(prestataire);

            assertThat(result.getPrestataire()).isEqualTo(prestataire);
            assertThat(result.getTriesLeft()).isEqualTo(GenerationIa.defaultTriesLeft());
            verify(generationIaRepository, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // hasTriesLeft
    // -------------------------------------------------------------------------

    @Nested
    class HasTriesLeft {

        @Test
        void givenTriesRemaining_whenHasTriesLeft_thenReturnsTrue() {
            GenerationIa generationIa = GenerationIa.builder().triesLeft(1).build();

            assertThat(generationIaService.hasTriesLeft(generationIa)).isTrue();
        }

        @Test
        void givenNoTriesRemaining_whenHasTriesLeft_thenReturnsFalse() {
            GenerationIa generationIa = GenerationIa.builder().triesLeft(0).build();

            assertThat(generationIaService.hasTriesLeft(generationIa)).isFalse();
        }
    }

    // -------------------------------------------------------------------------
    // recordSuccess
    // -------------------------------------------------------------------------

    @Nested
    class RecordSuccess {

        @Test
        void givenSuccessfulGeneration_whenRecordSuccess_thenSavesJsonUrlAndDecrementsQuota() {
            GenerationIa generationIa = GenerationIa.builder().triesLeft(3).build();
            when(generationIaRepository.save(generationIa)).thenReturn(generationIa);

            GenerationIa result = generationIaService.recordSuccess(generationIa, dto, "https://exemple.fr");

            assertThat(result.getSourceUrl()).isEqualTo("https://exemple.fr");
            assertThat(result.getTriesLeft()).isEqualTo(2);
            assertThat(result.getLastGenerationDateTime()).isNotNull();
            assertThat(result.getLastGeneration()).contains("Baseline");
        }
    }

    // -------------------------------------------------------------------------
    // recordFailedAttempt
    // -------------------------------------------------------------------------

    @Nested
    class RecordFailedAttempt {

        @Test
        void givenFailedAttempt_whenRecordFailedAttempt_thenDecrementsQuotaWithoutTouchingLastResult() {
            GenerationIa generationIa = GenerationIa.builder()
                    .triesLeft(2).lastGeneration("{}").sourceUrl("https://precedent.fr").build();
            when(generationIaRepository.save(generationIa)).thenReturn(generationIa);

            GenerationIa result = generationIaService.recordFailedAttempt(generationIa);

            assertThat(result.getTriesLeft()).isEqualTo(1);
            assertThat(result.getLastGeneration()).isEqualTo("{}");
            assertThat(result.getSourceUrl()).isEqualTo("https://precedent.fr");
        }
    }

    // -------------------------------------------------------------------------
    // getLastGeneration
    // -------------------------------------------------------------------------

    @Nested
    class GetLastGeneration {

        @Test
        void givenExploitableLastGeneration_whenGetLastGeneration_thenReturnsDeserializedDto() {
            GenerationIa generationIa = GenerationIa.builder()
                    .triesLeft(2).lastGeneration(toJson(dto)).build();
            when(generationIaRepository.findByPrestataire(prestataire)).thenReturn(Optional.of(generationIa));

            FicheIaGenerationDto result = generationIaService.getLastGeneration(prestataire);

            assertThat(result).isEqualTo(dto);
        }

        @Test
        void givenNoLineForPrestataire_whenGetLastGeneration_thenThrowsNoResultAvailable() {
            when(generationIaRepository.findByPrestataire(prestataire)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> generationIaService.getLastGeneration(prestataire))
                    .isInstanceOf(FicheIaNoResultAvailableException.class);
        }

        @Test
        void givenLineWithoutAnyGenerationYet_whenGetLastGeneration_thenThrowsNoResultAvailable() {
            GenerationIa generationIa = GenerationIa.builder().triesLeft(3).lastGeneration(null).build();
            when(generationIaRepository.findByPrestataire(prestataire)).thenReturn(Optional.of(generationIa));

            assertThatThrownBy(() -> generationIaService.getLastGeneration(prestataire))
                    .isInstanceOf(FicheIaNoResultAvailableException.class);
        }

        @Test
        void givenCorruptedStoredJson_whenGetLastGeneration_thenThrowsIllegalStateException() {
            GenerationIa generationIa = GenerationIa.builder().triesLeft(2).lastGeneration("{invalide").build();
            when(generationIaRepository.findByPrestataire(prestataire)).thenReturn(Optional.of(generationIa));

            assertThatThrownBy(() -> generationIaService.getLastGeneration(prestataire))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    // -------------------------------------------------------------------------
    // getState
    // -------------------------------------------------------------------------

    @Nested
    class GetState {

        @Test
        void givenLineWithLastGeneration_whenGetState_thenReturnsResultTriesAndTimestamp() {
            LocalDateTime timestamp = LocalDateTime.now();
            GenerationIa generationIa = GenerationIa.builder()
                    .triesLeft(1).lastGeneration(toJson(dto)).lastGenerationDateTime(timestamp).build();
            when(generationIaRepository.findByPrestataire(prestataire)).thenReturn(Optional.of(generationIa));

            FicheIaGenerationResultDto result = generationIaService.getState(prestataire);

            assertThat(result).isEqualTo(new FicheIaGenerationResultDto(dto, 1, timestamp));
        }

        @Test
        void givenLineWithoutLastGeneration_whenGetState_thenReturnsNullResult() {
            GenerationIa generationIa = GenerationIa.builder().triesLeft(3).lastGeneration(null).build();
            when(generationIaRepository.findByPrestataire(prestataire)).thenReturn(Optional.of(generationIa));

            FicheIaGenerationResultDto result = generationIaService.getState(prestataire);

            assertThat(result).isEqualTo(new FicheIaGenerationResultDto(null, 3, null));
        }

        @Test
        void givenNoLineForPrestataire_whenGetState_thenReturnsDefaultTriesLeftAndNullFields() {
            when(generationIaRepository.findByPrestataire(prestataire)).thenReturn(Optional.empty());

            FicheIaGenerationResultDto result = generationIaService.getState(prestataire);

            assertThat(result).isEqualTo(new FicheIaGenerationResultDto(null, GenerationIa.defaultTriesLeft(), null));
        }
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String toJson(FicheIaGenerationDto dto) {
        try {
            return OBJECT_MAPPER.writeValueAsString(dto);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
