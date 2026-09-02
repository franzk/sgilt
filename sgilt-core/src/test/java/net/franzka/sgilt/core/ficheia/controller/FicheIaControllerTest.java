package net.franzka.sgilt.core.ficheia.controller;

import net.franzka.sgilt.core.ficheia.domain.FicheIaAction;
import net.franzka.sgilt.core.ficheia.domain.FicheIaSection;
import net.franzka.sgilt.core.ficheia.dto.FicheIaApplyRequest;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationRequest;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationResultDto;
import net.franzka.sgilt.core.ficheia.service.FicheIaApplyService;
import net.franzka.sgilt.core.ficheia.service.FicheIaOrchestrationService;
import net.franzka.sgilt.core.prestataire.dto.PrestataireDetailDto;
import net.franzka.sgilt.core.security.CurrentUserService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FicheIaControllerTest {

    @Mock
    private FicheIaOrchestrationService ficheIaOrchestrationService;

    @Mock
    private FicheIaApplyService ficheIaApplyService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private FicheIaController controller;

    private final Utilisateur utilisateur = Utilisateur.builder().id(UUID.randomUUID()).email("pro@sgilt.fr").build();

    // -------------------------------------------------------------------------
    // generate
    // -------------------------------------------------------------------------

    @Nested
    class Generate {

        @Test
        void givenUrl_whenGenerate_thenDelegatesToOrchestrationService() {
            when(currentUserService.get()).thenReturn(utilisateur);
            FicheIaGenerationRequest request = new FicheIaGenerationRequest("https://exemple.fr");
            FicheIaGenerationResultDto dto = mock(FicheIaGenerationResultDto.class);
            when(ficheIaOrchestrationService.generate(utilisateur, "https://exemple.fr")).thenReturn(dto);

            assertThat(controller.generate(request).getBody()).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // apply
    // -------------------------------------------------------------------------

    @Nested
    class Apply {

        @Test
        void givenSectionAndAction_whenApply_thenDelegatesToApplyService() {
            when(currentUserService.get()).thenReturn(utilisateur);
            FicheIaApplyRequest request = new FicheIaApplyRequest(FicheIaSection.BASELINE, FicheIaAction.REMPLACER);
            PrestataireDetailDto dto = mock(PrestataireDetailDto.class);
            when(ficheIaApplyService.apply(utilisateur, FicheIaSection.BASELINE, FicheIaAction.REMPLACER)).thenReturn(dto);

            assertThat(controller.apply(request).getBody()).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // getState
    // -------------------------------------------------------------------------

    @Nested
    class GetState {

        @Test
        void givenCurrentUser_whenGetState_thenDelegatesToOrchestrationService() {
            when(currentUserService.get()).thenReturn(utilisateur);
            FicheIaGenerationResultDto dto = mock(FicheIaGenerationResultDto.class);
            when(ficheIaOrchestrationService.getState(utilisateur)).thenReturn(dto);

            assertThat(controller.getState().getBody()).isEqualTo(dto);
        }
    }
}
