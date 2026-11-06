package net.franzka.sgilt.core.evenement.billetterie.commande.controller;

import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeSummaryDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CreateCommandeRequest;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.EBilletDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.mapper.CommandeMapper;
import net.franzka.sgilt.core.evenement.billetterie.commande.service.CommandeService;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.billetterie.service.BilletterieService;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.dto.EvenementInfosDto;
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

    @Mock
    private CommandeMapper commandeMapper;

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

    // -------------------------------------------------------------------------
    // getCommande
    // -------------------------------------------------------------------------

    @Nested
    class GetCommande {

        @Test
        void givenExistingCommande_whenGetCommande_thenReturns200WithCommande() {
            UUID id = UUID.randomUUID();
            CommandeSummaryDto dto = mock(CommandeSummaryDto.class);
            when(commandeService.getCommande(id)).thenReturn(dto);

            ResponseEntity<CommandeSummaryDto> result = controller.getCommande(id);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isSameAs(dto);
        }
    }

    // -------------------------------------------------------------------------
    // getEBillet
    // -------------------------------------------------------------------------

    @Nested
    class GetEBillet {

        @Test
        void givenPaidCommande_whenGetEBillet_thenReturns200WithEBilletAndInfosOfItsEvent() {
            UUID id = UUID.randomUUID();
            Evenement evenement = Evenement.builder().build();
            Commande commande = Commande.builder().billetterie(Billetterie.builder().evenement(evenement).build()).build();
            EvenementInfosDto evenementInfos = mock(EvenementInfosDto.class);
            EBilletDto dto = mock(EBilletDto.class);
            when(commandeService.getPaidCommande(id)).thenReturn(commande);
            when(pageEvenementService.getEvenementInfos(evenement)).thenReturn(evenementInfos);
            when(pageEvenementService.isOnline(evenement)).thenReturn(true);
            when(commandeMapper.toEBilletDto(commande, evenementInfos, true)).thenReturn(dto);

            ResponseEntity<EBilletDto> result = controller.getEBillet(id);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isSameAs(dto);
        }
    }
}
