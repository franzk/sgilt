package net.franzka.sgilt.core.reservation.api;

import net.franzka.sgilt.core.reservation.dto.AddNoteRequest;
import net.franzka.sgilt.core.reservation.dto.FeedItemDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequestMapping("api/v1/reservations/{reservationId}/feed")
public interface ReservationFeedApi {

    @GetMapping
    ResponseEntity<List<FeedItemDto>> getFeed(@PathVariable UUID reservationId);

    @PostMapping("/notes")
    ResponseEntity<FeedItemDto> addNote(
            @PathVariable UUID reservationId,
            @RequestBody AddNoteRequest body
    );

    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<FeedItemDto> addDocument(
            @PathVariable UUID reservationId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPersonal", defaultValue = "false") boolean isPersonal
    );

    @GetMapping("/documents/{documentId}")
    ResponseEntity<InputStreamResource> streamDocument(
            @PathVariable UUID reservationId,
            @PathVariable UUID documentId
    ) throws IOException;

    @DeleteMapping("/documents/{documentId}")
    ResponseEntity<Void> deleteDocument(
            @PathVariable UUID reservationId,
            @PathVariable UUID documentId
    );
}
