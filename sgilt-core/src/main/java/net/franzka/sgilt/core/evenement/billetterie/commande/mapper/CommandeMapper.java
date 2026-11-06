package net.franzka.sgilt.core.evenement.billetterie.commande.mapper;

import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.EntryCodeUtils;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeSummaryDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CreateCommandeRequest;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.EBilletDto;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.page.dto.EvenementInfosDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = EntryCodeUtils.class)
public interface CommandeMapper {

    /**
     * Mappe une requête de commande vers une nouvelle commande PENDING, au prix de la billetterie.
     *
     * @param request     la requête
     * @param billetterie la billetterie
     * @param expiresAt   la fin du blocage de ses places, calculée par le service
     * @return l'entité à enregistrer
     */
    @Mapping(target = "id",             ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "billetterie",    source = "billetterie")
    @Mapping(target = "quantity",       source = "request.quantity")
    @Mapping(target = "unitPriceCents", source = "billetterie.unitPriceCents")
    @Mapping(target = "buyerFirstName", source = "request.firstName")
    @Mapping(target = "buyerLastName",  source = "request.lastName")
    @Mapping(target = "buyerEmail",     source = "request.email")
    @Mapping(target = "status",         constant = "PENDING")
    @Mapping(target = "expiresAt",      source = "expiresAt")
    @Mapping(target = "code",           ignore = true)
    Commande toCommande(CreateCommandeRequest request, Billetterie billetterie, LocalDateTime expiresAt);

    /**
     * Mappe une commande vers ce qu'en voit l'acheteur, avec de quoi la payer.
     *
     * @param commande     la commande
     * @param clientSecret le secret client de son PaymentIntent Stripe
     * @return la commande, avec son montant total
     */
    @Mapping(target = "id",           source = "commande.id")
    @Mapping(target = "quantity",     source = "commande.quantity")
    @Mapping(target = "totalCents",   expression = "java(commande.getQuantity() * commande.getUnitPriceCents())")
    @Mapping(target = "expiresAt",    source = "commande.expiresAt")
    @Mapping(target = "clientSecret", source = "clientSecret")
    CommandeDto toCommandeDto(Commande commande, String clientSecret);

    /**
     * Mappe une commande vers ce qu'en relit l'écran de confirmation.
     *
     * @param commande la commande
     * @return la commande, avec son statut et son montant total
     */
    @Mapping(target = "totalCents", expression = "java(commande.getQuantity() * commande.getUnitPriceCents())")
    CommandeSummaryDto toCommandeSummaryDto(Commande commande);

    /**
     * Mappe une commande payée vers son e-billet.
     *
     * @param commande       la commande payée, avec sa billetterie et son événement
     * @param evenementInfos les infos de l'événement
     * @param pageOnline     la page de l'événement est en ligne
     * @return l'événement, ses infos, le nombre de places et le QR de la commande
     */
    @Mapping(target = "title",          source = "commande.billetterie.evenement.title")
    @Mapping(target = "heroImage",      source = "commande.billetterie.evenement.imagePath")
    @Mapping(target = "eventType",      source = "commande.billetterie.evenement.eventType")
    @Mapping(target = "evenementInfos", source = "evenementInfos")
    @Mapping(target = "quantity",       source = "commande.quantity")
    @Mapping(target = "qrContent",      expression = "java(EntryCodeUtils.commandeQrContent(commande.getCode()))")
    @Mapping(target = "pageOnline",     source = "pageOnline")
    EBilletDto toEBilletDto(Commande commande, EvenementInfosDto evenementInfos, boolean pageOnline);
}
