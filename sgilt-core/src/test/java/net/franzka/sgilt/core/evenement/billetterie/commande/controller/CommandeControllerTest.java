package net.franzka.sgilt.core.evenement.billetterie.commande.controller;

import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CreateCommandeRequest;
import net.franzka.sgilt.core.evenement.billetterie.commande.service.CommandeService;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.billetterie.service.BilletterieService;
import net.franzka.sgilt.core.evenement.page.service.PageEvenementService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandeControllerTest {

    @Mock
    private PageEvenementService pageEvenementService;

    @Mock
    private BilletterieService billetterieService;

    @Mock
    private CommandeService commandeService;

    @InjectMocks
    private CommandeController controller;

    // -------------------------------------------------------------------------
    // createCommande
    // -------------------------------------------------------------------------

    @Nested
    class CreateCommande {

        @Test
        void givenSlugOfOpenBilletterie_whenCreateCommande_thenReturns201WithCommande() {
            UUID evenementId = UUID.randomUUID();
            CreateCommandeRequest request =
                    new CreateCommandeRequest("slug", 2, "Jeanne", "Martin", "jeanne@example.com");
            Billetterie billetterie = Billetterie.builder().build();
            CommandeDto dto = mock(CommandeDto.class);
            when(pageEvenementService.getEvenementIdBySlug("slug")).thenReturn(evenementId);
            when(billetterieService.lockOpenBilletterie(evenementId)).thenReturn(billetterie);
            when(commandeService.createCommande(billetterie, request)).thenReturn(dto);

            ResponseEntity<CommandeDto> result = controller.createCommande(request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(result.getBody()).isSameAs(dto);
        }
    }
}
