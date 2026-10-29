package net.franzka.sgilt.core.evenement.billetterie.commande.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.billetterie.commande.api.CommandeApi;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CreateCommandeRequest;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.NotEnoughTicketsException;
import net.franzka.sgilt.core.evenement.billetterie.commande.service.CommandeService;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotFoundException;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotOpenException;
import net.franzka.sgilt.core.evenement.billetterie.service.BilletterieService;
import net.franzka.sgilt.core.evenement.page.exception.PageEvenementNotFoundException;
import net.franzka.sgilt.core.evenement.page.service.PageEvenementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller HTTP des commandes de billets. Endpoint public, sans authentification : on achète
 * des billets sans compte. Enchaîne les trois domaines : la page (slug → événement), la
 * billetterie (verrou, statut), la commande (places restantes, création).
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class CommandeController implements CommandeApi {

    private final PageEvenementService pageEvenementService;
    private final BilletterieService   billetterieService;
    private final CommandeService      commandeService;

    /**
     * Crée une commande en attente de paiement, qui bloque ses places.
     *
     * @param request la commande demandée
     * @return 201 Created avec la commande
     * @throws PageEvenementNotFoundException si aucune page ne correspond au slug
     * @throws BilletterieNotFoundException   si l'événement n'a pas de billetterie
     * @throws BilletterieNotOpenException    si la billetterie n'est pas ouverte
     * @throws NotEnoughTicketsException      s'il ne reste pas assez de places
     */
    @Override
    @Transactional
    public ResponseEntity<CommandeDto> createCommande(CreateCommandeRequest request) {
        log.info("POST /commandes — slug={} quantity={}", request.slug(), request.quantity());
        UUID evenementId = pageEvenementService.getEvenementIdBySlug(request.slug());
        Billetterie billetterie = billetterieService.lockOpenBilletterie(evenementId);
        return ResponseEntity.status(HttpStatus.CREATED).body(commandeService.createCommande(billetterie, request));
    }
}
