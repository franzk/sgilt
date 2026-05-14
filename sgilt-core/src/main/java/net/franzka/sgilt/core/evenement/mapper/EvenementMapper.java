package net.franzka.sgilt.core.evenement.mapper;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.dto.ClientInfoDto;
import net.franzka.sgilt.core.evenement.dto.EventDetailDto;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
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
    @Mapping(source = "counts.confirmedCount",      target = "confirmedCount")
    @Mapping(source = "counts.inDiscussionCount",   target = "inDiscussionCount")
    @Mapping(source = "coverUrl",                   target = "coverUrl")
    EvenementSummaryDto toSummaryDto(Evenement evenement, ReservationCounts counts, String coverUrl);

    ClientInfoDto toClientInfo(Utilisateur utilisateur);

    @Mapping(source = "evenement.notePartagee", target = "sharedNote")
    @Mapping(source = "evenement.description",  target = "description")
    @Mapping(source = "evenement.momentCle",    target = "momentCle")
    @Mapping(source = "evenement.utilisateur",  target = "clientInfo")
    @Mapping(source = "countdown",              target = "countdown")
    @Mapping(source = "lastUpdateDate",         target = "lastUpdateDate")
    @Mapping(source = "coverUrl",               target = "coverUrl")
    EventDetailDto toDetailDto(Evenement evenement, String countdown, LocalDateTime lastUpdateDate, String coverUrl);
}
