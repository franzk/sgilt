package net.franzka.sgilt.core.reservation.api;

import net.franzka.sgilt.core.reservation.dto.AddNoteRequest;
import net.franzka.sgilt.core.reservation.dto.FeedItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
