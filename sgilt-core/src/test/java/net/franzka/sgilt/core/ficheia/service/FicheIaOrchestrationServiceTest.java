package net.franzka.sgilt.core.ficheia.service;

import com.openai.errors.OpenAIIoException;
import com.openai.errors.OpenAIInvalidDataException;
import com.openai.errors.OpenAIRetryableException;
import net.franzka.sgilt.core.ficheia.domain.GenerationIa;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationDto;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationResultDto;
import net.franzka.sgilt.core.ficheia.exception.FicheIaEmptyResultException;
import net.franzka.sgilt.core.ficheia.exception.FicheIaGenerationFailedException;
import net.franzka.sgilt.core.ficheia.exception.FicheIaQuotaExhaustedException;
import net.franzka.sgilt.core.ficheia.exception.FicheIaTechnicalException;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.dto.IdentityDto;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FicheIaOrchestrationServiceTest {

    @Mock
    private FicheIaGenerationService ficheIaGenerationService;

    @Mock
    private GenerationIaService generationIaService;

    @Mock
    private PrestataireService prestataireService;

    @InjectMocks
    private FicheIaOrchestrationService ficheIaOrchestrationService;

    private final Utilisateur utilisateur = Utilisateur.builder().id(UUID.randomUUID()).email("pro@sgilt.fr").build();
    private final Prestataire prestataire = Prestataire.builder().id(UUID.randomUUID()).build();
    private static final String URL = "https://exemple.fr";

    // -------------------------------------------------------------------------
    // generate
    // -------------------------------------------------------------------------

    @Nested
    class Generate {

        @Test
        void givenQuotaExhausted_whenGenerate_thenThrowsQuotaExhaustedWithoutCallingGenerationService() {
            GenerationIa generationIa = GenerationIa.builder().triesLeft(0).build();
            when(prestataireService.getEntityByUtilisateurOwner(utilisateur)).thenReturn(prestataire);
            when(generationIaService.getOrCreateFor(prestataire)).thenReturn(generationIa);
            when(generationIaService.hasTriesLeft(generationIa)).thenReturn(false);

            assertThatThrownBy(() -> ficheIaOrchestrationService.generate(utilisateur, URL))
                    .isInstanceOf(FicheIaQuotaExhaustedException.class);
            verify(ficheIaGenerationService, never()).generate(any());
        }

        @Test
        void givenTechnicalFailure_whenGenerate_thenThrowsTechnicalExceptionWithoutConsumingQuota() {
            GenerationIa generationIa = GenerationIa.builder().triesLeft(3).build();
            when(prestataireService.getEntityByUtilisateurOwner(utilisateur)).thenReturn(prestataire);
            when(generationIaService.getOrCreateFor(prestataire)).thenReturn(generationIa);
            when(generationIaService.hasTriesLeft(generationIa)).thenReturn(true);
            when(ficheIaGenerationService.generate(URL)).thenThrow(new OpenAIIoException("panne réseau"));

            assertThatThrownBy(() -> ficheIaOrchestrationService.generate(utilisateur, URL))
                    .isInstanceOf(FicheIaTechnicalException.class);
            verify(generationIaService, never()).recordFailedAttempt(any());
            verify(generationIaService, never()).recordSuccess(any(), any(), any());
        }

        @Test
        void givenRetryableFailure_whenGenerate_thenThrowsTechnicalException() {
            GenerationIa generationIa = GenerationIa.builder().triesLeft(3).build();
            when(prestataireService.getEntityByUtilisateurOwner(utilisateur)).thenReturn(prestataire);
            when(generationIaService.getOrCreateFor(prestataire)).thenReturn(generationIa);
            when(generationIaService.hasTriesLeft(generationIa)).thenReturn(true);
            when(ficheIaGenerationService.generate(URL)).thenThrow(new OpenAIRetryableException("timeout"));

            assertThatThrownBy(() -> ficheIaOrchestrationService.generate(utilisateur, URL))
                    .isInstanceOf(FicheIaTechnicalException.class);
        }

        @Test
        void givenEmptyResult_whenGenerate_thenRecordsFailedAttemptAndThrowsGenerationFailed() {
            GenerationIa generationIa = GenerationIa.builder().triesLeft(3).build();
            when(prestataireService.getEntityByUtilisateurOwner(utilisateur)).thenReturn(prestataire);
            when(generationIaService.getOrCreateFor(prestataire)).thenReturn(generationIa);
            when(generationIaService.hasTriesLeft(generationIa)).thenReturn(true);
            when(ficheIaGenerationService.generate(URL)).thenThrow(new FicheIaEmptyResultException("vide"));

            assertThatThrownBy(() -> ficheIaOrchestrationService.generate(utilisateur, URL))
                    .isInstanceOf(FicheIaGenerationFailedException.class);
            verify(generationIaService).recordFailedAttempt(generationIa);
            verify(generationIaService, never()).recordSuccess(any(), any(), any());
        }

        @Test
        void givenInvalidData_whenGenerate_thenRecordsFailedAttemptAndThrowsGenerationFailed() {
            GenerationIa generationIa = GenerationIa.builder().triesLeft(3).build();
            when(prestataireService.getEntityByUtilisateurOwner(utilisateur)).thenReturn(prestataire);
            when(generationIaService.getOrCreateFor(prestataire)).thenReturn(generationIa);
            when(generationIaService.hasTriesLeft(generationIa)).thenReturn(true);
            when(ficheIaGenerationService.generate(URL)).thenThrow(new OpenAIInvalidDataException("données invalides"));

            assertThatThrownBy(() -> ficheIaOrchestrationService.generate(utilisateur, URL))
                    .isInstanceOf(FicheIaGenerationFailedException.class);
            verify(generationIaService).recordFailedAttempt(generationIa);
        }

        @Test
        void givenSuccessfulGeneration_whenGenerate_thenRecordsSuccessAndReturnsResult() {
            GenerationIa generationIa = GenerationIa.builder().triesLeft(3).build();
            FicheIaGenerationDto generated = new FicheIaGenerationDto(
                    "Courte description", "Baseline", List.of("Offre 1"),
                    new IdentityDto("Citation", "Bio"), List.of(), List.of(), List.of(), "Budget");
            LocalDateTime timestamp = LocalDateTime.now();
            GenerationIa saved = GenerationIa.builder().triesLeft(2).lastGenerationDateTime(timestamp).build();

            when(prestataireService.getEntityByUtilisateurOwner(utilisateur)).thenReturn(prestataire);
            when(generationIaService.getOrCreateFor(prestataire)).thenReturn(generationIa);
            when(generationIaService.hasTriesLeft(generationIa)).thenReturn(true);
            when(ficheIaGenerationService.generate(URL)).thenReturn(generated);
            when(generationIaService.recordSuccess(generationIa, generated, URL)).thenReturn(saved);

            FicheIaGenerationResultDto result = ficheIaOrchestrationService.generate(utilisateur, URL);

            assertThat(result).isEqualTo(new FicheIaGenerationResultDto(generated, 2, timestamp));
        }
    }

    // -------------------------------------------------------------------------
    // getState
    // -------------------------------------------------------------------------

    @Nested
    class GetState {

        @Test
        void givenUtilisateur_whenGetState_thenDelegatesToGenerationIaServiceForLinkedPrestataire() {
            FicheIaGenerationResultDto expected = new FicheIaGenerationResultDto(null, 3, null);
            when(prestataireService.getEntityByUtilisateurOwner(utilisateur)).thenReturn(prestataire);
            when(generationIaService.getState(prestataire)).thenReturn(expected);

            assertThat(ficheIaOrchestrationService.getState(utilisateur)).isEqualTo(expected);
        }
    }
}
