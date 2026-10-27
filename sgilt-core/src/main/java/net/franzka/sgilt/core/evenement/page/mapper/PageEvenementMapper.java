package net.franzka.sgilt.core.evenement.page.mapper;

import net.franzka.sgilt.core.evenement.billetterie.dto.AdminTicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenement;
import net.franzka.sgilt.core.evenement.page.dto.AdminPageDto;
import net.franzka.sgilt.core.evenement.page.dto.AdminEventPublicFormDto;
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

    /**
     * Mappe la face publique d'un événement vers sa saisie dans le back-office admin : ses
     * horaires, sa page et sa billetterie.
     *
     * @param evenement l'événement
     * @param page      sa page, ou null s'il n'en a pas
     * @param ticketing sa billetterie, ou null s'il n'en a pas
     * @return la face publique telle que saisie
     */
    @Mapping(source = "evenement.startTime", target = "startTime")
    @Mapping(source = "evenement.endTime",   target = "endTime")
    @Mapping(source = "page",                target = "page")
    @Mapping(source = "ticketing",           target = "ticketing")
    AdminEventPublicFormDto toAdminEventPublicFormDto(Evenement evenement, PageEvenement page, AdminTicketingDto ticketing);

    /**
     * Mappe une page d'événement vers sa saisie dans le back-office admin.
     *
     * @param page la page
     * @return la page telle que saisie
     */
    AdminPageDto toAdminPageDto(PageEvenement page);

    /**
     * Mappe la saisie admin d'une page vers une nouvelle entité, rattachée à son événement.
     *
     * @param page      la page saisie
     * @param evenement son événement
     * @return l'entité à enregistrer
     */
    @Mapping(target = "id",            ignore = true)
    @Mapping(target = "createdAt",     ignore = true)
    @Mapping(target = "evenement",     source = "evenement")
    @Mapping(target = "slug",          source = "page.slug")
    @Mapping(target = "tag",           source = "page.tag")
    @Mapping(target = "subtitle",      source = "page.subtitle")
    @Mapping(target = "sectionTitle",  source = "page.sectionTitle")
    @Mapping(target = "description",   source = "page.description")
    @Mapping(target = "importantInfo", source = "page.importantInfo", defaultExpression = "java(java.util.List.of())")
    @Mapping(target = "practicalInfo", source = "page.practicalInfo", defaultExpression = "java(java.util.List.of())")
    @Mapping(target = "pictos",        source = "page.pictos",        defaultExpression = "java(java.util.List.of())")
    PageEvenement toPageEvenement(AdminPageDto page, Evenement evenement);
}
