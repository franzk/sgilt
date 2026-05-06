package net.franzka.sgilt.core.evenement.mapper;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.reservation.dto.ReservationCounts;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EvenementMapper {

    @Mapping(source = "evenement.id", target = "id")
    @Mapping(source = "evenement.name", target = "name")
    @Mapping(source = "evenement.date", target = "date")
    @Mapping(source = "evenement.ville", target = "ville")
    @Mapping(source = "evenement.coverUrl", target = "coverUrl")
    @Mapping(source = "evenement.eventType", target = "eventType")
    @Mapping(source = "counts.confirmedCount", target = "confirmedCount")
    @Mapping(source = "counts.inDiscussionCount", target = "inDiscussionCount")
    EvenementSummaryDto toSummaryDto(Evenement evenement, ReservationCounts counts);
}
