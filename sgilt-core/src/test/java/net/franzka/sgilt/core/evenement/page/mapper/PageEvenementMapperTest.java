package net.franzka.sgilt.core.evenement.page.mapper;

import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenement;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenementPicto;
import net.franzka.sgilt.core.evenement.page.dto.EventPageDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
}
