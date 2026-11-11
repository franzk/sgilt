package net.franzka.sgilt.core.evenement.page.controller;

import net.franzka.sgilt.core.evenement.page.dto.EventPageDto;
import net.franzka.sgilt.core.evenement.billetterie.domain.BilletterieStatus;
import net.franzka.sgilt.core.evenement.billetterie.dto.TicketingSettingsDto;
import net.franzka.sgilt.core.evenement.domain.Evenement;
import net.franzka.sgilt.core.evenement.page.dto.OrganizerPublicPageDto;
import net.franzka.sgilt.core.evenement.page.dto.PageContentDto;
import net.franzka.sgilt.core.evenement.page.exception.PagePubliqueDisabledException;
import net.franzka.sgilt.core.evenement.page.service.PageEvenementService;
import net.franzka.sgilt.core.evenement.service.EvenementService;
import net.franzka.sgilt.core.security.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizerPageControllerTest {

    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private EvenementService evenementService;

    @Mock
    private PageEvenementService pageEvenementService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private OrganizerPageController controller;

    @Mock
    private OrganizerPublicPageDto state;

    private final Evenement evenement = Evenement.builder().id(EVENT_ID).build();

    @BeforeEach
    void givenOwnedEventWithPagePublique() {
        when(currentUserService.getId()).thenReturn(USER_ID);
    }

    // Événement de l'utilisateur connecté, page publique activée ; l'état renvoyé après l'action.
    private void givenOwnedEvent() {
        when(evenementService.getOwnedEventWithPagePublique(EVENT_ID, USER_ID)).thenReturn(evenement);
        when(pageEvenementService.getOrganizerPublicPage(evenement)).thenReturn(state);
    }

    // -------------------------------------------------------------------------
    // getPublicPage
    // -------------------------------------------------------------------------

    @Nested
    class GetPublicPage {

        @Test
        void givenOwnedEvent_whenGetPublicPage_thenReturnsItsState() {
            givenOwnedEvent();

            ResponseEntity<OrganizerPublicPageDto> response = controller.getPublicPage(EVENT_ID);

            assertThat(response.getBody()).isSameAs(state);
        }

        @Test
        void givenPagePubliqueDisabled_whenGetPublicPage_thenThrowsWithoutReadingIt() {
            when(evenementService.getOwnedEventWithPagePublique(EVENT_ID, USER_ID))
                    .thenThrow(new PagePubliqueDisabledException(EVENT_ID));

            assertThatThrownBy(() -> controller.getPublicPage(EVENT_ID))
                    .isInstanceOf(PagePubliqueDisabledException.class);
            verifyNoInteractions(pageEvenementService);
        }
    }

    // -------------------------------------------------------------------------
    // getPreview
    // -------------------------------------------------------------------------

    @Nested
    class GetPreview {

        @Test
        void givenOwnedEvent_whenGetPreview_thenReturnsThePage() {
            EventPageDto preview = mock(EventPageDto.class);
            when(evenementService.getOwnedEventWithPagePublique(EVENT_ID, USER_ID)).thenReturn(evenement);
            when(pageEvenementService.getPreview(evenement)).thenReturn(preview);

            assertThat(controller.getPreview(EVENT_ID).getBody()).isSameAs(preview);
        }
    }

    // -------------------------------------------------------------------------
    // putContent
    // -------------------------------------------------------------------------

    @Nested
    class PutContent {

        @Test
        void givenContent_whenPutContent_thenSavesItThenReturnsState() {
            PageContentDto content = new PageContentDto("slug", null, null, null, null, List.of(), List.of(), List.of(), null);
            givenOwnedEvent();

            ResponseEntity<OrganizerPublicPageDto> response = controller.putContent(EVENT_ID, content);

            InOrder order = inOrder(pageEvenementService);
            order.verify(pageEvenementService).putOrganizerContent(evenement, content);
            order.verify(pageEvenementService).getOrganizerPublicPage(evenement);
            assertThat(response.getBody()).isSameAs(state);
        }
    }

    // -------------------------------------------------------------------------
    // publish / unpublish
    // -------------------------------------------------------------------------

    @Nested
    class Publish {

        @Test
        void givenOwnedEvent_whenPublish_thenPublishesThenReturnsState() {
            givenOwnedEvent();

            ResponseEntity<OrganizerPublicPageDto> response = controller.publish(EVENT_ID);

            InOrder order = inOrder(pageEvenementService);
            order.verify(pageEvenementService).publish(evenement);
            order.verify(pageEvenementService).getOrganizerPublicPage(evenement);
            assertThat(response.getBody()).isSameAs(state);
        }
    }

    @Nested
    class Unpublish {

        @Test
        void givenOwnedEvent_whenUnpublish_thenUnpublishesThenReturnsState() {
            givenOwnedEvent();

            ResponseEntity<OrganizerPublicPageDto> response = controller.unpublish(EVENT_ID);

            InOrder order = inOrder(pageEvenementService);
            order.verify(pageEvenementService).unpublish(evenement);
            order.verify(pageEvenementService).getOrganizerPublicPage(evenement);
            assertThat(response.getBody()).isSameAs(state);
        }
    }

    // -------------------------------------------------------------------------
    // putTicketing
    // -------------------------------------------------------------------------

    @Nested
    class PutTicketing {

        @Test
        void givenSettings_whenPutTicketing_thenSavesThemThenReturnsState() {
            TicketingSettingsDto ticketing = new TicketingSettingsDto(BilletterieStatus.OPEN, 1200, "Tout compris", 150);
            givenOwnedEvent();

            ResponseEntity<OrganizerPublicPageDto> response = controller.putTicketing(EVENT_ID, ticketing);

            InOrder order = inOrder(pageEvenementService);
            order.verify(pageEvenementService).putOrganizerTicketing(evenement, ticketing);
            order.verify(pageEvenementService).getOrganizerPublicPage(evenement);
            assertThat(response.getBody()).isSameAs(state);
        }
    }
}
