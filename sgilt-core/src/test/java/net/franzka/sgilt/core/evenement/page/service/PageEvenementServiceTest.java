package net.franzka.sgilt.core.evenement.page.service;

import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.service.BilletterieService;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenement;
import net.franzka.sgilt.core.evenement.page.dto.EventPageDto;
import net.franzka.sgilt.core.evenement.page.exception.PageEvenementNotFoundException;
import net.franzka.sgilt.core.evenement.page.mapper.PageEvenementMapper;
import net.franzka.sgilt.core.evenement.page.repository.PageEvenementRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PageEvenementServiceTest {

    private static final String SLUG = "les-jeudis-du-taennel";
    private static final UUID EVENEMENT_ID = UUID.randomUUID();

    @Mock
    private PageEvenementRepository pageEvenementRepository;

    @Mock
    private BilletterieService billetterieService;

    @Mock
    private PageEvenementMapper pageEvenementMapper;

    @InjectMocks
    private PageEvenementService pageEvenementService;

    private static final LocalDate DATE = LocalDate.of(2026, 3, 12);

    private PageEvenement givenPage(LocalTime startTime, LocalTime endTime) {
        Evenement evenement = Evenement.builder().id(EVENEMENT_ID).date(DATE).startTime(startTime).endTime(endTime).build();
        PageEvenement page = PageEvenement.builder().evenement(evenement).slug(SLUG).build();
        when(pageEvenementRepository.findBySlug(SLUG)).thenReturn(Optional.of(page));
        return page;
    }

    // -------------------------------------------------------------------------
    // getPublicPage
    // -------------------------------------------------------------------------

    @Nested
    class GetPublicPage {

        @Test
        void givenPageWithBilletterie_whenGetPublicPage_thenMapsPageTicketingAndHours() {
            PageEvenement page = givenPage(LocalTime.of(19, 0), LocalTime.of(23, 0));
            TicketingDto ticketing = new TicketingDto(BilletterieStatus.OPEN, 1200, 150);
            EventPageDto dto = mock(EventPageDto.class);
            when(billetterieService.getPublicTicketing(EVENEMENT_ID)).thenReturn(Optional.of(ticketing));
            when(pageEvenementMapper.toEventPageDto(page, ticketing,
                    LocalDateTime.of(2026, 3, 12, 19, 0), LocalDateTime.of(2026, 3, 12, 23, 0))).thenReturn(dto);

            assertThat(pageEvenementService.getPublicPage(SLUG)).isSameAs(dto);
        }

        @Test
        void givenPageWithoutBilletterie_whenGetPublicPage_thenMapsNullTicketing() {
            PageEvenement page = givenPage(LocalTime.of(19, 0), LocalTime.of(23, 0));
            EventPageDto dto = mock(EventPageDto.class);
            when(billetterieService.getPublicTicketing(EVENEMENT_ID)).thenReturn(Optional.empty());
            when(pageEvenementMapper.toEventPageDto(page, null,
                    LocalDateTime.of(2026, 3, 12, 19, 0), LocalDateTime.of(2026, 3, 12, 23, 0))).thenReturn(dto);

            assertThat(pageEvenementService.getPublicPage(SLUG)).isSameAs(dto);
        }

        @Test
        void givenEndAfterMidnight_whenGetPublicPage_thenEndsAtIsNextDay() {
            PageEvenement page = givenPage(LocalTime.of(19, 0), LocalTime.of(2, 0));
            EventPageDto dto = mock(EventPageDto.class);
            when(billetterieService.getPublicTicketing(EVENEMENT_ID)).thenReturn(Optional.empty());
            when(pageEvenementMapper.toEventPageDto(page, null,
                    LocalDateTime.of(2026, 3, 12, 19, 0), LocalDateTime.of(2026, 3, 13, 2, 0))).thenReturn(dto);

            assertThat(pageEvenementService.getPublicPage(SLUG)).isSameAs(dto);
        }

        @Test
        void givenNoHours_whenGetPublicPage_thenStartsAtAndEndsAtAreNull() {
            PageEvenement page = givenPage(null, null);
            EventPageDto dto = mock(EventPageDto.class);
            when(billetterieService.getPublicTicketing(EVENEMENT_ID)).thenReturn(Optional.empty());
            when(pageEvenementMapper.toEventPageDto(page, null, null, null)).thenReturn(dto);

            assertThat(pageEvenementService.getPublicPage(SLUG)).isSameAs(dto);
        }

        @Test
        void givenEndWithoutStart_whenGetPublicPage_thenEndsAtIsSameDay() {
            PageEvenement page = givenPage(null, LocalTime.of(2, 0));
            EventPageDto dto = mock(EventPageDto.class);
            when(billetterieService.getPublicTicketing(EVENEMENT_ID)).thenReturn(Optional.empty());
            when(pageEvenementMapper.toEventPageDto(page, null, null, LocalDateTime.of(2026, 3, 12, 2, 0))).thenReturn(dto);

            assertThat(pageEvenementService.getPublicPage(SLUG)).isSameAs(dto);
        }

        @Test
        void givenUnknownSlug_whenGetPublicPage_thenThrowsNotFound() {
            when(pageEvenementRepository.findBySlug("inconnu")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pageEvenementService.getPublicPage("inconnu"))
                    .isInstanceOf(PageEvenementNotFoundException.class);
        }
    }
}
