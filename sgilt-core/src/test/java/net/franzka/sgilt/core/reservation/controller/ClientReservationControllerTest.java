package net.franzka.sgilt.core.reservation.controller;

import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.reservation.dto.ActiveReservationsDto;
import net.franzka.sgilt.core.reservation.dto.CancelReservationRequest;
import net.franzka.sgilt.core.reservation.dto.ReservationMetaDto;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.security.CurrentUserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private EvenementService evenementService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ClientReservationController controller;

    private final UUID userId = UUID.randomUUID();
    private final UUID reservationId = UUID.randomUUID();
    private final UUID eventId = UUID.randomUUID();

    // -------------------------------------------------------------------------
    // getByEventId
    // -------------------------------------------------------------------------

    @Nested
    class GetByEventId {

        @Test
        void givenOwnedEvent_whenGetByEventId_thenVerifiesOwnershipBeforeReturningSummaries() {
            when(currentUserService.getId()).thenReturn(userId);
            List<ReservationSummaryDto> summaries = List.of(mock(ReservationSummaryDto.class));
            when(reservationService.getReservationSummaries(eventId)).thenReturn(summaries);

            ResponseEntity<List<ReservationSummaryDto>> response = controller.getByEventId(eventId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(summaries);
            InOrder order = inOrder(evenementService, reservationService);
            order.verify(evenementService).verifyEventOwnership(eventId, userId);
            order.verify(reservationService).getReservationSummaries(eventId);
        }
    }

    // -------------------------------------------------------------------------
    // getActive
    // -------------------------------------------------------------------------

    @Nested
    class GetActive {

        @Test
        void givenCurrentUser_whenGetActive_thenReturnsActiveReservations() {
            when(currentUserService.getId()).thenReturn(userId);
            ActiveReservationsDto dto = mock(ActiveReservationsDto.class);
            when(reservationService.getActiveReservations(userId)).thenReturn(dto);

            ResponseEntity<ActiveReservationsDto> response = controller.getActive();

            assertThat(response.getBody()).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // getDetail
    // -------------------------------------------------------------------------

    @Nested
    class GetDetail {

        @Test
        void givenOwnedReservation_whenGetDetail_thenVerifiesOwnershipBeforeReturningMeta() {
            when(currentUserService.getId()).thenReturn(userId);
            ReservationMetaDto dto = mock(ReservationMetaDto.class);
            when(reservationService.getMeta(reservationId)).thenReturn(dto);

            ResponseEntity<ReservationMetaDto> response = controller.getDetail(reservationId);

            assertThat(response.getBody()).isEqualTo(dto);
            InOrder order = inOrder(reservationService);
            order.verify(reservationService).verifyOwnershipByReservationId(reservationId, userId);
            order.verify(reservationService).getMeta(reservationId);
        }
    }

    // -------------------------------------------------------------------------
    // markContacted
    // -------------------------------------------------------------------------

    @Nested
    class MarkContacted {

        @Test
        void givenOwnedReservation_whenMarkContacted_thenVerifiesOwnershipAndReturnsNoContent() {
            when(currentUserService.getId()).thenReturn(userId);

            ResponseEntity<Void> response = controller.markContacted(reservationId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(reservationService).verifyOwnershipByReservationId(reservationId, userId);
            verify(reservationService).markContacted(reservationId);
        }
    }

    // -------------------------------------------------------------------------
    // confirm
    // -------------------------------------------------------------------------

    @Nested
    class Confirm {

        @Test
        void givenOwnedReservation_whenConfirm_thenVerifiesOwnershipAndReturnsNoContent() {
            when(currentUserService.getId()).thenReturn(userId);

            ResponseEntity<Void> response = controller.confirm(reservationId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(reservationService).verifyOwnershipByReservationId(reservationId, userId);
            verify(reservationService).confirm(reservationId);
        }
    }

    // -------------------------------------------------------------------------
    // cancel
    // -------------------------------------------------------------------------

    @Nested
    class Cancel {

        @Test
        void givenOwnedReservation_whenCancel_thenVerifiesOwnershipAndDelegatesReasonAndPersonalFlag() {
            when(currentUserService.getId()).thenReturn(userId);
            CancelReservationRequest body = new CancelReservationRequest("Indisponible", true);

            ResponseEntity<Void> response = controller.cancel(reservationId, body);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(reservationService).verifyOwnershipByReservationId(reservationId, userId);
            verify(reservationService).cancel(reservationId, "Indisponible", true);
        }
    }
}
