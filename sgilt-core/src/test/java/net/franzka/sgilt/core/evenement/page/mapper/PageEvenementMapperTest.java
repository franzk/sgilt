package net.franzka.sgilt.core.evenement.page.mapper;

import net.franzka.sgilt.core.evenement.page.dto.OrganizerPageDto;
import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenement;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenementPicto;
import net.franzka.sgilt.core.evenement.page.dto.PageContentDto;
import net.franzka.sgilt.core.evenement.page.dto.EvenementInfosDto;
import net.franzka.sgilt.core.evenement.page.dto.EventPageDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
                    .title("Les Jeudis du Taennel").imagePath("bank/taennel.png").eventType("evenement_public")
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
            TicketingDto ticketing = new TicketingDto(BilletterieStatus.OPEN, 1200, 150, 10);
            LocalDateTime startsAt = LocalDateTime.of(2026, 3, 12, 19, 0);
            LocalDateTime endsAt = LocalDateTime.of(2026, 3, 13, 2, 0);

            EventPageDto dto = mapper.toEventPageDto(page, ticketing, startsAt, endsAt);

            assertThat(dto).isEqualTo(new EventPageDto(
                    "les-jeudis-du-taennel", "Édition du 12 mars", "Les Jeudis du Taennel",
                    "Concert & bar éphémère", "bank/taennel.png", "evenement_public", LocalDate.of(2026, 3, 12), startsAt, endsAt,
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
    // toOrganizerPageDto
    // -------------------------------------------------------------------------

    @Nested
    class ToOrganizerPageDto {

        private PageEvenement.PageEvenementBuilder page() {
            return PageEvenement.builder().slug("les-jeudis-du-taennel")
                    .importantInfo(List.of()).practicalInfo(List.of()).pictos(List.of());
        }

        private final PageContentDto content =
                new PageContentDto("les-jeudis-du-taennel", null, null, null, null, List.of(), List.of(), List.of());

        @Test
        void givenDraftNeverPublished_whenToOrganizerPageDto_thenNotPublishedAndSlugFree() {
            assertThat(mapper.toOrganizerPageDto(page().build()))
                    .isEqualTo(new OrganizerPageDto(content, false, false));
        }

        @Test
        void givenPublishedPage_whenToOrganizerPageDto_thenPublishedAndSlugLocked() {
            LocalDateTime publishedAt = LocalDateTime.of(2026, 1, 5, 10, 0);

            assertThat(mapper.toOrganizerPageDto(page().publishedAt(publishedAt).firstPublishedAt(publishedAt).build()))
                    .isEqualTo(new OrganizerPageDto(content, true, true));
        }

        @Test
        void givenUnpublishedPage_whenToOrganizerPageDto_thenNotPublishedButSlugLocked() {
            assertThat(mapper.toOrganizerPageDto(page().firstPublishedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build()))
                    .isEqualTo(new OrganizerPageDto(content, false, true));
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
            PageContentDto page = new PageContentDto("les-jeudis-du-taennel", "Tag", "Sous-titre", "Section",
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
            PageContentDto page = new PageContentDto("slug", null, null, null, null, null, null, null);

            PageEvenement pageEvenement = mapper.toPageEvenement(page, Evenement.builder().build());

            assertThat(pageEvenement.getImportantInfo()).isEmpty();
            assertThat(pageEvenement.getPracticalInfo()).isEmpty();
            assertThat(pageEvenement.getPictos()).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // toEvenementInfosDto
    // -------------------------------------------------------------------------

    @Nested
    class ToEvenementInfosDto {

        private final Evenement evenement = Evenement.builder()
                .date(LocalDate.of(2026, 3, 12)).lieu("Le Taennel").ville("Scherwiller (67)").build();
        private final LocalDateTime startsAt = LocalDateTime.of(2026, 3, 12, 19, 0);
        private final LocalDateTime endsAt = LocalDateTime.of(2026, 3, 13, 2, 0);

        @Test
        void givenEventWithPage_whenToEvenementInfosDto_thenMapsEventHoursAndPageFields() {
            PageEvenement page = PageEvenement.builder()
                    .tag("Édition du 12 mars").subtitle("Concert et apéro").practicalInfo(List.of("Vestiaire")).build();

            assertThat(mapper.toEvenementInfosDto(evenement, page, startsAt, endsAt))
                    .isEqualTo(new EvenementInfosDto("Édition du 12 mars", "Concert et apéro",
                            LocalDate.of(2026, 3, 12), startsAt, endsAt, "Le Taennel", "Scherwiller (67)",
                            List.of("Vestiaire")));
        }

        @Test
        void givenEventWithoutPage_whenToEvenementInfosDto_thenMapsNoTagNoSubtitleNoPracticalInfo() {
            assertThat(mapper.toEvenementInfosDto(evenement, null, startsAt, endsAt))
                    .isEqualTo(new EvenementInfosDto(null, null, LocalDate.of(2026, 3, 12), startsAt, endsAt,
                            "Le Taennel", "Scherwiller (67)", List.of()));
        }
    }
}
