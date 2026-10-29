package net.franzka.sgilt.core.evenement.billetterie.commande.api;

import jakarta.validation.Valid;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CreateCommandeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/v1/commandes")
public interface CommandeApi {

    @PostMapping
    ResponseEntity<CommandeDto> createCommande(@RequestBody @Valid CreateCommandeRequest request);
}
