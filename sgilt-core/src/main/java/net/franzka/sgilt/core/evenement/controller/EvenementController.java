package net.franzka.sgilt.core.evenement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.api.EvenementApi;
import net.franzka.sgilt.core.evenement.dto.EventCountsDto;
import net.franzka.sgilt.core.evenement.dto.EventDetailDto;
import net.franzka.sgilt.core.evenement.dto.EventPatchDto;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import net.franzka.sgilt.core.security.CurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EvenementController implements EvenementApi {

    private final EvenementService evenementService;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<EvenementSummaryDto>> getMyEvents() {
        UUID userId = currentUserService.getId();
        log.info("GET /events");
        return ResponseEntity.ok(evenementService.getUserEvents(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<EventDetailDto> getEventDetail(UUID eventId) {
        UUID userId = currentUserService.getId();
        log.info("GET /events/{}", eventId);
        return ResponseEntity.ok(evenementService.getEventDetail(eventId, userId));
    }

    @Override
    @Transactional
    public ResponseEntity<EventDetailDto> patchEvent(UUID eventId, EventPatchDto patch) {
        UUID userId = currentUserService.getId();
        log.info("PATCH /events/{}", eventId);
        return ResponseEntity.ok(evenementService.patchEvent(eventId, userId, patch));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<EventCountsDto> getEventCounts(UUID eventId) {
        UUID userId = currentUserService.getId();
        log.info("GET /events/{}/counts", eventId);
        return ResponseEntity.ok(evenementService.getEventCounts(eventId, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<ReservationSummaryDto>> getEventReservations(UUID eventId) {
        UUID userId = currentUserService.getId();
        log.info("GET /events/{}/reservations", eventId);
        return ResponseEntity.ok(evenementService.getEventReservations(eventId, userId));
    }
}
