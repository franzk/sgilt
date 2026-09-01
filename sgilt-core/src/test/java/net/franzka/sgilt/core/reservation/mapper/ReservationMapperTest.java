package net.franzka.sgilt.core.reservation.mapper;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.dto.ActiveReservationItemDto;
import net.franzka.sgilt.core.reservation.dto.AdminReservationListItemDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationDetailDto;
import net.franzka.sgilt.core.reservation.dto.ProReservationSummaryDto;
import net.franzka.sgilt.core.reservation.dto.ReservationMetaDto;
import net.franzka.sgilt.core.reservation.dto.ReservationSummaryDto;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationMapperTest {

    private final ReservationMapper mapper = new ReservationMapperImpl();

    private final Utilisateur prestataireUtilisateur = Utilisateur.builder()
            .id(UUID.randomUUID()).email("presta@sgilt.fr").build();
    private final Prestataire prestataire = Prestataire.builder()
            .id(UUID.randomUUID()).utilisateur(prestataireUtilisateur)
            .name("Studio Fleur").slug("studio-fleur").categoryKey("photo").avatar("avatar.jpg").build();
    private final Utilisateur client = Utilisateur.builder()
            .firstName("Sophie").lastName("Leroy").email("sophie@sgilt.fr").phone("0102030405").build();
    private final Evenement evenement = Evenement.builder()
            .id(UUID.randomUUID()).title("Anniversaire de Paul").eventType("Anniversaire")
            .imagePath("cover.jpg").date(LocalDate.of(2027, 6, 15)).ville("Lyon").build();
    private final UUID reservationId = UUID.randomUUID();
    private final LocalDateTime createdAt = LocalDateTime.now();

    private final Reservation reservation = Reservation.builder()
            .id(reservationId).prestataire(prestataire).evenement(evenement).utilisateur(client)
            .date(LocalDate.of(2027, 6, 15)).status(ReservationStatus.CONFIRMED).createdAt(createdAt)
            .build();

    // -------------------------------------------------------------------------
    // mapStatus
    // -------------------------------------------------------------------------

    @Nested
    class MapStatus {

        @Test
        void givenEachReservationStatus_whenMapStatus_thenReturnsExpectedFrenchLabel() {
            Map<ReservationStatus, String> expected = new EnumMap<>(ReservationStatus.class);
            expected.put(ReservationStatus.NEW, "nouvelle");
            expected.put(ReservationStatus.IN_DISCUSSION, "en_discussion");
            expected.put(ReservationStatus.CONFIRMED, "confirmee");
            expected.put(ReservationStatus.DONE, "realisee");
            expected.put(ReservationStatus.REFUSED_PRE_CONTACT, "refusee");
            expected.put(ReservationStatus.REFUSED_POST_CONTACT, "refusee");
            expected.put(ReservationStatus.CANCELED_BY_CLIENT_PRE_CONTACT, "annulee");
            expected.put(ReservationStatus.CANCELED_BY_CLIENT_POST_CONTACT, "annulee");
            expected.put(ReservationStatus.CANCELED_BY_CLIENT_POST_CONFIRMATION, "annulee");
            expected.put(ReservationStatus.CANCELED_BY_PRO_POST_CONFIRMATION, "annulee");

            expected.forEach((status, label) -> assertThat(mapper.mapStatus(status)).as(status.name()).isEqualTo(label));
        }
    }

    // -------------------------------------------------------------------------
    // resolveAvatar
    // -------------------------------------------------------------------------

    @Nested
    class ResolveAvatar {

        @Test
        void givenAvatarSet_whenResolveAvatar_thenReturnsAvatar() {
            assertThat(mapper.resolveAvatar(prestataire)).isEqualTo("avatar.jpg");
        }

        @Test
        void givenNoAvatarWithHeroMedia_whenResolveAvatar_thenReturnsHeroImageRef() {
            Prestataire noAvatar = Prestataire.builder()
                    .medias("[{\"type\":\"IMAGE\",\"ref\":\"hero.jpg\",\"position\":0}]").build();

            assertThat(mapper.resolveAvatar(noAvatar)).isEqualTo("hero.jpg");
        }

        @Test
        void givenNoAvatarNoMedia_whenResolveAvatar_thenReturnsNull() {
            Prestataire noAvatarNoMedia = Prestataire.builder().build();

            assertThat(mapper.resolveAvatar(noAvatarNoMedia)).isNull();
        }
    }

    // -------------------------------------------------------------------------
    // toSummaryDto
    // -------------------------------------------------------------------------

    @Nested
    class ToSummaryDto {

        @Test
        void givenFullReservation_whenToSummaryDto_thenMapsAllFields() {
            ReservationSummaryDto dto = mapper.toSummaryDto(reservation);

            assertThat(dto).isEqualTo(new ReservationSummaryDto(
                    reservationId, prestataire.getId(), "Studio Fleur", "avatar.jpg", "photo", "confirmee", 0));
        }
    }

    // -------------------------------------------------------------------------
    // toActiveItemDto
    // -------------------------------------------------------------------------

    @Nested
    class ToActiveItemDto {

        @Test
        void givenFullReservation_whenToActiveItemDto_thenMapsAllFields() {
            ActiveReservationItemDto dto = mapper.toActiveItemDto(reservation);

            assertThat(dto).isEqualTo(new ActiveReservationItemDto(
                    reservationId, "confirmee", evenement.getId(), "Anniversaire de Paul",
                    "studio-fleur", "Studio Fleur", "avatar.jpg"));
        }
    }

    // -------------------------------------------------------------------------
    // toProReservationSummaryDto
    // -------------------------------------------------------------------------

    @Nested
    class ToProReservationSummaryDto {

        @Test
        void givenFullReservation_whenToProReservationSummaryDto_thenMapsAllFields() {
            ProReservationSummaryDto dto = mapper.toProReservationSummaryDto(reservation);

            assertThat(dto).isEqualTo(new ProReservationSummaryDto(
                    reservationId, "Anniversaire de Paul", "Anniversaire", "cover.jpg",
                    LocalDate.of(2027, 6, 15), "confirmee", 0));
        }
    }

    // -------------------------------------------------------------------------
    // toProReservationDetailDto
    // -------------------------------------------------------------------------

    @Nested
    class ToProReservationDetailDto {

        @Test
        void givenFullReservation_whenToProReservationDetailDto_thenMapsAllFields() {
            ProReservationDetailDto dto = mapper.toProReservationDetailDto(reservation);

            assertThat(dto).isEqualTo(new ProReservationDetailDto(
                    reservationId, "confirmee", "photo", "Studio Fleur", "avatar.jpg",
                    "Anniversaire de Paul", "Anniversaire", "cover.jpg", LocalDate.of(2027, 6, 15), "Lyon",
                    "Sophie", "Leroy", "0102030405", "sophie@sgilt.fr"));
        }
    }

    // -------------------------------------------------------------------------
    // toReservationMetaDto
    // -------------------------------------------------------------------------

    @Nested
    class ToReservationMetaDto {

        @Test
        void givenFullReservation_whenToReservationMetaDto_thenMapsAllFields() {
            ReservationMetaDto dto = mapper.toReservationMetaDto(reservation);

            assertThat(dto).isEqualTo(new ReservationMetaDto(
                    reservationId, prestataire.getId(), "Studio Fleur", "avatar.jpg", "photo", "confirmee", 0));
        }
    }

    // -------------------------------------------------------------------------
    // toAdminListItemDto
    // -------------------------------------------------------------------------

    @Nested
    class ToAdminListItemDto {

        @Test
        void givenFullReservation_whenToAdminListItemDto_thenMapsAllFields() {
            AdminReservationListItemDto dto = mapper.toAdminListItemDto(reservation);

            assertThat(dto).isEqualTo(new AdminReservationListItemDto(
                    reservationId, "Anniversaire de Paul", "sophie@sgilt.fr", "presta@sgilt.fr",
                    "studio-fleur", ReservationStatus.CONFIRMED, createdAt));
        }
    }
}
