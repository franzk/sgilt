package net.franzka.sgilt.core.evenement.billetterie.commande.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.evenement.billetterie.commande.api.CommandeApi;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeSummaryDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CreateCommandeRequest;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.EBilletDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.ebillet.EBilletPdf;
import net.franzka.sgilt.core.evenement.billetterie.commande.ebillet.EBilletService;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.CommandeNotFoundException;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.CommandeNotPaidException;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.EBilletPdfException;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.NotEnoughTicketsException;
import net.franzka.sgilt.core.evenement.billetterie.commande.service.CommandeService;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotFoundException;
import net.franzka.sgilt.core.evenement.billetterie.exception.BilletterieNotOpenException;
import net.franzka.sgilt.core.evenement.billetterie.service.BilletterieService;
import net.franzka.sgilt.core.evenement.page.exception.PageEvenementNotFoundException;
import net.franzka.sgilt.core.evenement.page.service.PageEvenementService;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller HTTP des commandes de places. Endpoints publics, sans authentification : on achète
 * des places sans compte. La création enchaîne les trois domaines : la page (slug → événement), la
 * billetterie (verrou, statut), la commande (places restantes, création). L'e-billet, en JSON pour
 * sa page ou en PDF, est délégué à EBilletService.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class CommandeController implements CommandeApi {

    private static final String X_ROBOTS_TAG_HEADER = "X-Robots-Tag";
    // Mêmes directives que la routeRule de la page e-billet côté front.
    private static final String X_ROBOTS_TAG_NO_INDEX = "noindex, nofollow, noarchive";

    private final PageEvenementService pageEvenementService;
    private final BilletterieService   billetterieService;
    private final CommandeService      commandeService;
    private final EBilletService       eBilletService;

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

    /**
     * Relit une commande, pour l'écran de confirmation (qui attend son passage PAID).
     *
     * @param id l'identifiant de la commande
     * @return 200 OK avec la commande
     * @throws CommandeNotFoundException si la commande est introuvable
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<CommandeSummaryDto> getCommande(UUID id) {
        log.info("GET /commandes/{}", id);
        return ResponseEntity.ok(commandeService.getCommande(id));
    }

    /**
     * Retourne l'e-billet d'une commande payée, pour sa page publique.
     *
     * @param id l'identifiant de la commande
     * @return 200 OK avec l'événement, ses infos, le nombre de places et le QR de la commande
     * @throws CommandeNotFoundException si la commande est introuvable
     * @throws CommandeNotPaidException  si la commande n'est pas payée
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<EBilletDto> getEBillet(UUID id) {
        log.info("GET /commandes/{}/e-billet", id);
        return ResponseEntity.ok(eBilletService.getEBillet(id));
    }

    /**
     * Retourne le PDF de l'e-billet d'une commande payée, à télécharger. Ni mis en cache ni indexé :
     * l'URL suffit à l'ouvrir, comme la page e-billet.
     *
     * @param id l'identifiant de la commande
     * @return 200 OK avec le PDF, en pièce jointe
     * @throws CommandeNotFoundException si la commande est introuvable
     * @throws CommandeNotPaidException  si la commande n'est pas payée
     * @throws EBilletPdfException       si la génération du PDF échoue
     */
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getEBilletPdf(UUID id) {
        log.info("GET /commandes/{}/e-billet.pdf", id);
        EBilletPdf pdf = eBilletService.getEBilletPdf(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(pdf.fileName()).build().toString())
                .cacheControl(CacheControl.noStore())
                .header(X_ROBOTS_TAG_HEADER, X_ROBOTS_TAG_NO_INDEX)
                .body(pdf.content());
    }
}
