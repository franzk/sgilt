package net.franzka.sgilt.core.evenement.api;

import net.franzka.sgilt.core.evenement.dto.AddReservationRequest;
import net.franzka.sgilt.core.evenement.dto.CoverSelectDto;
import net.franzka.sgilt.core.evenement.dto.CoverUrlDto;
import net.franzka.sgilt.core.evenement.dto.EventCountsDto;
import net.franzka.sgilt.core.evenement.dto.EventDetailDto;
import net.franzka.sgilt.core.evenement.dto.EventPatchDto;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.dto.JournalEvenementDto;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequestMapping("api/v1/events")
public interface EvenementApi {

    @GetMapping
    ResponseEntity<List<EvenementSummaryDto>> getMyEvents();

    @GetMapping("/{eventId}")
    ResponseEntity<EventDetailDto> getEventDetail(@PathVariable UUID eventId);

    @PatchMapping("/{eventId}")
    ResponseEntity<EventDetailDto> patchEvent(@PathVariable UUID eventId, @RequestBody EventPatchDto patch);

    @GetMapping("/{eventId}/counts")
    ResponseEntity<EventCountsDto> getEventCounts(@PathVariable UUID eventId);

    @GetMapping("/{eventId}/journal")
    ResponseEntity<Page<JournalEvenementDto>> getJournal(
            @PathVariable UUID eventId,
            @RequestParam(defaultValue = "0") int page
    );

    @PatchMapping(value = "/{eventId}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<CoverUrlDto> uploadCover(
            @PathVariable UUID eventId,
            @RequestPart("file") MultipartFile file
    );

    @PatchMapping("/{eventId}/cover/select")
    ResponseEntity<CoverUrlDto> selectCover(
            @PathVariable UUID eventId,
            @RequestBody CoverSelectDto body
    );

    @PostMapping("/{eventId}/reservations")
    ResponseEntity<Void> addReservation(
            @PathVariable UUID eventId,
            @RequestBody AddReservationRequest body
    );
}
