package net.franzka.sgilt.core.evenement.page.mapper;

import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenement;
import net.franzka.sgilt.core.evenement.page.dto.EventPageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface PageEvenementMapper {

    /**
     * Mappe une page d'événement vers sa face publique : la page, son événement et sa billetterie.
     *
     * @param page      la page, avec son événement
     * @param ticketing la billetterie de l'événement, ou null s'il n'en a pas
     * @param startsAt  le début de l'événement, calculé par le service, ou null
     * @param endsAt    la fin de l'événement, calculée par le service, ou null
     * @return la page publique
     */
    @Mapping(source = "page.slug",                 target = "slug")
    @Mapping(source = "page.tag",                  target = "tag")
    @Mapping(source = "page.evenement.title",      target = "title")
    @Mapping(source = "page.subtitle",             target = "subtitle")
    @Mapping(source = "page.evenement.imagePath",  target = "heroImage")
    @Mapping(source = "page.evenement.date",       target = "date")
    @Mapping(source = "startsAt",                  target = "startsAt")
    @Mapping(source = "endsAt",                    target = "endsAt")
    @Mapping(source = "page.evenement.lieu",       target = "venue")
    @Mapping(source = "page.evenement.ville",      target = "city")
    @Mapping(source = "page.sectionTitle",         target = "sectionTitle")
    @Mapping(source = "page.description",          target = "description")
    @Mapping(source = "ticketing",                 target = "ticketing")
    @Mapping(source = "page.importantInfo",        target = "importantInfo")
    @Mapping(source = "page.pictos",               target = "pictos")
    @Mapping(source = "page.practicalInfo",        target = "practicalInfo")
    EventPageDto toEventPageDto(PageEvenement page, TicketingDto ticketing, LocalDateTime startsAt, LocalDateTime endsAt);
}
