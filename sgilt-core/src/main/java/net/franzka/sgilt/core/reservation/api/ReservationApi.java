package net.franzka.sgilt.core.reservation.api;

import net.franzka.sgilt.core.reservation.dto.ActiveReservationsDto;
import net.franzka.sgilt.core.reservation.dto.ProBoardCountsDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationSummaryDto;
import net.franzka.sgilt.core.reservation.dto.ReservationMetaDto;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@RequestMapping("api/v1/reservations")
public interface ReservationApi {

    @GetMapping
    ResponseEntity<List<ReservationSummaryDto>> getByEventId(@RequestParam UUID eventId);

    @GetMapping("/active")
    ResponseEntity<ActiveReservationsDto> getActive();

    @GetMapping("/pro")
    ResponseEntity<List<ProReservationSummaryDto>> getProReservations();

    @GetMapping("/pro/counts")
    ResponseEntity<ProBoardCountsDto> getProBoardCounts();

    @GetMapping("/{reservationId}")
    ResponseEntity<ReservationMetaDto> getDetail(@PathVariable UUID reservationId);

    @PostMapping("/{reservationId}/cancel")
    ResponseEntity<Void> cancel(@PathVariable UUID reservationId);
}
