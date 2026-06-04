package net.franzka.sgilt.core.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.reservation.api.ReservationFeedApi;
import net.franzka.sgilt.core.reservation.dto.AddNoteRequest;
import net.franzka.sgilt.core.reservation.dto.FeedItemDto;
import net.franzka.sgilt.core.storage.FileStreamResult;
import net.franzka.sgilt.core.reservation.service.ReservationFeedService;
import net.franzka.sgilt.core.security.CurrentUserService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('ROLE_USER')")
public class ReservationFeedController implements ReservationFeedApi {

    private final ReservationFeedService reservationFeedService;
    private final CurrentUserService     currentUserService;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<FeedItemDto>> getFeed(UUID reservationId) {
        UUID userId = currentUserService.getId();
        log.info("GET /reservations/{}/feed", reservationId);
        return ResponseEntity.ok(reservationFeedService.getFeedForClient(reservationId, userId));
    }

    @Override
    @Valid
    @Transactional
    public ResponseEntity<FeedItemDto> addNote(UUID reservationId, AddNoteRequest body) {
        Utilisateur utilisateur = currentUserService.get();
        log.info("POST /reservations/{}/feed/notes", reservationId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationFeedService.addNote(reservationId, utilisateur, body));
    }

    @Override
    @Valid
    @Transactional
    public ResponseEntity<FeedItemDto> addDocument(UUID reservationId, MultipartFile file, boolean isPersonal) {
        Utilisateur utilisateur = currentUserService.get();
        log.info("POST /reservations/{}/feed/documents", reservationId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationFeedService.addDocument(reservationId, utilisateur, file, isPersonal));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<InputStreamResource> streamDocument(UUID reservationId, UUID documentId) throws IOException {
        UUID userId = currentUserService.getId();
        log.info("GET /reservations/{}/feed/documents/{}", reservationId, documentId);
        FileStreamResult result = reservationFeedService.streamDocument(reservationId, documentId, userId);
        return ResponseEntity.ok()
                .contentType(result.resolvedMediaType())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(result.fileName()).build().toString())
                .body(new InputStreamResource(result.inputStream()));
    }
}
