package net.franzka.sgilt.core.evenement.page.service;

import net.franzka.sgilt.core.evenement.page.domain.PublicPageStatus;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingSalesDto;
import net.franzka.sgilt.core.evenement.page.dto.OrganizerPageDto;
import net.franzka.sgilt.core.evenement.page.dto.OrganizerPublicPageDto;
import net.franzka.sgilt.core.evenement.page.exception.PageNotCreatedException;
import net.franzka.sgilt.core.evenement.page.exception.PageSlugLockedException;
import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingSettingsDto;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingDto;
import net.franzka.sgilt.core.evenement.billetterie.service.BilletterieService;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.domain.PageEvenement;
import net.franzka.sgilt.core.evenement.page.dto.PageContentDto;
import net.franzka.sgilt.core.evenement.page.dto.EvenementInfosDto;
import net.franzka.sgilt.core.evenement.page.dto.EventPageDto;
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
    private static final PageContentDto PAGE =
            new PageContentDto(SLUG, null, null, null, null, List.of(), List.of(), List.of());
    private static final TicketingSettingsDto TICKETING = new TicketingSettingsDto(BilletterieStatus.OPEN, 1200, 150);

    @Mock
    private PageEvenementRepository pageEvenementRepository;

    @Mock
    private BilletterieService billetterieService;

    @Mock
    private PageEvenementMapper pageEvenementMapper;

    @InjectMocks
    private PageEvenementService pageEvenementService;

    private final Evenement evenement = Evenement.builder().id(EVENEMENT_ID).build();

    // Page en ligne : publiée, page publique de l'événement activée.
    private PageEvenement givenPage(LocalTime startTime, LocalTime endTime) {
        Evenement evenement = Evenement.builder().id(EVENEMENT_ID).date(DATE).startTime(startTime).endTime(endTime)
                .pagePubliqueEnabled(true).build();
        PageEvenement page = PageEvenement.builder().evenement(evenement).slug(SLUG)
                .publishedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();
        when(pageEvenementRepository.findBySlug(SLUG)).thenReturn(Optional.of(page));
        return page;
    }

    // Page hors ligne : brouillon, ou page publique de l'événement désactivée.
    private void givenOfflinePage(boolean published, boolean pagePubliqueEnabled) {
        Evenement evenement = Evenement.builder().id(EVENEMENT_ID).pagePubliqueEnabled(pagePubliqueEnabled).build();
        PageEvenement page = PageEvenement.builder().evenement(evenement).slug(SLUG)
                .publishedAt(published ? LocalDateTime.of(2026, 1, 5, 10, 0) : null).build();
        when(pageEvenementRepository.findBySlug(SLUG)).thenReturn(Optional.of(page));
    }

    // -------------------------------------------------------------------------
    // getPublicPage
    // -------------------------------------------------------------------------

    @Nested
    class GetPublicPage {

        @Test
        void givenPageWithBilletterie_whenGetPublicPage_thenMapsPageTicketingAndHours() {
            PageEvenement page = givenPage(LocalTime.of(19, 0), LocalTime.of(23, 0));
            TicketingDto ticketing = new TicketingDto(BilletterieStatus.OPEN, 1200, 150, 10);
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

        @Test
        void givenDraftPage_whenGetPublicPage_thenThrowsNotFound() {
            givenOfflinePage(false, true);

            assertThatThrownBy(() -> pageEvenementService.getPublicPage(SLUG))
                    .isInstanceOf(PageEvenementNotFoundException.class);
            verifyNoInteractions(billetterieService);
        }

        @Test
        void givenPagePubliqueDisabled_whenGetPublicPage_thenThrowsNotFound() {
            givenOfflinePage(true, false);

            assertThatThrownBy(() -> pageEvenementService.getPublicPage(SLUG))
                    .isInstanceOf(PageEvenementNotFoundException.class);
            verifyNoInteractions(billetterieService);
        }
    }

    // -------------------------------------------------------------------------
    // getEvenementInfos
    // -------------------------------------------------------------------------

    @Nested
    class GetEvenementInfos {

        private final Evenement evenement = Evenement.builder()
                .id(EVENEMENT_ID).date(DATE).startTime(LocalTime.of(19, 0)).endTime(LocalTime.of(2, 0)).build();

        @Test
        void givenEventWithPage_whenGetEvenementInfos_thenMapsHoursAndItsPage() {
            PageEvenement page = PageEvenement.builder().evenement(evenement).build();
            EvenementInfosDto dto = mock(EvenementInfosDto.class);
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(page));
            when(pageEvenementMapper.toEvenementInfosDto(evenement, page,
                    LocalDateTime.of(2026, 3, 12, 19, 0), LocalDateTime.of(2026, 3, 13, 2, 0)))
                    .thenReturn(dto);

            assertThat(pageEvenementService.getEvenementInfos(evenement)).isSameAs(dto);
        }

        @Test
        void givenEventWithoutPage_whenGetEvenementInfos_thenMapsHoursWithoutPage() {
            EvenementInfosDto dto = mock(EvenementInfosDto.class);
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());
            when(pageEvenementMapper.toEvenementInfosDto(evenement, null,
                    LocalDateTime.of(2026, 3, 12, 19, 0), LocalDateTime.of(2026, 3, 13, 2, 0)))
                    .thenReturn(dto);

            assertThat(pageEvenementService.getEvenementInfos(evenement)).isSameAs(dto);
        }
    }

    // -------------------------------------------------------------------------
    // getSlug
    // -------------------------------------------------------------------------

    @Nested
    class GetSlug {

        private final Evenement evenement = Evenement.builder().id(EVENEMENT_ID).build();

        @Test
        void givenEventWithPage_whenGetSlug_thenReturnsSlugOfItsPage() {
            PageEvenement page = PageEvenement.builder().evenement(evenement).slug("jeudis-taennel").build();
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(page));

            assertThat(pageEvenementService.getSlug(evenement)).contains("jeudis-taennel");
        }

        @Test
        void givenEventWithoutPage_whenGetSlug_thenReturnsEmpty() {
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());

            assertThat(pageEvenementService.getSlug(evenement)).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // getEvenementIdBySlug
    // -------------------------------------------------------------------------

    @Nested
    class GetEvenementIdBySlug {

        @Test
        void givenPage_whenGetEvenementIdBySlug_thenReturnsItsEvenementId() {
            givenPage(null, null);

            assertThat(pageEvenementService.getEvenementIdBySlug(SLUG)).isEqualTo(EVENEMENT_ID);
        }

        @Test
        void givenUnknownSlug_whenGetEvenementIdBySlug_thenThrowsNotFound() {
            when(pageEvenementRepository.findBySlug("inconnu")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pageEvenementService.getEvenementIdBySlug("inconnu"))
                    .isInstanceOf(PageEvenementNotFoundException.class);
        }

        @Test
        void givenDraftPage_whenGetEvenementIdBySlug_thenThrowsNotFound() {
            givenOfflinePage(false, true);

            assertThatThrownBy(() -> pageEvenementService.getEvenementIdBySlug(SLUG))
                    .isInstanceOf(PageEvenementNotFoundException.class);
        }

        @Test
        void givenPagePubliqueDisabled_whenGetEvenementIdBySlug_thenThrowsNotFound() {
            givenOfflinePage(true, false);

            assertThatThrownBy(() -> pageEvenementService.getEvenementIdBySlug(SLUG))
                    .isInstanceOf(PageEvenementNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // getOrganizerPublicPage
    // -------------------------------------------------------------------------

    @Nested
    class GetOrganizerPublicPage {

        @Test
        void givenPageAndBilletterie_whenGetOrganizerPublicPage_thenAssemblesPageTicketingAndSales() {
            Evenement enabled = Evenement.builder().id(EVENEMENT_ID).pagePubliqueEnabled(true).billetterieEnabled(true).build();
            PageEvenement page = PageEvenement.builder().build();
            OrganizerPageDto pageDto = new OrganizerPageDto(PAGE, true, true);
            TicketingSalesDto sales = new TicketingSalesDto(42, 3, 150);
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(page));
            when(pageEvenementMapper.toOrganizerPageDto(page)).thenReturn(pageDto);
            when(billetterieService.getOrganizerTicketing(EVENEMENT_ID)).thenReturn(Optional.of(TICKETING));
            when(billetterieService.getSales(EVENEMENT_ID)).thenReturn(Optional.of(sales));

            assertThat(pageEvenementService.getOrganizerPublicPage(enabled))
                    .isEqualTo(new OrganizerPublicPageDto(true, pageDto, TICKETING, sales));
        }

        @Test
        void givenNoPageNorBilletterie_whenGetOrganizerPublicPage_thenTheyAreNull() {
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());
            when(billetterieService.getOrganizerTicketing(EVENEMENT_ID)).thenReturn(Optional.empty());
            when(billetterieService.getSales(EVENEMENT_ID)).thenReturn(Optional.empty());

            assertThat(pageEvenementService.getOrganizerPublicPage(evenement))
                    .isEqualTo(new OrganizerPublicPageDto(false, null, null, null));
        }
    }

    // -------------------------------------------------------------------------
    // putOrganizerContent
    // -------------------------------------------------------------------------

    @Nested
    class PutOrganizerContent {

        @Test
        void givenNoPage_whenPutOrganizerContent_thenCreatesDraft() {
            PageEvenement created = PageEvenement.builder().build();
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());
            when(pageEvenementRepository.findBySlug(SLUG)).thenReturn(Optional.empty());
            when(pageEvenementMapper.toPageEvenement(PAGE, evenement)).thenReturn(created);

            pageEvenementService.putOrganizerContent(evenement, PAGE);

            verify(pageEvenementRepository).save(created);
            assertThat(created.getPublishedAt()).isNull();
            assertThat(created.getFirstPublishedAt()).isNull();
        }

        @Test
        void givenDraftPage_whenPutOrganizerContentWithNewSlug_thenReplacesItsFields() {
            PageEvenement existing = PageEvenement.builder().evenement(evenement).slug("ancien-slug").build();
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(existing));
            when(pageEvenementRepository.findBySlug(SLUG)).thenReturn(Optional.empty());

            pageEvenementService.putOrganizerContent(evenement, PAGE);

            verify(pageEvenementRepository).save(existing);
            assertThat(existing.getSlug()).isEqualTo(SLUG);
        }

        @Test
        void givenPublishedOnce_whenPutOrganizerContentWithSameSlug_thenReplacesItsFields() {
            PageEvenement existing = PageEvenement.builder().evenement(evenement).slug(SLUG)
                    .firstPublishedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(existing));
            when(pageEvenementRepository.findBySlug(SLUG)).thenReturn(Optional.of(existing));

            pageEvenementService.putOrganizerContent(evenement, PAGE);

            verify(pageEvenementRepository).save(existing);
        }

        @Test
        void givenPublishedOnce_whenPutOrganizerContentWithNewSlug_thenThrowsSlugLocked() {
            PageEvenement existing = PageEvenement.builder().evenement(evenement).slug("ancien-slug")
                    .firstPublishedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> pageEvenementService.putOrganizerContent(evenement, PAGE))
                    .isInstanceOf(PageSlugLockedException.class);
            verify(pageEvenementRepository, never()).save(any());
            assertThat(existing.getSlug()).isEqualTo("ancien-slug");
        }

        @Test
        void givenSlugOfAnotherEvenement_whenPutOrganizerContent_thenThrowsSlugAlreadyUsed() {
            PageEvenement other = PageEvenement.builder()
                    .evenement(Evenement.builder().id(UUID.randomUUID()).build()).slug(SLUG).build();
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());
            when(pageEvenementRepository.findBySlug(SLUG)).thenReturn(Optional.of(other));

            assertThatThrownBy(() -> pageEvenementService.putOrganizerContent(evenement, PAGE))
                    .isInstanceOf(PageSlugAlreadyUsedException.class);
            verify(pageEvenementRepository, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // publish / unpublish
    // -------------------------------------------------------------------------

    @Nested
    class Publish {

        @Test
        void givenDraftNeverPublished_whenPublish_thenPublishesAndLocksSlug() {
            PageEvenement page = PageEvenement.builder().evenement(evenement).slug(SLUG).build();
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(page));

            pageEvenementService.publish(evenement);

            verify(pageEvenementRepository).save(page);
            assertThat(page.getPublishedAt()).isNotNull();
            assertThat(page.getFirstPublishedAt()).isEqualTo(page.getPublishedAt());
        }

        @Test
        void givenDraftPublishedBefore_whenPublish_thenKeepsFirstPublicationDate() {
            LocalDateTime firstPublishedAt = LocalDateTime.of(2026, 1, 5, 10, 0);
            PageEvenement page = PageEvenement.builder().evenement(evenement).slug(SLUG).firstPublishedAt(firstPublishedAt).build();
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(page));

            pageEvenementService.publish(evenement);

            assertThat(page.getPublishedAt()).isAfter(firstPublishedAt);
            assertThat(page.getFirstPublishedAt()).isEqualTo(firstPublishedAt);
        }

        @Test
        void givenPublishedPage_whenPublish_thenChangesNothing() {
            LocalDateTime publishedAt = LocalDateTime.of(2026, 1, 5, 10, 0);
            PageEvenement page = PageEvenement.builder().evenement(evenement).slug(SLUG)
                    .publishedAt(publishedAt).firstPublishedAt(publishedAt).build();
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(page));

            pageEvenementService.publish(evenement);

            verify(pageEvenementRepository, never()).save(any());
            assertThat(page.getPublishedAt()).isEqualTo(publishedAt);
        }

        @Test
        void givenNoPage_whenPublish_thenThrowsNotCreated() {
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pageEvenementService.publish(evenement))
                    .isInstanceOf(PageNotCreatedException.class);
        }
    }

    @Nested
    class GetPreview {

        @Test
        void givenDraftPage_whenGetPreview_thenMapsItWithoutTicketing() {
            Evenement event = Evenement.builder().id(EVENEMENT_ID).date(DATE)
                    .startTime(LocalTime.of(19, 0)).endTime(LocalTime.of(2, 0)).build();
            PageEvenement page = PageEvenement.builder().evenement(event).slug(SLUG).build();
            EventPageDto dto = mock(EventPageDto.class);
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(page));
            when(pageEvenementMapper.toEventPageDto(page, null,
                    LocalDateTime.of(2026, 3, 12, 19, 0), LocalDateTime.of(2026, 3, 13, 2, 0))).thenReturn(dto);

            assertThat(pageEvenementService.getPreview(event)).isSameAs(dto);
            verifyNoInteractions(billetterieService);
        }

        @Test
        void givenNoPage_whenGetPreview_thenThrowsNotCreated() {
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pageEvenementService.getPreview(evenement))
                    .isInstanceOf(PageNotCreatedException.class);
        }
    }

    @Nested
    class Unpublish {

        @Test
        void givenPublishedPage_whenUnpublish_thenBecomesDraftWithSlugStillLocked() {
            LocalDateTime publishedAt = LocalDateTime.of(2026, 1, 5, 10, 0);
            PageEvenement page = PageEvenement.builder().evenement(evenement).slug(SLUG)
                    .publishedAt(publishedAt).firstPublishedAt(publishedAt).build();
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(page));

            pageEvenementService.unpublish(evenement);

            verify(pageEvenementRepository).save(page);
            assertThat(page.getPublishedAt()).isNull();
            assertThat(page.getFirstPublishedAt()).isEqualTo(publishedAt);
            verifyNoInteractions(billetterieService);
        }

        @Test
        void givenNoPage_whenUnpublish_thenThrowsNotCreated() {
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pageEvenementService.unpublish(evenement))
                    .isInstanceOf(PageNotCreatedException.class);
        }
    }

    // -------------------------------------------------------------------------
    // putOrganizerTicketing
    // -------------------------------------------------------------------------

    @Nested
    class PutOrganizerTicketing {

        @Test
        void givenPublishedPage_whenPutOrganizerTicketing_thenSavesItAsPublished() {
            PageEvenement page = PageEvenement.builder().publishedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(page));

            pageEvenementService.putOrganizerTicketing(evenement, TICKETING);

            verify(billetterieService).putOrganizerTicketing(evenement, TICKETING, true);
        }

        @Test
        void givenDraftPage_whenPutOrganizerTicketing_thenSavesItAsNotPublished() {
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(PageEvenement.builder().build()));

            pageEvenementService.putOrganizerTicketing(evenement, TICKETING);

            verify(billetterieService).putOrganizerTicketing(evenement, TICKETING, false);
        }

        @Test
        void givenNoPage_whenPutOrganizerTicketing_thenSavesItAsNotPublished() {
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());

            pageEvenementService.putOrganizerTicketing(evenement, TICKETING);

            verify(billetterieService).putOrganizerTicketing(evenement, TICKETING, false);
        }
    }

    // -------------------------------------------------------------------------
    // getPublicPageStatus
    // -------------------------------------------------------------------------

    @Nested
    class GetPublicPageStatus {

        private final Evenement enabled = Evenement.builder().id(EVENEMENT_ID).pagePubliqueEnabled(true).build();

        @Test
        void givenPagePubliqueDisabled_whenGetPublicPageStatus_thenNull() {
            assertThat(pageEvenementService.getPublicPageStatus(evenement)).isNull();
            verifyNoInteractions(pageEvenementRepository);
        }

        @Test
        void givenNoPage_whenGetPublicPageStatus_thenNotCreated() {
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());

            assertThat(pageEvenementService.getPublicPageStatus(enabled)).isEqualTo(PublicPageStatus.NOT_CREATED);
        }

        @Test
        void givenDraftPage_whenGetPublicPageStatus_thenDraft() {
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(PageEvenement.builder().build()));

            assertThat(pageEvenementService.getPublicPageStatus(enabled)).isEqualTo(PublicPageStatus.DRAFT);
        }

        @Test
        void givenPublishedPage_whenGetPublicPageStatus_thenPublished() {
            PageEvenement page = PageEvenement.builder().publishedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(page));

            assertThat(pageEvenementService.getPublicPageStatus(enabled)).isEqualTo(PublicPageStatus.PUBLISHED);
        }
    }

    // -------------------------------------------------------------------------
    // isOnline
    // -------------------------------------------------------------------------

    @Nested
    class IsOnline {

        // Page de l'événement, publiée ou non, page publique activée ou non.
        private Evenement givenPageOf(boolean published, boolean pagePubliqueEnabled) {
            Evenement event = Evenement.builder().id(EVENEMENT_ID).pagePubliqueEnabled(pagePubliqueEnabled).build();
            PageEvenement page = PageEvenement.builder().evenement(event)
                    .publishedAt(published ? LocalDateTime.of(2026, 1, 5, 10, 0) : null).build();
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.of(page));
            return event;
        }

        @Test
        void givenPublishedPageEnabled_whenIsOnline_thenTrue() {
            assertThat(pageEvenementService.isOnline(givenPageOf(true, true))).isTrue();
        }

        @Test
        void givenDraftPage_whenIsOnline_thenFalse() {
            assertThat(pageEvenementService.isOnline(givenPageOf(false, true))).isFalse();
        }

        @Test
        void givenPagePubliqueDisabled_whenIsOnline_thenFalse() {
            assertThat(pageEvenementService.isOnline(givenPageOf(true, false))).isFalse();
        }

        @Test
        void givenNoPage_whenIsOnline_thenFalse() {
            when(pageEvenementRepository.findByEvenementId(EVENEMENT_ID)).thenReturn(Optional.empty());

            assertThat(pageEvenementService.isOnline(evenement)).isFalse();
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
