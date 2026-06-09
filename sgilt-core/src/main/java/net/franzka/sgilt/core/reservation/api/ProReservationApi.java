package net.franzka.sgilt.core.reservation.api;

import net.franzka.sgilt.core.reservation.dto.ProBoardCountsDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationDetailDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationSummaryDto;
import net.franzka.sgilt.core.reservation.dto.RefuseReservationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping("api/v1/pro/reservations")
public interface ProReservationApi {

    @GetMapping
    ResponseEntity<List<ProReservationSummaryDto>> getAll();

    @GetMapping("/counts")
    ResponseEntity<ProBoardCountsDto> getCounts();

    @GetMapping("/{reservationId}")
    ResponseEntity<ProReservationDetailDto> getDetail(@PathVariable UUID reservationId);

    @PostMapping("/{reservationId}/mark-contacted")
    ResponseEntity<Void> markContacted(@PathVariable UUID reservationId);

    @PostMapping("/{reservationId}/confirm")
    ResponseEntity<Void> confirm(@PathVariable UUID reservationId);

    @PostMapping("/{reservationId}/refuse")
    ResponseEntity<Void> refuse(@PathVariable UUID reservationId, @RequestBody RefuseReservationRequest body);

    @PostMapping("/{reservationId}/cancel")
    ResponseEntity<Void> cancelByPro(@PathVariable UUID reservationId);
}
