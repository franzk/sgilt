package net.franzka.sgilt.core.evenement.page.api;

import jakarta.validation.Valid;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.OrganizerCommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingSettingsDto;
import net.franzka.sgilt.core.evenement.page.dto.EventPageDto;
import net.franzka.sgilt.core.evenement.page.dto.OrganizerPublicPageDto;
import net.franzka.sgilt.core.evenement.page.dto.PageContentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping("api/v1/user/events/{eventId}/page-publique")
public interface OrganizerPageApi {

    @GetMapping
    ResponseEntity<OrganizerPublicPageDto> getPublicPage(@PathVariable UUID eventId);

    @GetMapping("/apercu")
    ResponseEntity<EventPageDto> getPreview(@PathVariable UUID eventId);

    @PutMapping("/contenu")
    ResponseEntity<OrganizerPublicPageDto> putContent(@PathVariable UUID eventId, @RequestBody @Valid PageContentDto content);

    @PostMapping("/publier")
    ResponseEntity<OrganizerPublicPageDto> publish(@PathVariable UUID eventId);

    @PostMapping("/depublier")
    ResponseEntity<OrganizerPublicPageDto> unpublish(@PathVariable UUID eventId);

    @PutMapping("/billetterie")
    ResponseEntity<OrganizerPublicPageDto> putTicketing(@PathVariable UUID eventId, @RequestBody @Valid TicketingSettingsDto ticketing);

    @GetMapping("/billetterie/commandes")
    ResponseEntity<List<OrganizerCommandeDto>> getCommandes(@PathVariable UUID eventId);
}
