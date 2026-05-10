package net.franzka.sgilt.core.evenement.api;

import net.franzka.sgilt.core.evenement.dto.EventCountsDto;
import net.franzka.sgilt.core.evenement.dto.EventDetailDto;
import net.franzka.sgilt.core.evenement.dto.EventPatchDto;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/{eventId}/reservations")
    ResponseEntity<List<ReservationSummaryDto>> getEventReservations(@PathVariable UUID eventId);
}
