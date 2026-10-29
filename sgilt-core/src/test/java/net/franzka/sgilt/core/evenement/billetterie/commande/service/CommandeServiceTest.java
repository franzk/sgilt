package net.franzka.sgilt.core.evenement.billetterie.commande.service;

import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.CommandeStatus;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CreateCommandeRequest;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.NotEnoughTicketsException;
import net.franzka.sgilt.core.evenement.billetterie.commande.mapper.CommandeMapper;
import net.franzka.sgilt.core.evenement.billetterie.commande.repository.CommandeRepository;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
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
    // createCommande
    // -------------------------------------------------------------------------

    @Nested
    class CreateCommande {

        @Test
        void givenEnoughTickets_whenCreateCommande_thenSavesItHeldForHoldDuration() {
            givenTakenTickets(List.of(4), List.of(2));
            CreateCommandeRequest request = request(4);
            Commande commande = Commande.builder().build();
            Commande saved = Commande.builder().build();
            CommandeDto dto = mock(CommandeDto.class);
            ArgumentCaptor<LocalDateTime> expiresAt = ArgumentCaptor.forClass(LocalDateTime.class);
            when(commandeMapper.toCommande(eq(request), eq(billetterie), any(LocalDateTime.class))).thenReturn(commande);
            when(commandeRepository.save(commande)).thenReturn(saved);
            when(commandeMapper.toCommandeDto(saved)).thenReturn(dto);

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
        }
    }
}
