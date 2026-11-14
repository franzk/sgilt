package net.franzka.sgilt.core.evenement.billetterie.service;

import net.franzka.sgilt.core.evenement.billetterie.commande.dto.OrganizerCommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingSalesDto;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieOpenWithoutPublishedPageException;
import net.franzka.sgilt.core.evenement.billetterie.exception.CapacityBelowSoldTicketsException;
import net.franzka.sgilt.core.evenement.billetterie.commande.service.CommandeService;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingSettingsDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotFoundException;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotOpenException;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieWithSalesException;
import net.franzka.sgilt.core.evenement.billetterie.mapper.BilletterieMapper;
import net.franzka.sgilt.core.evenement.billetterie.repository.BilletterieRepository;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BilletterieServiceTest {

    private static final UUID EVENEMENT_ID = UUID.randomUUID();
    private static final TicketingSettingsDto TICKETING = new TicketingSettingsDto(BilletterieStatus.OPEN, 1200, "Tout compris", 150);

    @Mock
    private BilletterieRepository billetterieRepository;

    @Mock
    private BilletterieMapper billetterieMapper;

    @Mock
    private CommandeService commandeService;

    @InjectMocks
    private BilletterieService billetterieService;

    private final Evenement evenement = Evenement.builder().id(EVENEMENT_ID).build();

    // Événement dont le back-office a activé la billetterie.
    private final Evenement billetterieEnabledEvenement = Evenement.builder().id(EVENEMENT_ID)
            .pagePubliqueEnabled(true).billetterieEnabled(true).build();

    // Événement dont le back-office a désactivé la billetterie (la page reste active).
    private final Evenement billetterieDisabledEvenement = Evenement.builder().id(EVENEMENT_ID)
            .pagePubliqueEnabled(true).build();

    // -------------------------------------------------------------------------
    // getPublicTicketing
    // -------------------------------------------------------------------------

    @Nested
    class GetPublicTicketing {

        @Test
        void givenBilletterie_whenGetPublicTicketing_thenMapsItWithRemainingTickets() {
            Billetterie billetterie = Billetterie.builder().evenement(billetterieEnabledEvenement)
                    .status(BilletterieStatus.OPEN).unitPriceCents(1200).capacity(150).build();
            TicketingDto dto = new TicketingDto(BilletterieStatus.OPEN, 1200, "Tout compris", 42, 10);
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(billetterie));
            when(commandeService.getRemainingTickets(billetterie)).thenReturn(42);
            when(billetterieMapper.toTicketingDto(billetterie, 42)).thenReturn(dto);

            assertThat(billetterieService.getPublicTicketing(EVENEMENT_ID)).contains(dto);
        }

        @Test
        void givenNoBilletterie_whenGetPublicTicketing_thenReturnsEmpty() {
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());

            assertThat(billetterieService.getPublicTicketing(EVENEMENT_ID)).isEmpty();
        }

        @Test
        void givenBilletterieDisabled_whenGetPublicTicketing_thenReturnsEmpty() {
            Billetterie billetterie = Billetterie.builder().evenement(billetterieDisabledEvenement)
                    .status(BilletterieStatus.OPEN).build();
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(billetterie));

            assertThat(billetterieService.getPublicTicketing(EVENEMENT_ID)).isEmpty();
            verifyNoInteractions(commandeService, billetterieMapper);
        }
    }

    // -------------------------------------------------------------------------
    // lockOpenBilletterie
    // -------------------------------------------------------------------------

    @Nested
    class LockOpenBilletterie {

        @Test
        void givenOpenBilletterie_whenLockOpenBilletterie_thenReturnsItLocked() {
            Billetterie billetterie = Billetterie.builder().evenement(billetterieEnabledEvenement)
                    .status(BilletterieStatus.OPEN).build();
            when(billetterieRepository.findForUpdateByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(billetterie));

            assertThat(billetterieService.lockOpenBilletterie(EVENEMENT_ID)).isSameAs(billetterie);
        }

        @Test
        void givenNotOpenBilletterie_whenLockOpenBilletterie_thenThrowsNotOpen() {
            Billetterie billetterie = Billetterie.builder().evenement(billetterieEnabledEvenement)
                    .status(BilletterieStatus.NOT_OPEN).build();
            when(billetterieRepository.findForUpdateByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(billetterie));

            assertThatThrownBy(() -> billetterieService.lockOpenBilletterie(EVENEMENT_ID))
                    .isInstanceOf(BilletterieNotOpenException.class);
        }

        @Test
        void givenNoBilletterie_whenLockOpenBilletterie_thenThrowsNotFound() {
            when(billetterieRepository.findForUpdateByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> billetterieService.lockOpenBilletterie(EVENEMENT_ID))
                    .isInstanceOf(BilletterieNotFoundException.class);
        }

        @Test
        void givenOpenBilletterieDisabled_whenLockOpenBilletterie_thenThrowsNotFound() {
            Billetterie billetterie = Billetterie.builder().evenement(billetterieDisabledEvenement)
                    .status(BilletterieStatus.OPEN).build();
            when(billetterieRepository.findForUpdateByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(billetterie));

            assertThatThrownBy(() -> billetterieService.lockOpenBilletterie(EVENEMENT_ID))
                    .isInstanceOf(BilletterieNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // getOrganizerTicketing / getSales
    // -------------------------------------------------------------------------

    @Nested
    class GetOrganizerTicketing {

        @Test
        void givenEnabledBilletterie_whenGetOrganizerTicketing_thenMapsItsSettings() {
            Billetterie billetterie = Billetterie.builder().evenement(billetterieEnabledEvenement).build();
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(billetterie));
            when(billetterieMapper.toTicketingSettingsDto(billetterie)).thenReturn(TICKETING);

            assertThat(billetterieService.getOrganizerTicketing(EVENEMENT_ID)).contains(TICKETING);
        }

        @Test
        void givenDisabledBilletterie_whenGetOrganizerTicketing_thenReturnsEmpty() {
            Billetterie billetterie = Billetterie.builder().evenement(billetterieDisabledEvenement).build();
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(billetterie));

            assertThat(billetterieService.getOrganizerTicketing(EVENEMENT_ID)).isEmpty();
        }
    }

    @Nested
    class GetSales {

        @Test
        void givenEnabledBilletterie_whenGetSales_thenReturnsSoldHeldAndCapacity() {
            Billetterie billetterie = Billetterie.builder().evenement(billetterieEnabledEvenement).capacity(150).build();
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(billetterie));
            when(commandeService.getSoldTickets(billetterie)).thenReturn(42);
            when(commandeService.getHeldTickets(billetterie)).thenReturn(3);

            assertThat(billetterieService.getSales(EVENEMENT_ID)).contains(new TicketingSalesDto(42, 3, 150));
        }

        @Test
        void givenDisabledBilletterie_whenGetSales_thenReturnsEmpty() {
            Billetterie billetterie = Billetterie.builder().evenement(billetterieDisabledEvenement).build();
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(billetterie));

            assertThat(billetterieService.getSales(EVENEMENT_ID)).isEmpty();
            verifyNoInteractions(commandeService);
        }
    }

    @Nested
    class GetOrganizerCommandes {

        @Test
        void givenEnabledBilletterie_whenGetOrganizerCommandes_thenReturnsItsCommandes() {
            Billetterie billetterie = Billetterie.builder().evenement(billetterieEnabledEvenement).build();
            List<OrganizerCommandeDto> commandes = List.of(mock(OrganizerCommandeDto.class));
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(billetterie));
            when(commandeService.getOrganizerCommandes(billetterie)).thenReturn(commandes);

            assertThat(billetterieService.getOrganizerCommandes(EVENEMENT_ID)).isSameAs(commandes);
        }

        @Test
        void givenDisabledBilletterie_whenGetOrganizerCommandes_thenReturnsEmpty() {
            Billetterie billetterie = Billetterie.builder().evenement(billetterieDisabledEvenement).build();
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(billetterie));

            assertThat(billetterieService.getOrganizerCommandes(EVENEMENT_ID)).isEmpty();
            verifyNoInteractions(commandeService);
        }

        @Test
        void givenNoBilletterie_whenGetOrganizerCommandes_thenReturnsEmpty() {
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());

            assertThat(billetterieService.getOrganizerCommandes(EVENEMENT_ID)).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // putOrganizerTicketing
    // -------------------------------------------------------------------------

    @Nested
    class PutOrganizerTicketing {

        @Test
        void givenNoBilletterie_whenPutOrganizerTicketing_thenCreatesIt() {
            Billetterie created = Billetterie.builder().build();
            when(billetterieRepository.findForUpdateByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());
            when(billetterieMapper.toBilletterie(TICKETING, billetterieEnabledEvenement)).thenReturn(created);

            billetterieService.putOrganizerTicketing(billetterieEnabledEvenement, TICKETING, true);

            verify(billetterieRepository).save(created);
        }

        @Test
        void givenBilletterieWithSales_whenPutOrganizerTicketingWithCapacityAboveSold_thenReplacesItsSettings() {
            Billetterie existing = Billetterie.builder().evenement(billetterieEnabledEvenement)
                    .status(BilletterieStatus.NOT_OPEN).unitPriceCents(1000).capacity(100).build();
            when(billetterieRepository.findForUpdateByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(existing));
            when(commandeService.getSoldTickets(existing)).thenReturn(150);

            billetterieService.putOrganizerTicketing(billetterieEnabledEvenement, TICKETING, true);

            verify(billetterieRepository).save(existing);
            assertThat(existing.getStatus()).isEqualTo(BilletterieStatus.OPEN);
            assertThat(existing.getUnitPriceCents()).isEqualTo(1200);
            assertThat(existing.getPriceNote()).isEqualTo("Tout compris");
            assertThat(existing.getCapacity()).isEqualTo(150);
        }

        @Test
        void givenCapacityBelowSoldTickets_whenPutOrganizerTicketing_thenThrowsWithoutSaving() {
            Billetterie existing = Billetterie.builder().id(UUID.randomUUID()).evenement(billetterieEnabledEvenement)
                    .capacity(200).build();
            when(billetterieRepository.findForUpdateByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(existing));
            when(commandeService.getSoldTickets(existing)).thenReturn(151);

            assertThatThrownBy(() -> billetterieService.putOrganizerTicketing(billetterieEnabledEvenement, TICKETING, true))
                    .isInstanceOf(CapacityBelowSoldTicketsException.class);
            verify(billetterieRepository, never()).save(any());
            assertThat(existing.getCapacity()).isEqualTo(200);
        }

        @Test
        void givenPageNotPublishedAndBilletterieNotOpen_whenPutOrganizerTicketingOpen_thenThrowsWithoutSaving() {
            Billetterie existing = Billetterie.builder().evenement(billetterieEnabledEvenement)
                    .status(BilletterieStatus.NOT_OPEN).build();
            when(billetterieRepository.findForUpdateByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> billetterieService.putOrganizerTicketing(billetterieEnabledEvenement, TICKETING, false))
                    .isInstanceOf(BilletterieOpenWithoutPublishedPageException.class);
            verify(billetterieRepository, never()).save(any());
        }

        @Test
        void givenPageNotPublishedAndNoBilletterie_whenPutOrganizerTicketingOpen_thenThrows() {
            when(billetterieRepository.findForUpdateByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> billetterieService.putOrganizerTicketing(billetterieEnabledEvenement, TICKETING, false))
                    .isInstanceOf(BilletterieOpenWithoutPublishedPageException.class);
            verify(billetterieRepository, never()).save(any());
        }

        @Test
        void givenPageUnpublishedAndBilletterieStillOpen_whenPutOrganizerTicketingOpen_thenSavesItsSettings() {
            Billetterie existing = Billetterie.builder().evenement(billetterieEnabledEvenement)
                    .status(BilletterieStatus.OPEN).unitPriceCents(1000).capacity(150).build();
            when(billetterieRepository.findForUpdateByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(existing));

            billetterieService.putOrganizerTicketing(billetterieEnabledEvenement, TICKETING, false);

            verify(billetterieRepository).save(existing);
            assertThat(existing.getUnitPriceCents()).isEqualTo(1200);
        }

        @Test
        void givenPageNotPublished_whenPutOrganizerTicketingNotOpen_thenSaves() {
            TicketingSettingsDto notOpen = new TicketingSettingsDto(BilletterieStatus.NOT_OPEN, 1200, "Tout compris", 150);
            Billetterie created = Billetterie.builder().build();
            when(billetterieRepository.findForUpdateByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());
            when(billetterieMapper.toBilletterie(notOpen, billetterieEnabledEvenement)).thenReturn(created);

            billetterieService.putOrganizerTicketing(billetterieEnabledEvenement, notOpen, false);

            verify(billetterieRepository).save(created);
        }

        @Test
        void givenBilletterieDisabled_whenPutOrganizerTicketing_thenThrowsNotFound() {
            assertThatThrownBy(() -> billetterieService.putOrganizerTicketing(billetterieDisabledEvenement, TICKETING, true))
                    .isInstanceOf(BilletterieNotFoundException.class);
            verifyNoInteractions(billetterieRepository);
        }
    }

    // -------------------------------------------------------------------------
    // checkCanBeDisabled
    // -------------------------------------------------------------------------

    @Nested
    class CheckCanBeDisabled {

        @Test
        void givenBilletterieWithSales_whenCheckCanBeDisabled_thenThrowsWithSales() {
            Billetterie billetterie = Billetterie.builder().build();
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(billetterie));
            when(commandeService.hasSales(billetterie)).thenReturn(true);

            assertThatThrownBy(() -> billetterieService.checkCanBeDisabled(EVENEMENT_ID))
                    .isInstanceOf(BilletterieWithSalesException.class);
        }

        @Test
        void givenBilletterieWithoutSales_whenCheckCanBeDisabled_thenPasses() {
            Billetterie billetterie = Billetterie.builder().build();
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(billetterie));
            when(commandeService.hasSales(billetterie)).thenReturn(false);

            assertThatCode(() -> billetterieService.checkCanBeDisabled(EVENEMENT_ID)).doesNotThrowAnyException();
        }

        @Test
        void givenNoBilletterie_whenCheckCanBeDisabled_thenPasses() {
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());

            assertThatCode(() -> billetterieService.checkCanBeDisabled(EVENEMENT_ID)).doesNotThrowAnyException();
            verifyNoInteractions(commandeService);
        }
    }
}
