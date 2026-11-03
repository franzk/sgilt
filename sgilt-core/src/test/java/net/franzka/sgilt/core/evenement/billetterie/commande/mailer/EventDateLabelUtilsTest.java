package net.franzka.sgilt.core.evenement.billetterie.commande.mailer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EventDateLabelUtilsTest {

    private static final LocalDate DATE = LocalDate.of(2026, 12, 3);
    private static final LocalDateTime STARTS_AT = LocalDateTime.of(2026, 12, 3, 19, 0);
    private static final LocalDateTime ENDS_AT = LocalDateTime.of(2026, 12, 4, 2, 0);

    // -------------------------------------------------------------------------
    // eventDateLabel
    // -------------------------------------------------------------------------

    @Nested
    class EventDateLabel {

        @Test
        void givenStartAndEnd_whenEventDateLabel_thenReturnsDateAndBothHours() {
            assertThat(EventDateLabelUtils.eventDateLabel(DATE, STARTS_AT, ENDS_AT))
                    .isEqualTo("Jeudi 3 décembre 2026 · 19h – 2h");
        }

        @Test
        void givenStartOnly_whenEventDateLabel_thenReturnsDateAndStart() {
            assertThat(EventDateLabelUtils.eventDateLabel(DATE, STARTS_AT, null))
                    .isEqualTo("Jeudi 3 décembre 2026 · 19h");
        }

        @Test
        void givenEndOnly_whenEventDateLabel_thenReturnsDateAndUntilEnd() {
            assertThat(EventDateLabelUtils.eventDateLabel(DATE, null, ENDS_AT))
                    .isEqualTo("Jeudi 3 décembre 2026 · jusqu'à 2h");
        }

        @Test
        void givenNoHours_whenEventDateLabel_thenReturnsDateOnly() {
            assertThat(EventDateLabelUtils.eventDateLabel(DATE, null, null))
                    .isEqualTo("Jeudi 3 décembre 2026");
        }

        @Test
        void givenHoursWithMinutes_whenEventDateLabel_thenPadsMinutes() {
            assertThat(EventDateLabelUtils.eventDateLabel(DATE,
                    LocalDateTime.of(2026, 12, 3, 19, 30), LocalDateTime.of(2026, 12, 3, 23, 5)))
                    .isEqualTo("Jeudi 3 décembre 2026 · 19h30 – 23h05");
        }
    }
}
