package net.franzka.sgilt.core.reservation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.reservation.api.ReservationApi;
import net.franzka.sgilt.core.reservation.dto.ActiveReservationsDto;
import net.franzka.sgilt.core.reservation.dto.ProBoardCountsDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationSummaryDto;
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
public class ReservationController implements ReservationApi {

    private final ReservationService reservationService;
    private final EvenementService evenementService;
    private final CurrentUserService currentUserService;

    @Override
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ReservationSummaryDto>> getByEventId(UUID eventId) {
        UUID userId = currentUserService.getId();
        log.info("GET /reservations?eventId={}", eventId);
        evenementService.verifyEventOwnership(eventId, userId);
        return ResponseEntity.ok(reservationService.getReservationSummaries(eventId));
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Transactional(readOnly = true)
    public ResponseEntity<ActiveReservationsDto> getActive() {
        UUID userId = currentUserService.getId();
        log.info("GET /reservations/active");
        return ResponseEntity.ok(reservationService.getActiveReservations(userId));
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Transactional(readOnly = true)
    public ResponseEntity<ReservationMetaDto> getDetail(UUID reservationId) {
        UUID userId = currentUserService.getId();
        log.info("GET /reservations/{}", reservationId);
        return ResponseEntity.ok(reservationService.getMeta(reservationId, userId));
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_PRO')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ProReservationSummaryDto>> getProReservations() {
        UUID userId = currentUserService.getId();
        log.info("GET /reservations/pro");
        return ResponseEntity.ok(reservationService.getProReservations(userId));
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_PRO')")
    @Transactional(readOnly = true)
    public ResponseEntity<ProBoardCountsDto> getProBoardCounts() {
        UUID userId = currentUserService.getId();
        log.info("GET /reservations/pro/counts");
        return ResponseEntity.ok(reservationService.getProBoardCounts(userId));
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Transactional
    public ResponseEntity<Void> cancel(UUID reservationId) {
        UUID userId = currentUserService.getId();
        log.info("POST /reservations/{}/cancel", reservationId);
        reservationService.cancel(reservationId, userId);
        return ResponseEntity.noContent().build();
    }
}
