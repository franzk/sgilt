package net.franzka.sgilt.core.evenement.billetterie.commande.api;

import jakarta.validation.Valid;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeSummaryDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CreateCommandeRequest;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.EBilletDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("api/v1/commandes")
public interface CommandeApi {

    @PostMapping
    ResponseEntity<CommandeDto> createCommande(@RequestBody @Valid CreateCommandeRequest request);

    @GetMapping("{id}")
    ResponseEntity<CommandeSummaryDto> getCommande(@PathVariable UUID id);

    @GetMapping("{id}/e-billet")
    ResponseEntity<EBilletDto> getEBillet(@PathVariable UUID id);

    @GetMapping(value = "{id}/e-billet.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    ResponseEntity<byte[]> getEBilletPdf(@PathVariable UUID id);
}
