package net.franzka.sgilt.core.evenement.billetterie.commande.ebillet;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.EntryCodeUtils;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.EBilletDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.CommandeNotFoundException;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.CommandeNotPaidException;
import net.franzka.sgilt.core.evenement.billetterie.commande.exception.EBilletPdfException;
import net.franzka.sgilt.core.evenement.billetterie.commande.mapper.CommandeMapper;
import net.franzka.sgilt.core.evenement.billetterie.commande.service.CommandeService;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.dto.EvenementInfosDto;
import net.franzka.sgilt.core.evenement.page.service.PageEvenementService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * E-billet d'une commande payée, pour sa page (JSON) ou à télécharger (PDF). Orchestre la commande
 * (payée) et la page de son événement (infos) : CommandeService ne peut pas appeler
 * PageEvenementService (BilletterieService dépend déjà de CommandeService), ce service-ci le peut,
 * personne parmi eux ne dépendant de lui.
 */
@Service
@RequiredArgsConstructor
public class EBilletService {

    private static final String EBILLET_PDF_FILENAME = "e-billet.pdf";

    private final CommandeService      commandeService;
    private final PageEvenementService pageEvenementService;
    private final CommandeMapper       commandeMapper;
    private final EBilletPdfService    eBilletPdfService;

    /**
     * Retourne l'e-billet d'une commande payée.
     *
     * @param commandeId l'identifiant de la commande
     * @return l'événement, ses infos, le nombre de places et le QR de la commande
     * @throws CommandeNotFoundException si la commande est introuvable
     * @throws CommandeNotPaidException  si la commande n'est pas payée
     */
    public EBilletDto getEBillet(UUID commandeId) {
        Commande commande = commandeService.getPaidCommande(commandeId);
        Evenement evenement = commande.getBilletterie().getEvenement();
        EvenementInfosDto evenementInfos = pageEvenementService.getEvenementInfos(evenement);
        String qrImage = QrCodeUtils.pngDataUri(EntryCodeUtils.commandeQrContent(commande.getCode()));
        return commandeMapper.toEBilletDto(commande, evenementInfos, pageEvenementService.isOnline(evenement), qrImage);
    }

    /**
     * Retourne le PDF de l'e-billet d'une commande payée.
     *
     * @param commandeId l'identifiant de la commande
     * @return le PDF et son nom de fichier
     * @throws CommandeNotFoundException si la commande est introuvable
     * @throws CommandeNotPaidException  si la commande n'est pas payée
     * @throws EBilletPdfException       si la génération du PDF échoue
     */
    public EBilletPdf getEBilletPdf(UUID commandeId) {
        return new EBilletPdf(eBilletPdfService.generate(getEBillet(commandeId)), EBILLET_PDF_FILENAME);
    }
}
