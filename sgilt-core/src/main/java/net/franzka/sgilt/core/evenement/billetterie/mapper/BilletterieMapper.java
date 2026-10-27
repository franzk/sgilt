package net.franzka.sgilt.core.evenement.billetterie.mapper;

import net.franzka.sgilt.core.evenement.billetterie.domain.Billetterie;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
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
}
