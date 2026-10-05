package net.franzka.sgilt.core.evenement.mapper;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.domain.EvenementRubrique;
import net.franzka.sgilt.core.evenement.dto.ClientInfoDto;
import net.franzka.sgilt.core.evenement.dto.EvenementDto;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.dto.EventMetaDto;
import net.franzka.sgilt.core.evenement.dto.RubriqueDto;
import net.franzka.sgilt.core.reservation.dto.ReservationCounts;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface EvenementMapper {

    @Mapping(source = "evenement.id",               target = "id")
    @Mapping(source = "evenement.title",            target = "title")
    @Mapping(source = "evenement.date",             target = "date")
    @Mapping(source = "evenement.ville",            target = "ville")
    @Mapping(source = "evenement.eventType",        target = "eventType")
    @Mapping(source = "evenement.imagePath",        target = "imagePath")
    @Mapping(source = "counts.confirmedCount",      target = "confirmedCount")
    @Mapping(source = "counts.inDiscussionCount",   target = "inDiscussionCount")
    EvenementSummaryDto toSummaryDto(Evenement evenement, ReservationCounts counts);

    ClientInfoDto toClientInfo(Utilisateur utilisateur);

    @Mapping(source = "evenement.notePartagee", target = "sharedNote")
    @Mapping(source = "evenement.description",  target = "description")
    @Mapping(source = "evenement.momentCle",    target = "momentCle")
    @Mapping(source = "evenement.utilisateur",  target = "clientInfo")
    @Mapping(source = "evenement.imagePath",    target = "imagePath")
    @Mapping(source = "countdown",              target = "countdown")
    @Mapping(source = "lastUpdateDate",         target = "lastUpdateDate")
    EventMetaDto toMetaDto(Evenement evenement, String countdown, LocalDateTime lastUpdateDate);

    /**
     * Mappe un événement complet (pas encore créé) vers l'entité, en statut ACTIVE, avec les
     * clés de ses rubriques. Le titre et les demandes initiées sont traités par le service.
     *
     * @param evenement   l'événement : données et rubriques
     * @param utilisateur son propriétaire
     * @return l'entité à enregistrer
     */
    @Mapping(target = "id",           ignore = true)
    @Mapping(target = "title",        ignore = true)
    @Mapping(target = "imagePath",    ignore = true)
    @Mapping(target = "notePartagee", ignore = true)
    @Mapping(target = "createdAt",    ignore = true)
    @Mapping(target = "status",       constant = "ACTIVE")
    @Mapping(target = "utilisateur",  source = "utilisateur")
    @Mapping(target = "date",         source = "evenement.date")
    @Mapping(target = "eventType",    source = "evenement.eventType")
    @Mapping(target = "ambiance",     source = "evenement.ambiance")
    @Mapping(target = "momentCle",    source = "evenement.momentCle")
    @Mapping(target = "description",  source = "evenement.description")
    @Mapping(target = "ville",        source = "evenement.ville")
    @Mapping(target = "nbInvites",    source = "evenement.nbInvites")
    @Mapping(target = "lieu",         source = "evenement.lieu")
    @Mapping(target = "rubriques",    source = "evenement.rubriques", defaultExpression = "java(java.util.List.of())")
    Evenement toEvenement(EvenementDto evenement, Utilisateur utilisateur);

    /**
     * Mappe une rubrique d'un événement complet vers la rubrique enregistrée sur l'événement (sa
     * clé : les demandes deviennent des réservations).
     *
     * @param rubrique la rubrique transmise
     * @return la rubrique de l'événement
     */
    EvenementRubrique toEvenementRubrique(RubriqueDto rubrique);
}
