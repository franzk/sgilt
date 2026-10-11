package net.franzka.sgilt.core.reservation.api;

import net.franzka.sgilt.core.reservation.dto.ActiveReservationsDto;
import net.franzka.sgilt.core.reservation.dto.CancelReservationRequest;
import net.franzka.sgilt.core.reservation.dto.ReservationMetaDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("api/v1/user/reservations")
public interface ClientReservationApi {

    @GetMapping("/active")
    ResponseEntity<ActiveReservationsDto> getActive();

    @GetMapping("/{reservationId}")
    ResponseEntity<ReservationMetaDto> getDetail(@PathVariable UUID reservationId);

    @PostMapping("/{reservationId}/mark-contacted")
    ResponseEntity<Void> markContacted(@PathVariable UUID reservationId);

    @PostMapping("/{reservationId}/confirm")
    ResponseEntity<Void> confirm(@PathVariable UUID reservationId);

    @PostMapping("/{reservationId}/cancel")
    ResponseEntity<Void> cancel(@PathVariable UUID reservationId, @RequestBody CancelReservationRequest body);
}
