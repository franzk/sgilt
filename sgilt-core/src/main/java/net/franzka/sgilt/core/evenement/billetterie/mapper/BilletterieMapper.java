package net.franzka.sgilt.core.evenement.billetterie.mapper;

import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.billetterie.dto.AdminTicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
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
    TicketingDto toTicketingDto(Billetterie billetterie, int remainingTickets);

    /**
     * Mappe une billetterie vers sa saisie dans le back-office admin.
     *
     * @param billetterie la billetterie
     * @return la billetterie telle que saisie
     */
    AdminTicketingDto toAdminTicketingDto(Billetterie billetterie);

    /**
     * Mappe la saisie admin d'une billetterie vers une nouvelle entité, rattachée à son événement.
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
    Billetterie toBilletterie(AdminTicketingDto ticketing, Evenement evenement);
}
