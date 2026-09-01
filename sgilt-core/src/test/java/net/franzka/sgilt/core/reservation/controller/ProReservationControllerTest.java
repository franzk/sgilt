package net.franzka.sgilt.core.reservation.controller;

import net.franzka.sgilt.core.reservation.dto.CancelReservationRequest;
import net.franzka.sgilt.core.reservation.dto.ProBoardCountsDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationDetailDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationSummaryDto;
import net.franzka.sgilt.core.reservation.dto.RefuseReservationRequest;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.security.CurrentUserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ProReservationController controller;

    private final UUID userId = UUID.randomUUID();
    private final UUID reservationId = UUID.randomUUID();

    // -------------------------------------------------------------------------
    // getAll
    // -------------------------------------------------------------------------

    @Nested
    class GetAll {

        @Test
        void givenCurrentUser_whenGetAll_thenReturnsProReservations() {
            when(currentUserService.getId()).thenReturn(userId);
            List<ProReservationSummaryDto> summaries = List.of(mock(ProReservationSummaryDto.class));
            when(reservationService.getProReservations(userId)).thenReturn(summaries);

            ResponseEntity<List<ProReservationSummaryDto>> response = controller.getAll();

            assertThat(response.getBody()).isEqualTo(summaries);
        }
    }

    // -------------------------------------------------------------------------
    // getCounts
    // -------------------------------------------------------------------------

    @Nested
    class GetCounts {

        @Test
        void givenCurrentUser_whenGetCounts_thenReturnsBoardCounts() {
            when(currentUserService.getId()).thenReturn(userId);
            ProBoardCountsDto dto = new ProBoardCountsDto(1, 2, 3);
            when(reservationService.getProBoardCounts(userId)).thenReturn(dto);

            ResponseEntity<ProBoardCountsDto> response = controller.getCounts();

            assertThat(response.getBody()).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // getDetail
    // -------------------------------------------------------------------------

    @Nested
    class GetDetail {

        @Test
        void givenOwnedReservation_whenGetDetail_thenVerifiesOwnershipBeforeReturningDetail() {
            when(currentUserService.getId()).thenReturn(userId);
            ProReservationDetailDto dto = mock(ProReservationDetailDto.class);
            when(reservationService.getProReservationDetail(reservationId)).thenReturn(dto);

            ResponseEntity<ProReservationDetailDto> response = controller.getDetail(reservationId);

            assertThat(response.getBody()).isEqualTo(dto);
            verify(reservationService).verifyProOwnershipByReservationId(reservationId, userId);
        }
    }

    // -------------------------------------------------------------------------
    // refuse
    // -------------------------------------------------------------------------

    @Nested
    class Refuse {

        @Test
        void givenOwnedReservation_whenRefuse_thenVerifiesOwnershipAndDelegatesRequest() {
            when(currentUserService.getId()).thenReturn(userId);
            RefuseReservationRequest body = new RefuseReservationRequest("Indisponible");

            ResponseEntity<Void> response = controller.refuse(reservationId, body);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(reservationService).verifyProOwnershipByReservationId(reservationId, userId);
            verify(reservationService).refuse(reservationId, body);
        }
    }

    // -------------------------------------------------------------------------
    // cancelByPro
    // -------------------------------------------------------------------------

    @Nested
    class CancelByPro {

        @Test
        void givenOwnedReservation_whenCancelByPro_thenVerifiesOwnershipAndDelegatesReasonAndPersonalFlag() {
            when(currentUserService.getId()).thenReturn(userId);
            CancelReservationRequest body = new CancelReservationRequest("Motif", false);

            ResponseEntity<Void> response = controller.cancelByPro(reservationId, body);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(reservationService).verifyProOwnershipByReservationId(reservationId, userId);
            verify(reservationService).cancelByPro(reservationId, "Motif", false);
        }
    }
}
