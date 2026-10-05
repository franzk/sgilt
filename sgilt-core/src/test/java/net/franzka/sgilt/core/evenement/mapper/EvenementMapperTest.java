package net.franzka.sgilt.core.evenement.mapper;

import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.domain.EvenementRubrique;
import net.franzka.sgilt.core.evenement.domain.EvenementStatus;
import net.franzka.sgilt.core.evenement.dto.ClientInfoDto;
import net.franzka.sgilt.core.evenement.dto.DemandeInitieeDto;
import net.franzka.sgilt.core.evenement.dto.EvenementDto;
import net.franzka.sgilt.core.evenement.dto.EvenementSummaryDto;
import net.franzka.sgilt.core.evenement.dto.EventMetaDto;
import net.franzka.sgilt.core.evenement.dto.RubriqueDto;
import net.franzka.sgilt.core.reservation.dto.ReservationCounts;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
    // toMetaDto
    // -------------------------------------------------------------------------

    @Nested
    class ToMetaDto {

        @Test
        void givenEvenementCountdownAndLastUpdate_whenToMetaDto_thenMapsAllFields() {
            Utilisateur utilisateur = Utilisateur.builder()
                    .firstName("Sophie").lastName("Leroy").phone("0102030405").email("sophie@sgilt.fr").build();
            Evenement evenement = Evenement.builder()
                    .id(UUID.randomUUID()).title("Anniversaire de Paul").date(LocalDate.of(2027, 6, 15))
                    .eventType("Anniversaire").ambiance("Champetre").ville("Lyon").lieu("Domaine des fleurs")
                    .nbInvites("80").imagePath("cover.jpg").notePartagee("Note partagee")
                    .description("Description").momentCle("Vin d'honneur").utilisateur(utilisateur).build();
            LocalDateTime lastUpdate = LocalDateTime.of(2027, 5, 1, 10, 0);

            EventMetaDto dto = mapper.toMetaDto(evenement, "imminent", lastUpdate);

            assertThat(dto).isEqualTo(new EventMetaDto(
                    evenement.getId(), "Anniversaire de Paul", LocalDate.of(2027, 6, 15), "Anniversaire",
                    "Champetre", "Lyon", "Domaine des fleurs", "80", "cover.jpg", "Note partagee",
                    "Description", "Vin d'honneur", "imminent", lastUpdate,
                    new ClientInfoDto("Sophie", "Leroy", "0102030405", "sophie@sgilt.fr")));
        }

        @Test
        void givenNoLastUpdateDate_whenToMetaDto_thenLastUpdateDateIsNull() {
            Evenement evenement = Evenement.builder().id(UUID.randomUUID()).title("Anniversaire").build();

            EventMetaDto dto = mapper.toMetaDto(evenement, "serein", null);

            assertThat(dto.lastUpdateDate()).isNull();
        }
    }

    // -------------------------------------------------------------------------
    // toEvenement
    // -------------------------------------------------------------------------

    @Nested
    class ToEvenement {

        @Test
        void givenEvenementDto_whenToEvenement_thenMapsDataStatusOwnerAndRubriqueKeys() {
            Utilisateur utilisateur = Utilisateur.builder().id(UUID.randomUUID()).build();
            EvenementDto evenementDto = new EvenementDto("mariage", "chic", "danse", "Description",
                    LocalDate.of(2027, 6, 12), "Lyon", "80", "Domaine", List.of(
                            new RubriqueDto("lieu", List.of()),
                            new RubriqueDto("autre", List.of(new DemandeInitieeDto(UUID.randomUUID(), "Bonjour")))));

            Evenement evenement = mapper.toEvenement(evenementDto, utilisateur);

            assertThat(evenement.getUtilisateur()).isSameAs(utilisateur);
            assertThat(evenement.getStatus()).isEqualTo(EvenementStatus.ACTIVE);
            assertThat(evenement.getEventType()).isEqualTo("mariage");
            assertThat(evenement.getDate()).isEqualTo(LocalDate.of(2027, 6, 12));
            assertThat(evenement.getLieu()).isEqualTo("Domaine");
            assertThat(evenement.getRubriques())
                    .containsExactly(new EvenementRubrique("lieu"), new EvenementRubrique("autre"));
        }

        @Test
        void givenNoRubriques_whenToEvenement_thenRubriquesAreEmpty() {
            EvenementDto evenementDto = new EvenementDto(
                    "mariage", null, null, null, null, null, null, null, null);

            assertThat(mapper.toEvenement(evenementDto, Utilisateur.builder().build()).getRubriques()).isEmpty();
        }
    }
}
