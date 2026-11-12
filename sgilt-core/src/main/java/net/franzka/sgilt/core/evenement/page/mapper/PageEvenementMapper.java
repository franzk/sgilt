package net.franzka.sgilt.core.evenement.page.mapper;

import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenement;
import net.franzka.sgilt.core.evenement.page.dto.PageContentDto;
import net.franzka.sgilt.core.evenement.page.dto.EvenementInfosDto;
import net.franzka.sgilt.core.evenement.page.dto.EventPageDto;
import net.franzka.sgilt.core.evenement.page.dto.OrganizerPageDto;
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
    @Mapping(source = "page.evenement.eventType",  target = "eventType")
    @Mapping(source = "page.evenement.date",       target = "date")
    @Mapping(source = "startsAt",                  target = "startsAt")
    @Mapping(source = "endsAt",                    target = "endsAt")
    @Mapping(source = "page.evenement.lieu",       target = "venue")
    @Mapping(source = "page.evenement.ville",      target = "city")
    @Mapping(target = "mapsUrl", expression = "java(MapsUrlUtils.mapsUrl(page.getLocalisation()))")
    @Mapping(source = "page.sectionTitle",         target = "sectionTitle")
    @Mapping(source = "page.description",          target = "description")
    @Mapping(source = "ticketing",                 target = "ticketing")
    @Mapping(source = "page.importantInfo",        target = "importantInfo")
    @Mapping(source = "page.pictos",               target = "pictos")
    @Mapping(source = "page.practicalInfo",        target = "practicalInfo")
    EventPageDto toEventPageDto(PageEvenement page, TicketingDto ticketing, LocalDateTime startsAt, LocalDateTime endsAt);

    /**
     * Mappe une page d'événement vers son contenu, tel que le saisit son organisateur.
     *
     * @param page la page
     * @return le contenu de la page
     */
    PageContentDto toPageContentDto(PageEvenement page);

    /**
     * Mappe une page d'événement vers ce qu'en administre son organisateur : son contenu et son
     * état (publiée, slug verrouillé).
     *
     * @param page la page
     * @return la page de l'organisateur
     */
    @Mapping(target = "content",    source = "page")
    @Mapping(target = "published",  expression = "java(page.getPublishedAt() != null)")
    @Mapping(target = "slugLocked", expression = "java(page.getFirstPublishedAt() != null)")
    OrganizerPageDto toOrganizerPageDto(PageEvenement page);

    /**
     * Mappe le contenu d'une page saisi par l'organisateur vers une nouvelle entité, en brouillon,
     * rattachée à son événement.
     *
     * @param page      la page saisie
     * @param evenement son événement
     * @return l'entité à enregistrer
     */
    @Mapping(target = "id",            ignore = true)
    @Mapping(target = "createdAt",        ignore = true)
    @Mapping(target = "publishedAt",      ignore = true)
    @Mapping(target = "firstPublishedAt", ignore = true)
    @Mapping(target = "evenement",     source = "evenement")
    @Mapping(target = "slug",          source = "page.slug")
    @Mapping(target = "tag",           source = "page.tag")
    @Mapping(target = "subtitle",      source = "page.subtitle")
    @Mapping(target = "sectionTitle",  source = "page.sectionTitle")
    @Mapping(target = "description",   source = "page.description")
    @Mapping(target = "localisation",  source = "page.localisation")
    @Mapping(target = "importantInfo", source = "page.importantInfo", defaultExpression = "java(java.util.List.of())")
    @Mapping(target = "practicalInfo", source = "page.practicalInfo", defaultExpression = "java(java.util.List.of())")
    @Mapping(target = "pictos",        source = "page.pictos",        defaultExpression = "java(java.util.List.of())")
    PageEvenement toPageEvenement(PageContentDto page, Evenement evenement);

    /**
     * Mappe les infos d'un événement, pour l'e-billet d'une commande.
     *
     * @param evenement l'événement
     * @param page      sa page, ou null s'il n'en a pas
     * @param startsAt  le début de l'événement, calculé par le service, ou null
     * @param endsAt    la fin de l'événement, calculée par le service, ou null
     * @return les infos de l'événement
     */
    @Mapping(source = "page.tag",           target = "tag")
    @Mapping(source = "page.subtitle",      target = "subtitle")
    @Mapping(source = "evenement.date",     target = "date")
    @Mapping(source = "startsAt",           target = "startsAt")
    @Mapping(source = "endsAt",             target = "endsAt")
    @Mapping(source = "evenement.lieu",     target = "venue")
    @Mapping(source = "evenement.ville",    target = "city")
    @Mapping(target = "mapsUrl", expression = "java(page != null ? MapsUrlUtils.mapsUrl(page.getLocalisation()) : null)")
    // Sans page, aucune info pratique (et pas null) : un defaultExpression ne s'applique pas quand
    // c'est la page elle-même qui manque.
    @Mapping(target = "practicalInfo", expression = "java(page != null ? page.getPracticalInfo() : java.util.List.of())")
    EvenementInfosDto toEvenementInfosDto(Evenement evenement, PageEvenement page, LocalDateTime startsAt, LocalDateTime endsAt);
}
