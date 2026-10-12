package net.franzka.sgilt.core.template.service;

import net.franzka.sgilt.core.evenement.dto.DemandeInitieeDto;
import net.franzka.sgilt.core.evenement.dto.RubriqueDto;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.service.CategorieService;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.template.domain.Template;
import net.franzka.sgilt.core.template.domain.TemplateDocument;
import net.franzka.sgilt.core.template.domain.TemplateRubrique;
import net.franzka.sgilt.core.template.repository.TemplateDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TemplateServiceTest {

    @Mock
    private TemplateDocumentRepository templateDocumentRepository;

    @Mock
    private CategorieService categorieService;

    @Mock
    private PrestataireService prestataireService;

    @InjectMocks
    private TemplateService templateService;

    // Template mariage : Musique entière + Services/Animation dans « musique-animation »,
    // Services/Décoration isolée, Hébergement vide.
    @BeforeEach
    void givenMariageTemplate() {
        Template mariage = new Template("mariage", List.of(
                new TemplateRubrique("lieu", List.of("lieu"), List.of()),
                new TemplateRubrique("musique-animation", List.of("musique"), List.of("animation")),
                new TemplateRubrique("decoration", List.of(), List.of("decoration")),
                new TemplateRubrique("hebergement", List.of(), List.of())));
        when(templateDocumentRepository.findById(TemplateDocument.SINGLE_ID))
                .thenReturn(Optional.of(TemplateDocument.builder().id(TemplateDocument.SINGLE_ID)
                        .content(List.of(mariage)).build()));
    }

    // -------------------------------------------------------------------------
    // getNewEventRubriques
    // -------------------------------------------------------------------------

    @Nested
    class GetNewEventRubriques {

        @Test
        void givenTypeWithTemplate_whenGetNewEventRubriques_thenReturnsEmptyRubriquesInOrder() {
            assertThat(templateService.getNewEventRubriques("mariage")).containsExactly(
                    new RubriqueDto("lieu", List.of()),
                    new RubriqueDto("musique-animation", List.of()),
                    new RubriqueDto("decoration", List.of()),
                    new RubriqueDto("hebergement", List.of()));
        }

        @Test
        void givenTypeWithoutTemplate_whenGetNewEventRubriques_thenReturnsEmpty() {
            assertThat(templateService.getNewEventRubriques("anniversaire")).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // getEventRubriquesFromDemande
    // -------------------------------------------------------------------------

    @Nested
    class GetEventRubriquesFromDemande {

        private final DemandeInitieeDto demande = new DemandeInitieeDto(UUID.randomUUID(), "Bonjour");

        // Le prestataire de la demande a la sous-catégorie donnée.
        private void givenPrestataireIn(String subcatKey) {
            when(prestataireService.getById(demande.prestataireId()))
                    .thenReturn(Prestataire.builder().id(demande.prestataireId()).subcatKey(subcatKey).build());
        }

        @Test
        void givenSubcategoryInATemplateRubrique_whenGetEventRubriquesFromDemande_thenPlacesItThereAmongAllRubriques() {
            givenPrestataireIn("dj");
            when(categorieService.getCategoryKeyOf("dj")).thenReturn("musique");

            assertThat(templateService.getEventRubriquesFromDemande("mariage", demande)).containsExactly(
                    new RubriqueDto("lieu", List.of()),
                    new RubriqueDto("musique-animation", List.of(demande)),
                    new RubriqueDto("decoration", List.of()),
                    new RubriqueDto("hebergement", List.of()));
        }

        @Test
        void givenIsolatedSubcategory_whenGetEventRubriquesFromDemande_thenPlacesItInTheRubriqueOfTheSubcategory() {
            givenPrestataireIn("decoration");
            when(categorieService.getCategoryKeyOf("decoration")).thenReturn("services");

            assertThat(templateService.getEventRubriquesFromDemande("mariage", demande)).containsExactly(
                    new RubriqueDto("lieu", List.of()),
                    new RubriqueDto("musique-animation", List.of()),
                    new RubriqueDto("decoration", List.of(demande)),
                    new RubriqueDto("hebergement", List.of()));
        }

        @Test
        void givenSubcategoryInNoRubrique_whenGetEventRubriquesFromDemande_thenAddsAutreAtTheEndWithIt() {
            givenPrestataireIn("video");
            when(categorieService.getCategoryKeyOf("video")).thenReturn("services");

            List<RubriqueDto> rubriques = templateService.getEventRubriquesFromDemande("mariage", demande);

            assertThat(rubriques).hasSize(5);
            assertThat(rubriques.getLast()).isEqualTo(new RubriqueDto(TemplateService.RUBRIQUE_AUTRE, List.of(demande)));
        }

        @Test
        void givenTypeWithoutTemplate_whenGetEventRubriquesFromDemande_thenOnlyTheCatalogueRubriqueWithTheDemande() {
            givenPrestataireIn("dj");
            when(categorieService.getCategoryKeyOf("dj")).thenReturn("musique");

            assertThat(templateService.getEventRubriquesFromDemande("anniversaire", demande))
                    .containsExactly(new RubriqueDto("musique-animation", List.of(demande)));
        }

        @Test
        void givenTypeWithoutTemplateAndSubcategoryInNoTemplate_whenGetEventRubriquesFromDemande_thenOnlyAutreWithTheDemande() {
            givenPrestataireIn("video");
            when(categorieService.getCategoryKeyOf("video")).thenReturn("services");

            assertThat(templateService.getEventRubriquesFromDemande("anniversaire", demande))
                    .containsExactly(new RubriqueDto(TemplateService.RUBRIQUE_AUTRE, List.of(demande)));
        }
    }

    // -------------------------------------------------------------------------
    // rubriqueOf
    // -------------------------------------------------------------------------

    @Nested
    class RubriqueOf {

        // Deux templates : mariage (BeforeEach) et anniversaire, sans rubrique de musique mais avec
        // une rubrique « photo-video » qui regroupe la vidéo.
        @BeforeEach
        void givenMariageAndAnniversaireTemplates() {
            Template anniversaire = new Template("anniversaire", List.of(
                    new TemplateRubrique("lieu", List.of("lieu"), List.of()),
                    new TemplateRubrique("photo-video", List.of(), List.of("video"))));
            Template mariage = templateDocumentRepository.findById(TemplateDocument.SINGLE_ID).orElseThrow()
                    .getContent().getFirst();
            when(templateDocumentRepository.findById(TemplateDocument.SINGLE_ID))
                    .thenReturn(Optional.of(TemplateDocument.builder().id(TemplateDocument.SINGLE_ID)
                            .content(List.of(mariage, anniversaire)).build()));
        }

        @Test
        void givenSubcategoryOfARubriqueOfTheEventTemplate_whenRubriqueOf_thenReturnsThatRubrique() {
            when(categorieService.getCategoryKeyOf("decoration")).thenReturn("services");

            assertThat(templateService.rubriqueOf("mariage", "decoration")).isEqualTo("decoration");
        }

        @Test
        void givenCategoryOfARubriqueOfTheEventTemplate_whenRubriqueOf_thenReturnsThatRubrique() {
            when(categorieService.getCategoryKeyOf("dj")).thenReturn("musique");

            assertThat(templateService.rubriqueOf("mariage", "dj")).isEqualTo("musique-animation");
        }

        @Test
        void givenSubcategoryOnlyInAnotherTemplate_whenRubriqueOf_thenReturnsTheCatalogueRubrique() {
            when(categorieService.getCategoryKeyOf("dj")).thenReturn("musique");

            assertThat(templateService.rubriqueOf("anniversaire", "dj")).isEqualTo("musique-animation");
        }

        @Test
        void givenSubcategoryInNoTemplate_whenRubriqueOf_thenReturnsAutre() {
            when(categorieService.getCategoryKeyOf("photobooth")).thenReturn("services");

            assertThat(templateService.rubriqueOf("anniversaire", "photobooth")).isEqualTo(TemplateService.RUBRIQUE_AUTRE);
        }
    }

    // -------------------------------------------------------------------------
    // insertionPosition
    // -------------------------------------------------------------------------

    @Nested
    class InsertionPosition {

        @Test
        void givenRubriqueOfTheTemplate_whenInsertionPosition_thenBeforeTheFirstPresentRubriqueThatFollowsIt() {
            // mariage : lieu, musique-animation, decoration, hebergement → musique-animation avant hebergement
            assertThat(templateService.insertionPosition("mariage", List.of("lieu", "hebergement", "autre"), "musique-animation"))
                    .isEqualTo(1);
        }

        @Test
        void givenRubriqueOfTheTemplateWithNothingAfterIt_whenInsertionPosition_thenBeforeAutre() {
            assertThat(templateService.insertionPosition("mariage", List.of("lieu", "autre"), "hebergement"))
                    .isEqualTo(1);
        }

        @Test
        void givenRubriqueFromTheCatalogue_whenInsertionPosition_thenAtTheEndBeforeAutre() {
            assertThat(templateService.insertionPosition("mariage", List.of("lieu", "decoration", "autre"), "photo-video"))
                    .isEqualTo(2);
        }

        @Test
        void givenRubriqueFromTheCatalogueAndNoAutre_whenInsertionPosition_thenAtTheEnd() {
            assertThat(templateService.insertionPosition("mariage", List.of("lieu", "decoration"), "photo-video"))
                    .isEqualTo(2);
        }

        @Test
        void givenAutre_whenInsertionPosition_thenLast() {
            assertThat(templateService.insertionPosition("mariage", List.of("lieu", "decoration"), TemplateService.RUBRIQUE_AUTRE))
                    .isEqualTo(2);
        }
    }

}
