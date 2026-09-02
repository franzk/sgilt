package net.franzka.sgilt.core.ficheia.service;

import net.franzka.sgilt.core.ficheia.domain.FicheIaAction;
import net.franzka.sgilt.core.ficheia.domain.FicheIaSection;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationDto;
import net.franzka.sgilt.core.ficheia.exception.FicheIaInvalidInstructionException;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.dto.IdentityDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireDetailDto;
import net.franzka.sgilt.core.prestataire.domain.PrestataireStatus;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FicheIaApplyServiceTest {

    @Mock
    private GenerationIaService generationIaService;

    @Mock
    private PrestataireService prestataireService;

    @InjectMocks
    private FicheIaApplyService ficheIaApplyService;

    private final Utilisateur utilisateur = Utilisateur.builder().id(UUID.randomUUID()).email("pro@sgilt.fr").build();
    private final Prestataire prestataire = Prestataire.builder().id(UUID.randomUUID()).build();
    private final PrestataireDetailDto dto = dummyDetailDto();

    private final FicheIaGenerationDto generated = new FicheIaGenerationDto(
            "Courte description", "Baseline", List.of("Offre 1"),
            new IdentityDto("Citation", "Bio"), List.of(), List.of(), List.of(), "Budget");

    // -------------------------------------------------------------------------
    // validate
    // -------------------------------------------------------------------------

    @Nested
    class Validate {

        @Test
        void givenSectionProvidedWithEcraserTout_whenApply_thenThrowsInvalidInstruction() {
            assertThatThrownBy(() -> ficheIaApplyService.apply(utilisateur, FicheIaSection.BASELINE, FicheIaAction.ECRASER_TOUT))
                    .isInstanceOf(FicheIaInvalidInstructionException.class);
        }

        @Test
        void givenNoSectionWithNonEcraserToutAction_whenApply_thenThrowsInvalidInstruction() {
            assertThatThrownBy(() -> ficheIaApplyService.apply(utilisateur, null, FicheIaAction.REMPLACER))
                    .isInstanceOf(FicheIaInvalidInstructionException.class);
        }

        @Test
        void givenAjouterOnNonListSection_whenApply_thenThrowsInvalidInstruction() {
            assertThatThrownBy(() -> ficheIaApplyService.apply(utilisateur, FicheIaSection.BASELINE, FicheIaAction.AJOUTER))
                    .isInstanceOf(FicheIaInvalidInstructionException.class);
        }
    }

    // -------------------------------------------------------------------------
    // apply - ECRASER_TOUT
    // -------------------------------------------------------------------------

    @Nested
    class ApplyEcraserTout {

        @Test
        void givenValidInstruction_whenApplyEcraserTout_thenReplacesAllEightSectionsAndReturnsFinalDto() {
            when(prestataireService.getEntityByUtilisateurOwner(utilisateur)).thenReturn(prestataire);
            when(generationIaService.getLastGeneration(prestataire)).thenReturn(generated);
            when(prestataireService.replaceFaq(prestataire, generated.faq())).thenReturn(dto);

            PrestataireDetailDto result = ficheIaApplyService.apply(utilisateur, null, FicheIaAction.ECRASER_TOUT);

            assertThat(result).isEqualTo(dto);
            verify(prestataireService).replaceBaseline(prestataire, generated.baseline());
            verify(prestataireService).replaceShortDescription(prestataire, generated.shortDescription());
            verify(prestataireService).replaceIdentity(prestataire, generated.identity());
            verify(prestataireService).replaceBudget(prestataire, generated.budget());
            verify(prestataireService).replaceOfferings(prestataire, generated.offerings());
            verify(prestataireService).replaceTestimonials(prestataire, generated.testimonials());
            verify(prestataireService).replaceDetails(prestataire, generated.details());
            verify(prestataireService).replaceFaq(prestataire, generated.faq());
        }
    }

    // -------------------------------------------------------------------------
    // apply - une section, REMPLACER
    // -------------------------------------------------------------------------

    @Nested
    class ApplyOneRemplacer {

        @Test
        void givenBaseline_whenApplyRemplacer_thenReplacesBaseline() {
            stubGeneration();
            when(prestataireService.replaceBaseline(prestataire, generated.baseline())).thenReturn(dto);

            assertThat(ficheIaApplyService.apply(utilisateur, FicheIaSection.BASELINE, FicheIaAction.REMPLACER)).isEqualTo(dto);
        }

        @Test
        void givenShortDescription_whenApplyRemplacer_thenReplacesShortDescription() {
            stubGeneration();
            when(prestataireService.replaceShortDescription(prestataire, generated.shortDescription())).thenReturn(dto);

            assertThat(ficheIaApplyService.apply(utilisateur, FicheIaSection.SHORT_DESCRIPTION, FicheIaAction.REMPLACER)).isEqualTo(dto);
        }

        @Test
        void givenIdentity_whenApplyRemplacer_thenReplacesIdentity() {
            stubGeneration();
            when(prestataireService.replaceIdentity(prestataire, generated.identity())).thenReturn(dto);

            assertThat(ficheIaApplyService.apply(utilisateur, FicheIaSection.IDENTITY, FicheIaAction.REMPLACER)).isEqualTo(dto);
        }

        @Test
        void givenBudget_whenApplyRemplacer_thenReplacesBudget() {
            stubGeneration();
            when(prestataireService.replaceBudget(prestataire, generated.budget())).thenReturn(dto);

            assertThat(ficheIaApplyService.apply(utilisateur, FicheIaSection.BUDGET, FicheIaAction.REMPLACER)).isEqualTo(dto);
        }

        @Test
        void givenOfferings_whenApplyRemplacer_thenReplacesOfferings() {
            stubGeneration();
            when(prestataireService.replaceOfferings(prestataire, generated.offerings())).thenReturn(dto);

            assertThat(ficheIaApplyService.apply(utilisateur, FicheIaSection.OFFERINGS, FicheIaAction.REMPLACER)).isEqualTo(dto);
            verify(prestataireService, never()).appendOfferings(any(), any());
        }

        @Test
        void givenTestimonials_whenApplyRemplacer_thenReplacesTestimonials() {
            stubGeneration();
            when(prestataireService.replaceTestimonials(prestataire, generated.testimonials())).thenReturn(dto);

            assertThat(ficheIaApplyService.apply(utilisateur, FicheIaSection.TESTIMONIALS, FicheIaAction.REMPLACER)).isEqualTo(dto);
        }

        @Test
        void givenDetails_whenApplyRemplacer_thenReplacesDetails() {
            stubGeneration();
            when(prestataireService.replaceDetails(prestataire, generated.details())).thenReturn(dto);

            assertThat(ficheIaApplyService.apply(utilisateur, FicheIaSection.DETAILS, FicheIaAction.REMPLACER)).isEqualTo(dto);
        }

        @Test
        void givenFaq_whenApplyRemplacer_thenReplacesFaq() {
            stubGeneration();
            when(prestataireService.replaceFaq(prestataire, generated.faq())).thenReturn(dto);

            assertThat(ficheIaApplyService.apply(utilisateur, FicheIaSection.FAQ, FicheIaAction.REMPLACER)).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // apply - une section liste, AJOUTER
    // -------------------------------------------------------------------------

    @Nested
    class ApplyOneAjouter {

        @Test
        void givenOfferings_whenApplyAjouter_thenAppendsOfferings() {
            stubGeneration();
            when(prestataireService.appendOfferings(prestataire, generated.offerings())).thenReturn(dto);

            assertThat(ficheIaApplyService.apply(utilisateur, FicheIaSection.OFFERINGS, FicheIaAction.AJOUTER)).isEqualTo(dto);
            verify(prestataireService, never()).replaceOfferings(any(), any());
        }

        @Test
        void givenTestimonials_whenApplyAjouter_thenAppendsTestimonials() {
            stubGeneration();
            when(prestataireService.appendTestimonials(prestataire, generated.testimonials())).thenReturn(dto);

            assertThat(ficheIaApplyService.apply(utilisateur, FicheIaSection.TESTIMONIALS, FicheIaAction.AJOUTER)).isEqualTo(dto);
        }

        @Test
        void givenDetails_whenApplyAjouter_thenAppendsDetails() {
            stubGeneration();
            when(prestataireService.appendDetails(prestataire, generated.details())).thenReturn(dto);

            assertThat(ficheIaApplyService.apply(utilisateur, FicheIaSection.DETAILS, FicheIaAction.AJOUTER)).isEqualTo(dto);
        }

        @Test
        void givenFaq_whenApplyAjouter_thenAppendsFaq() {
            stubGeneration();
            when(prestataireService.appendFaq(prestataire, generated.faq())).thenReturn(dto);

            assertThat(ficheIaApplyService.apply(utilisateur, FicheIaSection.FAQ, FicheIaAction.AJOUTER)).isEqualTo(dto);
        }
    }

    private void stubGeneration() {
        when(prestataireService.getEntityByUtilisateurOwner(utilisateur)).thenReturn(prestataire);
        when(generationIaService.getLastGeneration(prestataire)).thenReturn(generated);
    }

    private static PrestataireDetailDto dummyDetailDto() {
        return new PrestataireDetailDto(
                UUID.randomUUID().toString(), "Jean Photographe", "photographe-jean", null, null, null, null, null, "photo",
                List.of(), List.of(), List.of(), List.of(), null, null, List.of(), List.of(), List.of(),
                PrestataireStatus.PUBLISHED
        );
    }
}
