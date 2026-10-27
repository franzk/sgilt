package net.franzka.sgilt.core.evenement.page.service;

import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;
import net.franzka.sgilt.core.evenement.billetterie.dto.AdminTicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.service.BilletterieService;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenement;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenementPicto;
import net.franzka.sgilt.core.evenement.page.dto.AdminPageDto;
import net.franzka.sgilt.core.evenement.page.dto.AdminEventPublicFormDto;
import net.franzka.sgilt.core.evenement.page.dto.EventPageDto;
import net.franzka.sgilt.core.evenement.page.exception.BilletterieWithoutPageException;
import net.franzka.sgilt.core.evenement.page.exception.PageEvenementNotFoundException;
import net.franzka.sgilt.core.evenement.page.exception.PageSlugAlreadyUsedException;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PageEvenementServiceTest {

    private static final String SLUG = "les-jeudis-du-taennel";
    private static final UUID EVENEMENT_ID = UUID.randomUUID();
    private static final LocalDate DATE = LocalDate.of(2026, 3, 12);
    private static final AdminPageDto PAGE =
            new AdminPageDto(SLUG, null, null, null, null, List.of(), List.of(), List.of());
    private static final AdminTicketingDto TICKETING = new AdminTicketingDto(BilletterieStatus.OPEN, 1200, 150);

    @Mock
    private PageEvenementRepository pageEvenementRepository;

    @Mock
    private BilletterieService billetterieService;

    @Mock
    private PageEvenementMapper pageEvenementMapper;

    @InjectMocks
    private PageEvenementService pageEvenementService;

    private final Evenement evenement = Evenement.builder().id(EVENEMENT_ID).build();

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

    // -------------------------------------------------------------------------
    // getAdminPublicPage
    // -------------------------------------------------------------------------

    @Nested
    class GetAdminPublicPage {

        @Test
        void givenPageAndBilletterie_whenGetAdminPublicPage_thenMapsEvenementPageAndTicketing() {
            PageEvenement page = PageEvenement.builder().build();
            AdminEventPublicFormDto dto = mock(AdminEventPublicFormDto.class);
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(page));
            when(billetterieService.getAdminTicketing(EVENEMENT_ID)).thenReturn(Optional.of(TICKETING));
            when(pageEvenementMapper.toAdminEventPublicFormDto(evenement, page, TICKETING)).thenReturn(dto);

            assertThat(pageEvenementService.getAdminPublicPage(evenement)).isSameAs(dto);
        }

        @Test
        void givenNoPageNorBilletterie_whenGetAdminPublicPage_thenMapsThemAsNull() {
            AdminEventPublicFormDto dto = mock(AdminEventPublicFormDto.class);
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());
            when(billetterieService.getAdminTicketing(EVENEMENT_ID)).thenReturn(Optional.empty());
            when(pageEvenementMapper.toAdminEventPublicFormDto(evenement, null, null)).thenReturn(dto);

            assertThat(pageEvenementService.getAdminPublicPage(evenement)).isSameAs(dto);
        }
    }

    // -------------------------------------------------------------------------
    // putPublicPage
    // -------------------------------------------------------------------------

    @Nested
    class PutPublicPage {

        @Test
        void givenTicketingWithoutPage_whenPutPublicPage_thenThrowsWithoutTouchingAnything() {
            assertThatThrownBy(() -> pageEvenementService.putPublicPage(evenement, null, TICKETING))
                    .isInstanceOf(BilletterieWithoutPageException.class);
            verifyNoInteractions(pageEvenementRepository, billetterieService);
        }

        @Test
        void givenNoPage_whenPutPublicPage_thenCreatesPageAndReplacesTicketing() {
            PageEvenement created = PageEvenement.builder().build();
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());
            when(pageEvenementRepository.findBySlug(SLUG)).thenReturn(Optional.empty());
            when(pageEvenementMapper.toPageEvenement(PAGE, evenement)).thenReturn(created);

            pageEvenementService.putPublicPage(evenement, PAGE, TICKETING);

            verify(pageEvenementRepository).save(created);
            verify(billetterieService).putTicketing(evenement, TICKETING);
        }

        @Test
        void givenPageWithSameSlug_whenPutPublicPage_thenReplacesItsFields() {
            PageEvenement existing = PageEvenement.builder().evenement(evenement).slug(SLUG)
                    .tag("Ancien tag").importantInfo(List.of("Ancienne info")).build();
            AdminPageDto page = new AdminPageDto(SLUG, null, "Sous-titre", "Section", "Description",
                    List.of("Nouvelle info"), null, List.of(new PageEvenementPicto("team", "Ambiance", "Locale")));
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(existing));
            when(pageEvenementRepository.findBySlug(SLUG)).thenReturn(Optional.of(existing));

            pageEvenementService.putPublicPage(evenement, page, null);

            assertThat(existing.getSlug()).isEqualTo(SLUG);
            assertThat(existing.getTag()).isNull();
            assertThat(existing.getSubtitle()).isEqualTo("Sous-titre");
            assertThat(existing.getSectionTitle()).isEqualTo("Section");
            assertThat(existing.getDescription()).isEqualTo("Description");
            assertThat(existing.getImportantInfo()).containsExactly("Nouvelle info");
            assertThat(existing.getPracticalInfo()).isEmpty();
            assertThat(existing.getPictos()).containsExactly(new PageEvenementPicto("team", "Ambiance", "Locale"));
            verify(pageEvenementRepository).save(existing);
            verify(pageEvenementMapper, never()).toPageEvenement(any(), any());
            verify(billetterieService).putTicketing(evenement, null);
        }

        @Test
        void givenSlugOfAnotherEvenement_whenPutPublicPage_thenThrowsSlugAlreadyUsed() {
            PageEvenement other = PageEvenement.builder()
                    .evenement(Evenement.builder().id(UUID.randomUUID()).build()).slug(SLUG).build();
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());
            when(pageEvenementRepository.findBySlug(SLUG)).thenReturn(Optional.of(other));

            assertThatThrownBy(() -> pageEvenementService.putPublicPage(evenement, PAGE, null))
                    .isInstanceOf(PageSlugAlreadyUsedException.class)
                    .hasMessageContaining(SLUG);
            verify(pageEvenementRepository, never()).save(any());
        }

        @Test
        void givenPageAndNullPage_whenPutPublicPage_thenDeletesPageAndTicketing() {
            PageEvenement existing = PageEvenement.builder().evenement(evenement).slug(SLUG).build();
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(existing));

            pageEvenementService.putPublicPage(evenement, null, null);

            verify(pageEvenementRepository).delete(existing);
            verify(pageEvenementRepository, never()).save(any());
            verify(billetterieService).putTicketing(evenement, null);
        }
    }

    // -------------------------------------------------------------------------
    // getSlugsByEvenementId
    // -------------------------------------------------------------------------

    @Nested
    class GetSlugsByEvenementId {

        @Test
        void givenPages_whenGetSlugsByEvenementId_thenIndexesSlugsByEvenement() {
            UUID otherId = UUID.randomUUID();
            when(pageEvenementRepository.findAll()).thenReturn(List.of(
                    PageEvenement.builder().evenement(evenement).slug(SLUG).build(),
                    PageEvenement.builder().evenement(Evenement.builder().id(otherId).build()).slug("autre").build()));

            assertThat(pageEvenementService.getSlugsByEvenementId())
                    .containsOnly(Map.entry(EVENEMENT_ID, SLUG), Map.entry(otherId, "autre"));
        }
    }
}
