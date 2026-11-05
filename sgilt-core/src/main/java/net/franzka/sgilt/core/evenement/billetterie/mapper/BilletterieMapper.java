package net.franzka.sgilt.core.evenement.billetterie.mapper;

import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;
import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingSettingsDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = Commande.class)
public interface BilletterieMapper {

    /**
     * Mappe une billetterie vers ce qu'en voit le public.
     *
     * @param billetterie      la billetterie
     * @param remainingTickets ses places restantes, calculées par le service
     * @return la billetterie publique
     */
    @Mapping(source = "billetterie.status",         target = "status")
    @Mapping(source = "billetterie.unitPriceCents", target = "unitPriceCents")
    @Mapping(source = "remainingTickets",           target = "remainingTickets")
    @Mapping(target = "maxPlacesPerCommande",       expression = "java(Commande.MAX_PLACES_PER_COMMANDE)")
    TicketingDto toTicketingDto(Billetterie billetterie, int remainingTickets);

    /**
     * Mappe une billetterie vers ses réglages, tels que les saisit son organisateur.
     *
     * @param billetterie la billetterie
     * @return les réglages de la billetterie
     */
    TicketingSettingsDto toTicketingSettingsDto(Billetterie billetterie);

    /**
     * Mappe les réglages saisis par l'organisateur vers une nouvelle billetterie, rattachée à son
     * événement.
     *
     * @param ticketing la billetterie saisie
     * @param evenement son événement
     * @return l'entité à enregistrer
     */
    @Mapping(target = "id",             ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "evenement",      source = "evenement")
    @Mapping(target = "status",         source = "ticketing.status")
    @Mapping(target = "unitPriceCents", source = "ticketing.unitPriceCents")
    @Mapping(target = "capacity",       source = "ticketing.capacity")
    Billetterie toBilletterie(TicketingSettingsDto ticketing, Evenement evenement);
}
