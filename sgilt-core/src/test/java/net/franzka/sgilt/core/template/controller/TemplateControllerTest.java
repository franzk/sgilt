package net.franzka.sgilt.core.template.controller;

import net.franzka.sgilt.core.evenement.dto.RubriqueDto;
import net.franzka.sgilt.core.template.dto.RubriqueContenuDto;
import net.franzka.sgilt.core.template.service.TemplateService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateControllerTest {

    @Mock
    private TemplateService templateService;

    @InjectMocks
    private TemplateController controller;

    // -------------------------------------------------------------------------
    // getNewEventRubriques
    // -------------------------------------------------------------------------

    @Nested
    class GetNewEventRubriques {

        @Test
        void givenType_whenGetNewEventRubriques_thenReturnsTheTemplateRubriques() {
            List<RubriqueDto> rubriques = List.of(
                    new RubriqueDto("lieu", List.of()), new RubriqueDto("restauration", List.of()));
            when(templateService.getNewEventRubriques("mariage")).thenReturn(rubriques);

            assertThat(controller.getNewEventRubriques("mariage").getBody()).isEqualTo(rubriques);
        }
    }

    // -------------------------------------------------------------------------
    // getRubriqueContenu
    // -------------------------------------------------------------------------

    @Nested
    class GetRubriqueContenu {

        @Test
        void givenTypeAndRubrique_whenGetRubriqueContenu_thenReturnsWhatTheRubriqueGroups() {
            RubriqueContenuDto contenu = new RubriqueContenuDto(List.of("musique"), List.of("animation"));
            when(templateService.getRubriqueContenu("mariage", "musique-animation")).thenReturn(contenu);

            assertThat(controller.getRubriqueContenu("mariage", "musique-animation").getBody()).isEqualTo(contenu);
        }
    }
}
