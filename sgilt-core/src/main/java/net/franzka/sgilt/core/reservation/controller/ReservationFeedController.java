package net.franzka.sgilt.core.reservation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.reservation.api.ReservationFeedApi;
import net.franzka.sgilt.core.reservation.dto.AddNoteRequest;
import net.franzka.sgilt.core.reservation.dto.FeedItemDto;
import net.franzka.sgilt.core.reservation.service.ReservationFeedService;
import net.franzka.sgilt.core.security.CurrentUserService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReservationFeedController implements ReservationFeedApi {

    private final ReservationFeedService reservationFeedService;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<FeedItemDto>> getFeed(UUID reservationId) {
        UUID userId = currentUserService.getId();
        log.info("GET /reservations/{}/feed", reservationId);
        return ResponseEntity.ok(reservationFeedService.getFeedForClient(reservationId, userId));
    }

    @Override
    @Transactional
    public ResponseEntity<FeedItemDto> addNote(UUID reservationId, AddNoteRequest body) {
        Utilisateur utilisateur = currentUserService.get();
        log.info("POST /reservations/{}/feed/notes", reservationId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationFeedService.addNote(reservationId, utilisateur, body));
    }
}
