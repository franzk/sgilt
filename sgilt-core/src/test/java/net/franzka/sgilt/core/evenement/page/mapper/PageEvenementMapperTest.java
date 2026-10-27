package net.franzka.sgilt.core.evenement.page.mapper;

import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;
import net.franzka.sgilt.core.evenement.billetterie.dto.AdminTicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenement;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenementPicto;
import net.franzka.sgilt.core.evenement.page.dto.AdminPageDto;
import net.franzka.sgilt.core.evenement.page.dto.AdminEventPublicFormDto;
import net.franzka.sgilt.core.evenement.page.dto.EventPageDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PageEvenementMapperTest {

    private final PageEvenementMapper mapper = new PageEvenementMapperImpl();

    // -------------------------------------------------------------------------
    // toEventPageDto
    // -------------------------------------------------------------------------

    @Nested
    class ToEventPageDto {

        @Test
        void givenPageTicketingAndHours_whenToEventPageDto_thenMapsAllFields() {
            Evenement evenement = Evenement.builder()
                    .title("Les Jeudis du Taennel").imagePath("bank/taennel.png")
                    .date(LocalDate.of(2026, 3, 12))
                    .lieu("Le Taennel").ville("Scherwiller (67)").build();
            PageEvenement page = PageEvenement.builder()
                    .evenement(evenement).slug("les-jeudis-du-taennel").tag("Édition du 12 mars")
                    .subtitle("Concert & bar éphémère").sectionTitle("Une soirée musicale")
                    .description("Le jeudi, le Taennel se transforme.")
                    .importantInfo(List.of("Places debout."))
                    .practicalInfo(List.of("Billet sur smartphone."))
                    .pictos(List.of(new PageEvenementPicto("music", "Concert live", "À partir de 20h")))
                    .build();
            TicketingDto ticketing = new TicketingDto(BilletterieStatus.OPEN, 1200, 150);
            LocalDateTime startsAt = LocalDateTime.of(2026, 3, 12, 19, 0);
            LocalDateTime endsAt = LocalDateTime.of(2026, 3, 13, 2, 0);

            EventPageDto dto = mapper.toEventPageDto(page, ticketing, startsAt, endsAt);

            assertThat(dto).isEqualTo(new EventPageDto(
                    "les-jeudis-du-taennel", "Édition du 12 mars", "Les Jeudis du Taennel",
                    "Concert & bar éphémère", "bank/taennel.png", LocalDate.of(2026, 3, 12), startsAt, endsAt,
                    "Le Taennel",
                    "Scherwiller (67)", "Une soirée musicale", "Le jeudi, le Taennel se transforme.",
                    ticketing, List.of("Places debout."),
                    List.of(new PageEvenementPicto("music", "Concert live", "À partir de 20h")),
                    List.of("Billet sur smartphone.")));
        }

        @Test
        void givenNoTicketingNorHours_whenToEventPageDto_thenTheyAreNull() {
            PageEvenement page = PageEvenement.builder()
                    .evenement(Evenement.builder().title("Les Jeudis du Taennel").date(LocalDate.of(2026, 3, 12)).build())
                    .slug("les-jeudis-du-taennel").build();

            EventPageDto dto = mapper.toEventPageDto(page, null, null, null);

            assertThat(dto.ticketing()).isNull();
            assertThat(dto.startsAt()).isNull();
            assertThat(dto.endsAt()).isNull();
        }
    }

    // -------------------------------------------------------------------------
    // toAdminEventPublicFormDto
    // -------------------------------------------------------------------------

    @Nested
    class ToAdminEventPublicFormDto {

        @Test
        void givenEvenementPageAndTicketing_whenToAdminEventPublicFormDto_thenMapsHoursPageAndTicketing() {
            Evenement evenement = Evenement.builder().startTime(LocalTime.of(19, 0)).endTime(LocalTime.of(2, 0)).build();
            PageEvenement page = PageEvenement.builder()
                    .slug("les-jeudis-du-taennel").tag("Édition du 12 mars").subtitle("Concert")
                    .sectionTitle("Une soirée").description("Le jeudi.")
                    .importantInfo(List.of("Places debout.")).practicalInfo(List.of("Billet sur smartphone."))
                    .pictos(List.of(new PageEvenementPicto("music", "Concert live", "À partir de 20h")))
                    .build();
            AdminTicketingDto ticketing = new AdminTicketingDto(BilletterieStatus.OPEN, 1200, 150);

            AdminEventPublicFormDto dto = mapper.toAdminEventPublicFormDto(evenement, page, ticketing);

            assertThat(dto).isEqualTo(new AdminEventPublicFormDto(
                    LocalTime.of(19, 0), LocalTime.of(2, 0),
                    new AdminPageDto("les-jeudis-du-taennel", "Édition du 12 mars", "Concert", "Une soirée",
                            "Le jeudi.", List.of("Places debout."), List.of("Billet sur smartphone."),
                            List.of(new PageEvenementPicto("music", "Concert live", "À partir de 20h"))),
                    ticketing));
        }

        @Test
        void givenNoHoursNorPageNorTicketing_whenToAdminEventPublicFormDto_thenAllAreNull() {
            AdminEventPublicFormDto dto = mapper.toAdminEventPublicFormDto(Evenement.builder().build(), null, null);

            assertThat(dto).isEqualTo(new AdminEventPublicFormDto(null, null, null, null));
        }
    }

    // -------------------------------------------------------------------------
    // toPageEvenement
    // -------------------------------------------------------------------------

    @Nested
    class ToPageEvenement {

        @Test
        void givenPageAndEvenement_whenToPageEvenement_thenMapsAllFields() {
            Evenement evenement = Evenement.builder().id(UUID.randomUUID()).build();
            AdminPageDto page = new AdminPageDto("les-jeudis-du-taennel", "Tag", "Sous-titre", "Section",
                    "Description", List.of("Important"), List.of("Pratique"),
                    List.of(new PageEvenementPicto("leaf", "Cadre", "Vignoble")));

            PageEvenement pageEvenement = mapper.toPageEvenement(page, evenement);

            assertThat(pageEvenement.getId()).isNull();
            assertThat(pageEvenement.getEvenement()).isSameAs(evenement);
            assertThat(pageEvenement.getSlug()).isEqualTo("les-jeudis-du-taennel");
            assertThat(pageEvenement.getTag()).isEqualTo("Tag");
            assertThat(pageEvenement.getSubtitle()).isEqualTo("Sous-titre");
            assertThat(pageEvenement.getSectionTitle()).isEqualTo("Section");
            assertThat(pageEvenement.getDescription()).isEqualTo("Description");
            assertThat(pageEvenement.getImportantInfo()).containsExactly("Important");
            assertThat(pageEvenement.getPracticalInfo()).containsExactly("Pratique");
            assertThat(pageEvenement.getPictos()).containsExactly(new PageEvenementPicto("leaf", "Cadre", "Vignoble"));
        }

        @Test
        void givenNullLists_whenToPageEvenement_thenListsAreEmpty() {
            AdminPageDto page = new AdminPageDto("slug", null, null, null, null, null, null, null);

            PageEvenement pageEvenement = mapper.toPageEvenement(page, Evenement.builder().build());

            assertThat(pageEvenement.getImportantInfo()).isEmpty();
            assertThat(pageEvenement.getPracticalInfo()).isEmpty();
            assertThat(pageEvenement.getPictos()).isEmpty();
        }
    }

}
