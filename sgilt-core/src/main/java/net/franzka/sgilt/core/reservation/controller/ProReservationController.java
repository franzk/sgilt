package net.franzka.sgilt.core.reservation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.reservation.api.ProReservationApi;
import net.franzka.sgilt.core.reservation.dto.ProBoardCountsDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationDetailDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationSummaryDto;
import net.franzka.sgilt.core.reservation.dto.RefuseReservationRequest;
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
@PreAuthorize("hasAuthority('ROLE_PRO')")
public class ProReservationController implements ProReservationApi {

    private final ReservationService reservationService;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<ProReservationSummaryDto>> getAll() {
        UUID userId = currentUserService.getId();
        log.info("GET /pro/reservations");
        return ResponseEntity.ok(reservationService.getProReservations(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ProBoardCountsDto> getCounts() {
        UUID userId = currentUserService.getId();
        log.info("GET /pro/reservations/counts");
        return ResponseEntity.ok(reservationService.getProBoardCounts(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ProReservationDetailDto> getDetail(UUID reservationId) {
        UUID userId = currentUserService.getId();
        log.info("GET /pro/reservations/{}", reservationId);
        reservationService.verifyProOwnershipByReservationId(reservationId, userId);
        return ResponseEntity.ok(reservationService.getProReservationDetail(reservationId));
    }

    @Override
    @Transactional
    public ResponseEntity<Void> accept(UUID reservationId) {
        UUID userId = currentUserService.getId();
        log.info("POST /pro/reservations/{}/accept", reservationId);
        reservationService.verifyProOwnershipByReservationId(reservationId, userId);
        reservationService.accept(reservationId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> confirm(UUID reservationId) {
        UUID userId = currentUserService.getId();
        log.info("POST /pro/reservations/{}/confirm", reservationId);
        reservationService.verifyProOwnershipByReservationId(reservationId, userId);
        reservationService.confirm(reservationId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> refuse(UUID reservationId, RefuseReservationRequest body) {
        UUID userId = currentUserService.getId();
        log.info("POST /pro/reservations/{}/refuse", reservationId);
        reservationService.verifyProOwnershipByReservationId(reservationId, userId);
        reservationService.refuse(reservationId, body);
        return ResponseEntity.noContent().build();
    }
}
