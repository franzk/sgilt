package net.franzka.sgilt.core.evenement.billetterie.commande.mapper;

import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CommandeDto;
import net.franzka.sgilt.core.evenement.billetterie.commande.dto.CreateCommandeRequest;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
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
    Commande toCommande(CreateCommandeRequest request, Billetterie billetterie, LocalDateTime expiresAt);

    /**
     * Mappe une commande vers ce qu'en voit l'acheteur.
     *
     * @param commande la commande
     * @return la commande, avec son montant total
     */
    @Mapping(target = "totalCents", expression = "java(commande.getQuantity() * commande.getUnitPriceCents())")
    CommandeDto toCommandeDto(Commande commande);
}
