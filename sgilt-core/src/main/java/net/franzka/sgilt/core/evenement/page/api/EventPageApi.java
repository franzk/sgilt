package net.franzka.sgilt.core.evenement.page.api;

import net.franzka.sgilt.core.evenement.page.dto.EventPageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/v1/eventpages")
public interface EventPageApi {

    @GetMapping("/{slug}")
    ResponseEntity<EventPageDto> getEventPage(@PathVariable String slug);
}
