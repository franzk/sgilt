package net.franzka.sgilt.core.evenement.billetterie.service;

import net.franzka.sgilt.core.evenement.billetterie.commande.service.CommandeService;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;
import net.franzka.sgilt.core.evenement.billetterie.dto.AdminTicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotFoundException;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotOpenException;
import net.franzka.sgilt.core.evenement.billetterie.mapper.BilletterieMapper;
import net.franzka.sgilt.core.evenement.billetterie.repository.BilletterieRepository;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BilletterieServiceTest {

    private static final UUID EVENEMENT_ID = UUID.randomUUID();
    private static final AdminTicketingDto TICKETING = new AdminTicketingDto(BilletterieStatus.OPEN, 1200, 150);

    @Mock
    private BilletterieRepository billetterieRepository;

    @Mock
    private BilletterieMapper billetterieMapper;

    @Mock
    private CommandeService commandeService;

    @InjectMocks
    private BilletterieService billetterieService;

    private final Evenement evenement = Evenement.builder().id(EVENEMENT_ID).build();

    // -------------------------------------------------------------------------
    // getPublicTicketing
    // -------------------------------------------------------------------------

    @Nested
    class GetPublicTicketing {

        @Test
        void givenBilletterie_whenGetPublicTicketing_thenMapsItWithRemainingTickets() {
            Billetterie billetterie = Billetterie.builder().status(BilletterieStatus.OPEN).unitPriceCents(1200).capacity(150).build();
            TicketingDto dto = new TicketingDto(BilletterieStatus.OPEN, 1200, 42);
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
    }

    // -------------------------------------------------------------------------
    // getAdminTicketing
    // -------------------------------------------------------------------------

    @Nested
    class GetAdminTicketing {

        @Test
        void givenBilletterie_whenGetAdminTicketing_thenMapsIt() {
            Billetterie billetterie = Billetterie.builder().build();
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(billetterie));
            when(billetterieMapper.toAdminTicketingDto(billetterie)).thenReturn(TICKETING);

            assertThat(billetterieService.getAdminTicketing(EVENEMENT_ID)).contains(TICKETING);
        }

        @Test
        void givenNoBilletterie_whenGetAdminTicketing_thenReturnsEmpty() {
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());

            assertThat(billetterieService.getAdminTicketing(EVENEMENT_ID)).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // lockOpenBilletterie
    // -------------------------------------------------------------------------

    @Nested
    class LockOpenBilletterie {

        @Test
        void givenOpenBilletterie_whenLockOpenBilletterie_thenReturnsItLocked() {
            Billetterie billetterie = Billetterie.builder().status(BilletterieStatus.OPEN).build();
            when(billetterieRepository.findForUpdateByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(billetterie));

            assertThat(billetterieService.lockOpenBilletterie(EVENEMENT_ID)).isSameAs(billetterie);
        }

        @Test
        void givenNotOpenBilletterie_whenLockOpenBilletterie_thenThrowsNotOpen() {
            Billetterie billetterie = Billetterie.builder().status(BilletterieStatus.NOT_OPEN).build();
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
    }

    // -------------------------------------------------------------------------
    // putTicketing
    // -------------------------------------------------------------------------

    @Nested
    class PutTicketing {

        @Test
        void givenNoBilletterie_whenPutTicketing_thenCreatesIt() {
            Billetterie created = Billetterie.builder().build();
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());
            when(billetterieMapper.toBilletterie(TICKETING, evenement)).thenReturn(created);

            billetterieService.putTicketing(evenement, TICKETING);

            verify(billetterieRepository).save(created);
        }

        @Test
        void givenBilletterie_whenPutTicketing_thenReplacesItsFields() {
            Billetterie existing = Billetterie.builder()
                    .status(BilletterieStatus.NOT_OPEN).unitPriceCents(1000).capacity(100).build();
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(existing));

            billetterieService.putTicketing(evenement, TICKETING);

            assertThat(existing.getStatus()).isEqualTo(BilletterieStatus.OPEN);
            assertThat(existing.getUnitPriceCents()).isEqualTo(1200);
            assertThat(existing.getCapacity()).isEqualTo(150);
            verify(billetterieRepository).save(existing);
            verify(billetterieMapper, never()).toBilletterie(any(), any());
        }

        @Test
        void givenBilletterieAndNullTicketing_whenPutTicketing_thenDeletesIt() {
            Billetterie existing = Billetterie.builder().build();
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(existing));

            billetterieService.putTicketing(evenement, null);

            verify(billetterieRepository).delete(existing);
            verify(billetterieRepository, never()).save(any());
        }

        @Test
        void givenNoBilletterieAndNullTicketing_whenPutTicketing_thenDoesNothing() {
            when(billetterieRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());

            billetterieService.putTicketing(evenement, null);

            verify(billetterieRepository, never()).delete(any());
            verify(billetterieRepository, never()).save(any());
        }
    }
}
