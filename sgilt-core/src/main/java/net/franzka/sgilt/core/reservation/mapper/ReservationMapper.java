package net.franzka.sgilt.core.reservation.mapper;

import java.time.LocalDateTime;
import net.franzka.sgilt.core.prestataire.domain.MediaUtils;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.dto.ActiveReservationItemDto;
import net.franzka.sgilt.core.reservation.dto.AdminReservationListItemDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationDetailDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationSummaryDto;
import net.franzka.sgilt.core.reservation.dto.ReservationMetaDto;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
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
    @ValueMapping(source = "CANCELED_BY_CLIENT_POST_CONFIRMATION",   target = "annulee")
    @ValueMapping(source = "CANCELED_BY_PRO_POST_CONFIRMATION",      target = "annulee")
    String mapStatus(ReservationStatus status);

    @Mapping(source = "prestataire.id",          target = "prestataireId")
    @Mapping(source = "prestataire.name",        target = "prestataireName")
    @Mapping(source = "prestataire",             target = "prestatairePhoto", qualifiedByName = "resolveAvatar")
    @Mapping(source = "prestataire.categoryKey", target = "category")
    @Mapping(source = "prestataire.subcatKey",   target = "subcatKey")
    @Mapping(target = "unreadNotesCount",        constant = "0")
    ReservationSummaryDto toSummaryDto(Reservation reservation);

    @Mapping(source = "id",                      target = "reservationId")
    @Mapping(source = "evenement.id",            target = "evenementId")
    @Mapping(source = "evenement.title",         target = "evenementTitle")
    @Mapping(source = "prestataire.slug",        target = "prestataireSlug")
    @Mapping(source = "prestataire.name",        target = "prestataireName")
    @Mapping(source = "prestataire",             target = "prestataireAvatar", qualifiedByName = "resolveAvatar")
    @Mapping(source = "status",                  target = "status")
    ActiveReservationItemDto toActiveItemDto(Reservation reservation);

    @Mapping(source = "evenement.title",     target = "evenementTitre")
    @Mapping(source = "evenement.eventType", target = "evenementType")
    @Mapping(source = "evenement.imagePath", target = "image")
    @Mapping(source = "date",                target = "datePrestation")
    @Mapping(source = "status",              target = "statut")
    @Mapping(target = "unreadNotesCount",    constant = "0")
    ProReservationSummaryDto toProReservationSummaryDto(Reservation reservation);

    @Mapping(source = "reservation.status",                     target = "statut")
    @Mapping(source = "reservation.prestataire.categoryKey",    target = "category")
    @Mapping(source = "reservation.prestataire.name",           target = "prestataireName")
    @Mapping(source = "reservation.prestataire",                target = "prestatairePhoto", qualifiedByName = "resolveAvatar")
    @Mapping(source = "reservation.evenement.title",            target = "evenementTitre")
    @Mapping(source = "reservation.evenement.eventType",        target = "evenementType")
    @Mapping(source = "reservation.evenement.imagePath",        target = "evenementImagePath")
    @Mapping(source = "reservation.evenement.date",             target = "evenementDate")
    @Mapping(source = "reservation.evenement.ville",            target = "evenementVille")
    @Mapping(source = "reservation.evenement.id",               target = "evenementId")
    @Mapping(source = "reservation.evenement.lieu",             target = "evenementLieu")
    @Mapping(source = "reservation.evenement.nbInvites",        target = "evenementNbInvites")
    @Mapping(source = "reservation.evenement.ambiance",         target = "evenementAmbiance")
    @Mapping(source = "reservation.evenement.momentCle",        target = "evenementMomentCle")
    @Mapping(source = "reservation.evenement.description",      target = "evenementDescription")
    @Mapping(source = "reservation.evenement.notePartagee",     target = "evenementNotePartagee")
    @Mapping(source = "lastUpdateDate",                         target = "evenementLastUpdateDate")
    @Mapping(source = "reservation.utilisateur.firstName",      target = "clientFirstName")
    @Mapping(source = "reservation.utilisateur.lastName",       target = "clientLastName")
    @Mapping(source = "reservation.utilisateur.phone",          target = "clientPhone")
    @Mapping(source = "reservation.utilisateur.email",          target = "clientEmail")
    ProReservationDetailDto toProReservationDetailDto(Reservation reservation, LocalDateTime lastUpdateDate);

    @Mapping(source = "prestataire.id",          target = "prestataireId")
    @Mapping(source = "prestataire.name",        target = "prestataireName")
    @Mapping(source = "prestataire.slug",        target = "prestataireSlug")
    @Mapping(source = "prestataire",             target = "prestatairePhoto", qualifiedByName = "resolveAvatar")
    @Mapping(source = "prestataire.categoryKey", target = "category")
    @Mapping(source = "status",                  target = "status")
    @Mapping(target = "unreadNotesCount",        constant = "0")
    ReservationMetaDto toReservationMetaDto(Reservation reservation);

    @Mapping(source = "evenement.title",         target = "eventTitle")
    @Mapping(source = "utilisateur.email",       target = "organizerEmail")
    @Mapping(source = "prestataire.utilisateur.email", target = "providerEmail")
    @Mapping(source = "prestataire.slug",        target = "providerSlug")
    @Mapping(source = "status",                  target = "status")
    AdminReservationListItemDto toAdminListItemDto(Reservation reservation);

    /**
     * Retourne l'avatar du prestataire, ou l'image hero (fallback).
     */
    @Named("resolveAvatar")
    default String resolveAvatar(Prestataire prestataire) {
        if (prestataire.getAvatar() != null) return prestataire.getAvatar();
        return MediaUtils.heroImageRef(prestataire.getMedias());
    }
}
