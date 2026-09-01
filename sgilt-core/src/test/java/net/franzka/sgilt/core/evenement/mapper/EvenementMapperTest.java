package net.franzka.sgilt.core.evenement.mapper;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.dto.ClientInfoDto;
import net.franzka.sgilt.core.evenement.dto.EventDetailDto;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.reservation.dto.ReservationCounts;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvenementMapperTest {

    private final EvenementMapper mapper = new EvenementMapperImpl();

    // -------------------------------------------------------------------------
    // toSummaryDto
    // -------------------------------------------------------------------------

    @Nested
    class ToSummaryDto {

        @Test
        void givenEvenementAndCounts_whenToSummaryDto_thenMapsAllFields() {
            Evenement evenement = Evenement.builder()
                    .id(UUID.randomUUID()).title("Anniversaire de Paul").date(LocalDate.of(2027, 6, 15))
                    .ville("Lyon").eventType("Anniversaire").imagePath("cover.jpg").build();
            ReservationCounts counts = new ReservationCounts(2, 1, 0);

            EvenementSummaryDto dto = mapper.toSummaryDto(evenement, counts);

            assertThat(dto).isEqualTo(new EvenementSummaryDto(
                    evenement.getId(), "Anniversaire de Paul", LocalDate.of(2027, 6, 15), "Lyon",
                    "cover.jpg", "Anniversaire", 2, 1));
        }
    }

    // -------------------------------------------------------------------------
    // toClientInfo
    // -------------------------------------------------------------------------

    @Nested
    class ToClientInfo {

        @Test
        void givenUtilisateur_whenToClientInfo_thenMapsAllFields() {
            Utilisateur utilisateur = Utilisateur.builder()
                    .firstName("Sophie").lastName("Leroy").phone("0102030405").email("sophie@sgilt.fr").build();

            ClientInfoDto dto = mapper.toClientInfo(utilisateur);

            assertThat(dto).isEqualTo(new ClientInfoDto("Sophie", "Leroy", "0102030405", "sophie@sgilt.fr"));
        }
    }

    // -------------------------------------------------------------------------
    // toDetailDto
    // -------------------------------------------------------------------------

    @Nested
    class ToDetailDto {

        @Test
        void givenEvenementCountdownAndLastUpdate_whenToDetailDto_thenMapsAllFields() {
            Utilisateur utilisateur = Utilisateur.builder()
                    .firstName("Sophie").lastName("Leroy").phone("0102030405").email("sophie@sgilt.fr").build();
            Evenement evenement = Evenement.builder()
                    .id(UUID.randomUUID()).title("Anniversaire de Paul").date(LocalDate.of(2027, 6, 15))
                    .eventType("Anniversaire").ambiance("Champetre").ville("Lyon").lieu("Domaine des fleurs")
                    .nbInvites("80").imagePath("cover.jpg").notePartagee("Note partagee")
                    .description("Description").momentCle("Vin d'honneur").utilisateur(utilisateur).build();
            LocalDateTime lastUpdate = LocalDateTime.of(2027, 5, 1, 10, 0);

            EventDetailDto dto = mapper.toDetailDto(evenement, "imminent", lastUpdate);

            assertThat(dto).isEqualTo(new EventDetailDto(
                    evenement.getId(), "Anniversaire de Paul", LocalDate.of(2027, 6, 15), "Anniversaire",
                    "Champetre", "Lyon", "Domaine des fleurs", "80", "cover.jpg", "Note partagee",
                    "Description", "Vin d'honneur", "imminent", lastUpdate,
                    new ClientInfoDto("Sophie", "Leroy", "0102030405", "sophie@sgilt.fr")));
        }

        @Test
        void givenNoLastUpdateDate_whenToDetailDto_thenLastUpdateDateIsNull() {
            Evenement evenement = Evenement.builder().id(UUID.randomUUID()).title("Anniversaire").build();

            EventDetailDto dto = mapper.toDetailDto(evenement, "serein", null);

            assertThat(dto.lastUpdateDate()).isNull();
        }
    }
}
