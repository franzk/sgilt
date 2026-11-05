package net.franzka.sgilt.core.evenement.billetterie.commande.service;

import net.franzka.sgilt.core.admin.mailer.AdminMailerService;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.CommandeStatus;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeSummaryDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CreateCommandeRequest;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.CommandeNotFoundException;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.CommandeNotPaidException;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.NotEnoughTicketsException;
import net.franzka.sgilt.core.evenement.billetterie.commande.mapper.CommandeMapper;
import net.franzka.sgilt.core.evenement.billetterie.commande.repository.CommandeRepository;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.pay.PayClient;
import net.franzka.sgilt.core.pay.PaymentIntentRequest;
import net.franzka.sgilt.core.pay.PaymentIntentResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandeServiceTest {

    private static final UUID BILLETTERIE_ID = UUID.randomUUID();

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private CommandeMapper commandeMapper;

    @Mock
    private PayClient payClient;

    @Mock
    private AdminMailerService adminMailerService;

    @InjectMocks
    private CommandeService commandeService;

    private final Billetterie billetterie = Billetterie.builder().id(BILLETTERIE_ID).capacity(10).build();

    private void givenTakenTickets(List<Integer> paid, List<Integer> held) {
        when(commandeRepository.findByBilletterieIdAndStatus(BILLETTERIE_ID, CommandeStatus.PAID))
                .thenReturn(commandes(paid));
        when(commandeRepository.findByBilletterieIdAndStatusAndExpiresAtAfter(
                eq(BILLETTERIE_ID), eq(CommandeStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(commandes(held));
    }

    private static List<Commande> commandes(List<Integer> quantities) {
        return quantities.stream().map(quantity -> Commande.builder().quantity(quantity).build()).toList();
    }

    private static CreateCommandeRequest request(int quantity) {
        return new CreateCommandeRequest("slug", quantity, "Jeanne", "Martin", "jeanne@example.com");
    }

    // -------------------------------------------------------------------------
    // getRemainingTickets
    // -------------------------------------------------------------------------

    @Nested
    class GetRemainingTickets {

        @Test
        void givenNoCommande_whenGetRemainingTickets_thenReturnsCapacity() {
            givenTakenTickets(List.of(), List.of());

            assertThat(commandeService.getRemainingTickets(billetterie)).isEqualTo(10);
        }

        @Test
        void givenPaidAndHeldCommandes_whenGetRemainingTickets_thenSubtractsBoth() {
            givenTakenTickets(List.of(2, 3), List.of(1));

            assertThat(commandeService.getRemainingTickets(billetterie)).isEqualTo(4);
        }

        @Test
        void givenCapacityExceeded_whenGetRemainingTickets_thenReturnsZero() {
            givenTakenTickets(List.of(8), List.of(4));

            assertThat(commandeService.getRemainingTickets(billetterie)).isZero();
        }

        @Test
        void givenAnyCall_whenGetRemainingTickets_thenOnlyCountsPendingNotYetExpired() {
            givenTakenTickets(List.of(), List.of());
            ArgumentCaptor<LocalDateTime> instant = ArgumentCaptor.forClass(LocalDateTime.class);

            commandeService.getRemainingTickets(billetterie);

            verify(commandeRepository).findByBilletterieIdAndStatusAndExpiresAtAfter(
                    eq(BILLETTERIE_ID), eq(CommandeStatus.PENDING), instant.capture());
            assertThat(instant.getValue()).isCloseTo(LocalDateTime.now(), within(5, ChronoUnit.SECONDS));
        }
    }

    // -------------------------------------------------------------------------
    // getSoldTickets / getHeldTickets
    // -------------------------------------------------------------------------

    @Nested
    class GetSoldTickets {

        @Test
        void givenPaidCommandes_whenGetSoldTickets_thenSumsTheirPlaces() {
            when(commandeRepository.findByBilletterieIdAndStatus(BILLETTERIE_ID, CommandeStatus.PAID))
                    .thenReturn(commandes(List.of(2, 3)));

            assertThat(commandeService.getSoldTickets(billetterie)).isEqualTo(5);
        }
    }

    @Nested
    class GetHeldTickets {

        @Test
        void givenPendingCommandesNotYetExpired_whenGetHeldTickets_thenSumsTheirPlaces() {
            when(commandeRepository.findByBilletterieIdAndStatusAndExpiresAtAfter(
                    eq(BILLETTERIE_ID), eq(CommandeStatus.PENDING), any(LocalDateTime.class)))
                    .thenReturn(commandes(List.of(1, 4)));

            assertThat(commandeService.getHeldTickets(billetterie)).isEqualTo(5);
        }
    }

    // -------------------------------------------------------------------------
    // hasSales
    // -------------------------------------------------------------------------

    @Nested
    class HasSales {

        @Test
        void givenPaidCommande_whenHasSales_thenReturnsTrue() {
            when(commandeRepository.existsByBilletterieIdAndStatus(BILLETTERIE_ID, CommandeStatus.PAID)).thenReturn(true);

            assertThat(commandeService.hasSales(billetterie)).isTrue();
        }

        @Test
        void givenNoPaidCommande_whenHasSales_thenReturnsFalse() {
            when(commandeRepository.existsByBilletterieIdAndStatus(BILLETTERIE_ID, CommandeStatus.PAID)).thenReturn(false);

            assertThat(commandeService.hasSales(billetterie)).isFalse();
        }
    }

    // -------------------------------------------------------------------------
    // createCommande
    // -------------------------------------------------------------------------

    @Nested
    class CreateCommande {

        @Test
        void givenEnoughTickets_whenCreateCommande_thenSavesItHeldForHoldDurationWithItsPaymentIntent() {
            givenTakenTickets(List.of(4), List.of(2));
            CreateCommandeRequest request = request(4);
            UUID commandeId = UUID.randomUUID();
            Commande commande = Commande.builder().build();
            Commande saved = Commande.builder().id(commandeId).quantity(4).unitPriceCents(1200).build();
            CommandeDto dto = mock(CommandeDto.class);
            ArgumentCaptor<LocalDateTime> expiresAt = ArgumentCaptor.forClass(LocalDateTime.class);
            when(commandeMapper.toCommande(eq(request), eq(billetterie), any(LocalDateTime.class))).thenReturn(commande);
            when(commandeRepository.save(commande)).thenReturn(saved);
            when(payClient.createPaymentIntent(new PaymentIntentRequest(commandeId, 4800)))
                    .thenReturn(new PaymentIntentResponse("pi_123_secret_456"));
            when(commandeMapper.toCommandeDto(saved, "pi_123_secret_456")).thenReturn(dto);

            assertThat(commandeService.createCommande(billetterie, request)).isSameAs(dto);

            verify(commandeMapper).toCommande(eq(request), eq(billetterie), expiresAt.capture());
            assertThat(expiresAt.getValue()).isCloseTo(
                    LocalDateTime.now().plus(CommandeService.COMMANDE_HOLD_DURATION), within(5, ChronoUnit.SECONDS));
        }

        @Test
        void givenNotEnoughTickets_whenCreateCommande_thenThrowsAndSavesNothing() {
            givenTakenTickets(List.of(4), List.of(2));

            assertThatThrownBy(() -> commandeService.createCommande(billetterie, request(5)))
                    .isInstanceOf(NotEnoughTicketsException.class);
            verify(commandeRepository, never()).save(any());
            verify(payClient, never()).createPaymentIntent(any());
        }
    }

    // -------------------------------------------------------------------------
    // getCommande
    // -------------------------------------------------------------------------

    @Nested
    class GetCommande {

        private final UUID commandeId = UUID.randomUUID();

        @Test
        void givenExistingCommande_whenGetCommande_thenReturnsItsSummary() {
            Commande commande = Commande.builder().id(commandeId).build();
            CommandeSummaryDto dto = mock(CommandeSummaryDto.class);
            when(commandeRepository.findById(commandeId)).thenReturn(Optional.of(commande));
            when(commandeMapper.toCommandeSummaryDto(commande)).thenReturn(dto);

            assertThat(commandeService.getCommande(commandeId)).isSameAs(dto);
        }

        @Test
        void givenUnknownCommande_whenGetCommande_thenThrowsCommandeNotFoundException() {
            when(commandeRepository.findById(commandeId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> commandeService.getCommande(commandeId))
                    .isInstanceOf(CommandeNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // getPaidCommande
    // -------------------------------------------------------------------------

    @Nested
    class GetPaidCommande {

        private final UUID commandeId = UUID.randomUUID();

        @Test
        void givenPaidCommande_whenGetPaidCommande_thenReturnsIt() {
            Commande commande = Commande.builder().id(commandeId).status(CommandeStatus.PAID).build();
            when(commandeRepository.findById(commandeId)).thenReturn(Optional.of(commande));

            assertThat(commandeService.getPaidCommande(commandeId)).isSameAs(commande);
        }

        @Test
        void givenPendingCommande_whenGetPaidCommande_thenThrowsCommandeNotPaidException() {
            Commande commande = Commande.builder().id(commandeId).status(CommandeStatus.PENDING).build();
            when(commandeRepository.findById(commandeId)).thenReturn(Optional.of(commande));

            assertThatThrownBy(() -> commandeService.getPaidCommande(commandeId))
                    .isInstanceOf(CommandeNotPaidException.class);
        }

        @Test
        void givenUnknownCommande_whenGetPaidCommande_thenThrowsCommandeNotFoundException() {
            when(commandeRepository.findById(commandeId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> commandeService.getPaidCommande(commandeId))
                    .isInstanceOf(CommandeNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // markPaid
    // -------------------------------------------------------------------------

    @Nested
    class MarkPaid {

        private final UUID commandeId = UUID.randomUUID();

        // Commande en attente de 2 places, sur la billetterie de 10 places.
        private Commande givenPendingCommande(List<Integer> alreadyPaid) {
            Commande commande = Commande.builder()
                    .id(commandeId).billetterie(billetterie).quantity(2).status(CommandeStatus.PENDING).build();
            when(commandeRepository.findById(commandeId)).thenReturn(Optional.of(commande));
            when(commandeRepository.findByBilletterieIdAndStatus(BILLETTERIE_ID, CommandeStatus.PAID))
                    .thenReturn(commandes(alreadyPaid));
            return commande;
        }

        @Test
        void givenPendingCommande_whenMarkPaid_thenSavesItPaidWithEntryCodeAndReturnsIt() {
            Commande commande = givenPendingCommande(List.of(3));

            assertThat(commandeService.markPaid(commandeId)).containsSame(commande);

            assertThat(commande.getStatus()).isEqualTo(CommandeStatus.PAID);
            assertThat(commande.getCode()).matches("[A-Za-z0-9_-]{22}");
            verify(commandeRepository).save(commande);
        }

        @Test
        void givenPaymentFillingCapacityExactly_whenMarkPaid_thenSendsNoAlert() {
            givenPendingCommande(List.of(5, 3));

            commandeService.markPaid(commandeId);

            verify(adminMailerService, never()).sendOversoldAlert(any(), anyInt());
        }

        @Test
        void givenPaymentExceedingCapacity_whenMarkPaid_thenSavesItPaidAndAlertsAdmin() {
            Commande commande = givenPendingCommande(List.of(6, 3));

            commandeService.markPaid(commandeId);

            assertThat(commande.getStatus()).isEqualTo(CommandeStatus.PAID);
            verify(commandeRepository).save(commande);
            verify(adminMailerService).sendOversoldAlert(commande, 11);
        }

        @Test
        void givenAlreadyPaidCommande_whenMarkPaid_thenSavesNothingAndReturnsEmpty() {
            Commande commande = Commande.builder().id(commandeId).status(CommandeStatus.PAID).build();
            when(commandeRepository.findById(commandeId)).thenReturn(Optional.of(commande));

            assertThat(commandeService.markPaid(commandeId)).isEmpty();

            assertThat(commande.getStatus()).isEqualTo(CommandeStatus.PAID);
            verify(commandeRepository, never()).save(any());
            verify(adminMailerService, never()).sendOversoldAlert(any(), anyInt());
        }

        @Test
        void givenUnknownCommande_whenMarkPaid_thenThrowsCommandeNotFoundException() {
            when(commandeRepository.findById(commandeId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> commandeService.markPaid(commandeId))
                    .isInstanceOf(CommandeNotFoundException.class);
        }
    }
}
