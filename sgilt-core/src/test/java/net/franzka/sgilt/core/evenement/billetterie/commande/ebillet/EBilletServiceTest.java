package net.franzka.sgilt.core.evenement.billetterie.commande.ebillet;

import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.EBilletDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.CommandeNotPaidException;
import net.franzka.sgilt.core.evenement.billetterie.commande.mapper.CommandeMapper;
import net.franzka.sgilt.core.evenement.billetterie.commande.service.CommandeService;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.dto.EvenementInfosDto;
import net.franzka.sgilt.core.evenement.page.service.PageEvenementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EBilletServiceTest {

    private static final UUID COMMANDE_ID = UUID.randomUUID();
    private static final String CODE = "q3Zx9kP2mL7vT1nR8wY4sA";

    @Mock
    private CommandeService commandeService;

    @Mock
    private PageEvenementService pageEvenementService;

    @Mock
    private CommandeMapper commandeMapper;

    @Mock
    private EBilletPdfService eBilletPdfService;

    @InjectMocks
    private EBilletService service;

    // -------------------------------------------------------------------------
    // getEBillet
    // -------------------------------------------------------------------------

    @Nested
    class GetEBillet {

        @Test
        void givenPaidCommande_whenGetEBillet_thenReturnsEBilletWithInfosOfItsEventAndItsQr() {
            EBilletDto eBillet = givenPaidCommandeMappedToEBillet();

            assertThat(service.getEBillet(COMMANDE_ID)).isSameAs(eBillet);
        }

        @Test
        void givenUnpaidCommande_whenGetEBillet_thenThrowsCommandeNotPaid() {
            when(commandeService.getPaidCommande(COMMANDE_ID)).thenThrow(new CommandeNotPaidException(COMMANDE_ID));

            assertThatThrownBy(() -> service.getEBillet(COMMANDE_ID)).isInstanceOf(CommandeNotPaidException.class);
            verifyNoInteractions(commandeMapper);
        }
    }

    // -------------------------------------------------------------------------
    // getEBilletPdf
    // -------------------------------------------------------------------------

    @Nested
    class GetEBilletPdf {

        @Test
        void givenPaidCommande_whenGetEBilletPdf_thenReturnsPdfOfItsEBilletWithFileName() {
            EBilletDto eBillet = givenPaidCommandeMappedToEBillet();
            byte[] content = {1, 2, 3};
            when(eBilletPdfService.generate(eBillet)).thenReturn(content);

            EBilletPdf pdf = service.getEBilletPdf(COMMANDE_ID);

            assertThat(pdf.content()).isSameAs(content);
            assertThat(pdf.fileName()).isEqualTo("e-billet.pdf");
        }

        @Test
        void givenUnpaidCommande_whenGetEBilletPdf_thenThrowsCommandeNotPaidWithoutGeneratingPdf() {
            when(commandeService.getPaidCommande(COMMANDE_ID)).thenThrow(new CommandeNotPaidException(COMMANDE_ID));

            assertThatThrownBy(() -> service.getEBilletPdf(COMMANDE_ID)).isInstanceOf(CommandeNotPaidException.class);
            verifyNoInteractions(eBilletPdfService);
        }
    }

    // commande payée d'un événement en ligne, mappée vers l'e-billet retourné
    private EBilletDto givenPaidCommandeMappedToEBillet() {
        Evenement evenement = Evenement.builder().build();
        Commande commande = Commande.builder()
                .billetterie(Billetterie.builder().evenement(evenement).build()).code(CODE).build();
        EvenementInfosDto evenementInfos = mock(EvenementInfosDto.class);
        EBilletDto eBillet = mock(EBilletDto.class);
        when(commandeService.getPaidCommande(COMMANDE_ID)).thenReturn(commande);
        when(pageEvenementService.getEvenementInfos(evenement)).thenReturn(evenementInfos);
        when(pageEvenementService.isOnline(evenement)).thenReturn(true);
        when(commandeMapper.toEBilletDto(commande, evenementInfos, true, QrCodeUtils.pngDataUri("SGILT:C:" + CODE)))
                .thenReturn(eBillet);
        return eBillet;
    }
}
