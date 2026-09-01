package net.franzka.sgilt.core.prestataire.controller;

import net.franzka.sgilt.core.prestataire.domain.Engagement;
import net.franzka.sgilt.core.prestataire.dto.MediaUploadDto;
import net.franzka.sgilt.core.prestataire.dto.MediaDto;
import net.franzka.sgilt.core.prestataire.dto.MediasPutRequest;
import net.franzka.sgilt.core.prestataire.dto.PrestataireDetailDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireSearchResponseDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireUpdateDto;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.security.CurrentUserService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrestataireControllerTest {

    @Mock
    private PrestataireService prestataireService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private PrestataireController controller;

    private final UUID userId = UUID.randomUUID();
    private final Utilisateur utilisateur = Utilisateur.builder().id(userId).email("pro@sgilt.fr").build();

    // -------------------------------------------------------------------------
    // search
    // -------------------------------------------------------------------------

    @Nested
    class Search {

        @Test
        void givenFilters_whenSearch_thenDelegatesToService() {
            List<String> subcats = List.of("dj");
            PrestataireSearchResponseDto dto = mock(PrestataireSearchResponseDto.class);
            when(prestataireService.search("musique", subcats)).thenReturn(dto);

            assertThat(controller.search("musique", subcats).getBody()).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // getBySlug
    // -------------------------------------------------------------------------

    @Nested
    class GetBySlug {

        @Test
        void givenSlug_whenGetBySlug_thenReturnsDetail() {
            PrestataireDetailDto dto = mock(PrestataireDetailDto.class);
            when(prestataireService.getBySlug("studio-fleur")).thenReturn(dto);

            assertThat(controller.getBySlug("studio-fleur").getBody()).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // getEngagementKeys
    // -------------------------------------------------------------------------

    @Nested
    class GetEngagementKeys {

        @Test
        void whenGetEngagementKeys_thenReturnsAllEnumNames() {
            List<String> keys = controller.getEngagementKeys().getBody();

            assertThat(keys).containsExactlyInAnyOrder(
                    "REPONSE_48H", "ADAPTABLE", "ACCOMPAGNEMENT", "EQUIPE", "INTERLOCUTEUR_UNIQUE", "ECORESPONSABLE");
            assertThat(keys).hasSize(Engagement.values().length);
        }
    }

    // -------------------------------------------------------------------------
    // getMaFiche
    // -------------------------------------------------------------------------

    @Nested
    class GetMaFiche {

        @Test
        void givenCurrentUser_whenGetMaFiche_thenReturnsOwnedFiche() {
            when(currentUserService.get()).thenReturn(utilisateur);
            PrestataireDetailDto dto = mock(PrestataireDetailDto.class);
            when(prestataireService.getByUtilisateurOwner(utilisateur)).thenReturn(dto);

            assertThat(controller.getMaFiche().getBody()).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // submit
    // -------------------------------------------------------------------------

    @Nested
    class Submit {

        @Test
        void givenCurrentUser_whenSubmit_thenDelegatesAndReturnsNoContent() {
            when(currentUserService.get()).thenReturn(utilisateur);

            ResponseEntity<Void> response = controller.submit();

            assertThat(response.getStatusCode().value()).isEqualTo(204);
            verify(prestataireService).submitMaFiche(utilisateur);
        }
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Nested
    class Update {

        @Test
        void givenIdAndDto_whenUpdate_thenDelegatesAndReturnsNoContent() {
            when(currentUserService.get()).thenReturn(utilisateur);
            UUID id = UUID.randomUUID();
            PrestataireUpdateDto dto = new PrestataireUpdateDto(
                    "Nouveau nom", null, null, null, null, null, null, null, null, null, null, null);

            ResponseEntity<Void> response = controller.update(id, dto);

            assertThat(response.getStatusCode().value()).isEqualTo(204);
            verify(prestataireService).update(id, dto, utilisateur);
        }
    }

    // -------------------------------------------------------------------------
    // uploadMedia
    // -------------------------------------------------------------------------

    @Nested
    class UploadMedia {

        @Test
        void givenFile_whenUploadMedia_thenReturnsUploadResult() {
            when(currentUserService.get()).thenReturn(utilisateur);
            var file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});
            MediaUploadDto dto = new MediaUploadDto("uploads/abc.jpg");
            when(prestataireService.uploadMedia(utilisateur, file)).thenReturn(dto);

            assertThat(controller.uploadMedia(file).getBody()).isEqualTo(dto);
        }
    }

    // -------------------------------------------------------------------------
    // updateMedias
    // -------------------------------------------------------------------------

    @Nested
    class UpdateMedias {

        @Test
        void givenMediasBody_whenUpdateMedias_thenDelegatesAndReturnsUpdatedFiche() {
            when(currentUserService.get()).thenReturn(utilisateur);
            List<MediaDto> medias = List.of();
            MediasPutRequest body = new MediasPutRequest(medias);
            PrestataireDetailDto dto = mock(PrestataireDetailDto.class);
            when(prestataireService.updateMedias(utilisateur, medias)).thenReturn(dto);

            assertThat(controller.updateMedias(body).getBody()).isEqualTo(dto);
        }
    }
}
