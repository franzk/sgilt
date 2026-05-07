package net.franzka.sgilt.core.reservation.mapper;

import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @ValueMapping(source = "NEW",                                    target = "nouvelle")
    @ValueMapping(source = "IN_DISCUSSION",                          target = "en_discussion")
    @ValueMapping(source = "CONFIRMED",                              target = "confirmee")
    @ValueMapping(source = "DONE",                                   target = "realisee")
    @ValueMapping(source = "REFUSED_PRE_CONTACT",                    target = "refusee")
    @ValueMapping(source = "REFUSED_POST_CONTACT",                   target = "refusee")
    @ValueMapping(source = "CANCELED_BY_CLIENT_PRE_CONTACT",         target = "annulee")
    @ValueMapping(source = "CANCELED_BY_CLIENT_POST_CONTACT",        target = "annulee")
    @ValueMapping(source = "CANCELED_POST_CONFIRMATION",             target = "annulee")
    String mapStatus(ReservationStatus status);

    @Mapping(source = "prestataire.id",          target = "prestataireId")
    @Mapping(source = "prestataire.name",        target = "prestataireName")
    @Mapping(source = "prestataire.avatar",      target = "prestatairePhoto")
    @Mapping(source = "prestataire.categoryKey", target = "category")
    @Mapping(target = "unreadNotesCount",        constant = "0")
    ReservationSummaryDto toSummaryDto(Reservation reservation);
}
