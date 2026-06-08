package net.franzka.sgilt.core.reservation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.reservation.api.ClientReservationApi;
import net.franzka.sgilt.core.reservation.dto.ActiveReservationsDto;
import net.franzka.sgilt.core.reservation.dto.ReservationMetaDto;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.security.CurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ROLE_USER')")
public class ClientReservationController implements ClientReservationApi {

    private final ReservationService reservationService;
    private final EvenementService evenementService;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<ReservationSummaryDto>> getByEventId(UUID eventId) {
        UUID userId = currentUserService.getId();
        log.info("GET /user/reservations?eventId={}", eventId);
        evenementService.verifyEventOwnership(eventId, userId);
        return ResponseEntity.ok(reservationService.getReservationSummaries(eventId));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ActiveReservationsDto> getActive() {
        UUID userId = currentUserService.getId();
        log.info("GET /user/reservations/active");
        return ResponseEntity.ok(reservationService.getActiveReservations(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ReservationMetaDto> getDetail(UUID reservationId) {
        UUID userId = currentUserService.getId();
        log.info("GET /user/reservations/{}", reservationId);
        reservationService.verifyOwnershipByReservationId(reservationId, userId);
        return ResponseEntity.ok(reservationService.getMeta(reservationId));
    }

    @Override
    @Transactional
    public ResponseEntity<Void> cancel(UUID reservationId) {
        UUID userId = currentUserService.getId();
        log.info("POST /user/reservations/{}/cancel", reservationId);
        reservationService.verifyOwnershipByReservationId(reservationId, userId);
        reservationService.cancel(reservationId);
        return ResponseEntity.noContent().build();
    }
}
