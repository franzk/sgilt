package net.franzka.sgilt.core.evenement.api;

import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("api/v1/events")
public interface EvenementApi {

    @GetMapping
    ResponseEntity<List<EvenementSummaryDto>> getMyEvents(@AuthenticationPrincipal Jwt jwt);
}
