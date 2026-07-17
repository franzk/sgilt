package net.franzka.sgilt.core.prestataire.service;

import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.domain.PrestataireStatus;
import net.franzka.sgilt.core.prestataire.dto.PrestataireAdminListItemDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireDetailDto;
import net.franzka.sgilt.core.prestataire.exception.PrestataireInvalidStateException;
import net.franzka.sgilt.core.prestataire.exception.PrestataireNotFoundException;
import net.franzka.sgilt.core.prestataire.mapper.PrestataireMapper;
import net.franzka.sgilt.core.prestataire.repository.PrestataireRepository;
import net.franzka.sgilt.core.storage.FileStorageService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrestataireServiceTest {

    @Mock
    private PrestataireRepository prestataireRepository;

    @Mock
    private PrestataireMapper prestataireMapper;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private PrestataireService prestataireService;

    private static final String SLUG = "photographe-jean";

    private final Utilisateur utilisateur = Utilisateur.builder().id(UUID.randomUUID()).email("pro@sgilt.fr").build();

    // -------------------------------------------------------------------------
    // getBySlug
    // -------------------------------------------------------------------------

    @Nested
    class GetBySlug {

        @Test
        void givenPublishedPrestataire_whenGetBySlug_thenReturnsDto() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.PUBLISHED);
            PrestataireDetailDto dto = dummyDetailDto();
            when(prestataireRepository.findBySlugAndStatusAndDeletedAtIsNull(SLUG, PrestataireStatus.PUBLISHED))
                    .thenReturn(Optional.of(prestataire));
            when(prestataireMapper.toDetailDto(prestataire)).thenReturn(dto);

            PrestataireDetailDto result = prestataireService.getBySlug(SLUG);

            assertThat(result).isEqualTo(dto);
        }

        @Test
        void givenNoPublishedPrestataireForSlug_whenGetBySlug_thenThrowsNotFound() {
            when(prestataireRepository.findBySlugAndStatusAndDeletedAtIsNull(SLUG, PrestataireStatus.PUBLISHED))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> prestataireService.getBySlug(SLUG))
                    .isInstanceOf(PrestataireNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // getMaFiche
    // -------------------------------------------------------------------------

    @Nested
    class GetByUtilisateurOwner {

        @Test
        void givenDraftPrestataire_whenGetByUtilisateurOwner_thenReturnsDto() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.DRAFT);
            PrestataireDetailDto dto = dummyDetailDto();
            when(prestataireRepository.findByUtilisateurAndDeletedAtIsNull(utilisateur)).thenReturn(Optional.of(prestataire));
            when(prestataireMapper.toDetailDto(prestataire)).thenReturn(dto);

            assertThat(prestataireService.getByUtilisateurOwner(utilisateur)).isEqualTo(dto);
        }

        @Test
        void givenNoLinkedPrestataire_whenGetByUtilisateurOwner_thenThrowsNotFound() {
            when(prestataireRepository.findByUtilisateurAndDeletedAtIsNull(utilisateur)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> prestataireService.getByUtilisateurOwner(utilisateur))
                    .isInstanceOf(PrestataireNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // getByUtilisateurEmail
    // -------------------------------------------------------------------------

    @Nested
    class GetByUtilisateurEmail {

        @Test
        void givenPrestataireLinkedToEmail_whenGetByUtilisateurEmail_thenReturnsPrestataire() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.DRAFT);
            when(prestataireRepository.findByUtilisateur_EmailAndDeletedAtIsNull(utilisateur.getEmail()))
                    .thenReturn(Optional.of(prestataire));

            assertThat(prestataireService.getByUtilisateurEmail(utilisateur.getEmail())).isEqualTo(prestataire);
        }

        @Test
        void givenNoPrestataireForEmail_whenGetByUtilisateurEmail_thenThrowsNotFound() {
            when(prestataireRepository.findByUtilisateur_EmailAndDeletedAtIsNull(utilisateur.getEmail()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> prestataireService.getByUtilisateurEmail(utilisateur.getEmail()))
                    .isInstanceOf(PrestataireNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // submitMaFiche
    // -------------------------------------------------------------------------

    @Nested
    class SubmitMaFiche {

        @Test
        void givenDraftPrestataire_whenSubmitMaFiche_thenStatusBecomesInReview() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.DRAFT);
            when(prestataireRepository.findByUtilisateurAndDeletedAtIsNull(utilisateur)).thenReturn(Optional.of(prestataire));

            prestataireService.submitMaFiche(utilisateur);

            assertThat(prestataire.getStatus()).isEqualTo(PrestataireStatus.IN_REVIEW);
            verify(prestataireRepository).save(prestataire);
        }

        @Test
        void givenInReviewPrestataire_whenSubmitMaFiche_thenThrowsInvalidState() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.IN_REVIEW);
            when(prestataireRepository.findByUtilisateurAndDeletedAtIsNull(utilisateur)).thenReturn(Optional.of(prestataire));

            assertThatThrownBy(() -> prestataireService.submitMaFiche(utilisateur))
                    .isInstanceOf(PrestataireInvalidStateException.class);
            verify(prestataireRepository, never()).save(any());
        }

        @Test
        void givenPublishedPrestataire_whenSubmitMaFiche_thenThrowsInvalidState() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.PUBLISHED);
            when(prestataireRepository.findByUtilisateurAndDeletedAtIsNull(utilisateur)).thenReturn(Optional.of(prestataire));

            assertThatThrownBy(() -> prestataireService.submitMaFiche(utilisateur))
                    .isInstanceOf(PrestataireInvalidStateException.class);
            verify(prestataireRepository, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // search
    // -------------------------------------------------------------------------

    @Nested
    class Search {

        @Test
        void givenNoFilters_whenSearch_thenQueriesOnlyPublishedStatus() {
            when(prestataireRepository.findByStatusAndDeletedAtIsNull(PrestataireStatus.PUBLISHED)).thenReturn(List.of());

            prestataireService.search(null, null);

            verify(prestataireRepository, atLeastOnce()).findByStatusAndDeletedAtIsNull(PrestataireStatus.PUBLISHED);
        }

        @Test
        void givenCategoryFilter_whenSearch_thenQueriesCategoryWithPublishedStatus() {
            when(prestataireRepository.findByStatusAndDeletedAtIsNull(PrestataireStatus.PUBLISHED)).thenReturn(List.of());
            when(prestataireRepository.findByCategoryKeyAndStatusAndDeletedAtIsNull("musique", PrestataireStatus.PUBLISHED))
                    .thenReturn(List.of());

            prestataireService.search("musique", null);

            verify(prestataireRepository).findByCategoryKeyAndStatusAndDeletedAtIsNull("musique", PrestataireStatus.PUBLISHED);
        }

        @Test
        void givenSubcatFilter_whenSearch_thenQueriesSubcatsWithPublishedStatus() {
            List<String> subcats = List.of("dj");
            when(prestataireRepository.findByStatusAndDeletedAtIsNull(PrestataireStatus.PUBLISHED)).thenReturn(List.of());
            when(prestataireRepository.findBySubcatKeysInAndStatusAndDeletedAtIsNull(subcats, PrestataireStatus.PUBLISHED))
                    .thenReturn(List.of());

            prestataireService.search(null, subcats);

            verify(prestataireRepository).findBySubcatKeysInAndStatusAndDeletedAtIsNull(subcats, PrestataireStatus.PUBLISHED);
        }
    }

    // -------------------------------------------------------------------------
    // createPrestataire
    // -------------------------------------------------------------------------

    @Nested
    class CreatePrestataire {

        @Test
        void givenNewPrestataire_whenCreatePrestataire_thenStatusIsDraft() {
            ArgumentCaptor<Prestataire> captor = ArgumentCaptor.forClass(Prestataire.class);
            when(prestataireRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            prestataireService.createPrestataire(utilisateur, SLUG, "Jean Photographe", "photo", List.of());

            assertThat(captor.getValue().getStatus()).isEqualTo(PrestataireStatus.DRAFT);
        }
    }

    // -------------------------------------------------------------------------
    // publish
    // -------------------------------------------------------------------------

    @Nested
    class Publish {

        @Test
        void givenInReviewPrestataire_whenPublish_thenStatusBecomesPublished() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.IN_REVIEW);
            when(prestataireRepository.findById(prestataire.getId())).thenReturn(Optional.of(prestataire));

            prestataireService.publish(prestataire.getId());

            assertThat(prestataire.getStatus()).isEqualTo(PrestataireStatus.PUBLISHED);
            verify(prestataireRepository).save(prestataire);
        }

        @Test
        void givenDraftPrestataire_whenPublish_thenThrowsInvalidState() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.DRAFT);
            when(prestataireRepository.findById(prestataire.getId())).thenReturn(Optional.of(prestataire));

            assertThatThrownBy(() -> prestataireService.publish(prestataire.getId()))
                    .isInstanceOf(PrestataireInvalidStateException.class);
            verify(prestataireRepository, never()).save(any());
        }

        @Test
        void givenPublishedPrestataire_whenPublish_thenThrowsInvalidState() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.PUBLISHED);
            when(prestataireRepository.findById(prestataire.getId())).thenReturn(Optional.of(prestataire));

            assertThatThrownBy(() -> prestataireService.publish(prestataire.getId()))
                    .isInstanceOf(PrestataireInvalidStateException.class);
            verify(prestataireRepository, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // sendBackToReview
    // -------------------------------------------------------------------------

    @Nested
    class SendBackToReview {

        @Test
        void givenPublishedPrestataire_whenSendBackToReview_thenStatusBecomesInReview() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.PUBLISHED);
            when(prestataireRepository.findById(prestataire.getId())).thenReturn(Optional.of(prestataire));

            prestataireService.sendBackToReview(prestataire.getId());

            assertThat(prestataire.getStatus()).isEqualTo(PrestataireStatus.IN_REVIEW);
            verify(prestataireRepository).save(prestataire);
        }

        @Test
        void givenDraftPrestataire_whenSendBackToReview_thenThrowsInvalidState() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.DRAFT);
            when(prestataireRepository.findById(prestataire.getId())).thenReturn(Optional.of(prestataire));

            assertThatThrownBy(() -> prestataireService.sendBackToReview(prestataire.getId()))
                    .isInstanceOf(PrestataireInvalidStateException.class);
            verify(prestataireRepository, never()).save(any());
        }

        @Test
        void givenInReviewPrestataire_whenSendBackToReview_thenThrowsInvalidState() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.IN_REVIEW);
            when(prestataireRepository.findById(prestataire.getId())).thenReturn(Optional.of(prestataire));

            assertThatThrownBy(() -> prestataireService.sendBackToReview(prestataire.getId()))
                    .isInstanceOf(PrestataireInvalidStateException.class);
            verify(prestataireRepository, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // getAllForAdmin
    // -------------------------------------------------------------------------

    @Nested
    class GetConfirmedPrestataires {

        @Test
        void givenConfirmedPrestatairesWithMixedStatuses_whenGetConfirmedPrestataires_thenReturnsAllRegardlessOfStatus() {
            Prestataire draft = prestataireWith(PrestataireStatus.DRAFT);
            Prestataire published = prestataireWith(PrestataireStatus.PUBLISHED);
            PrestataireAdminListItemDto draftDto =
                    new PrestataireAdminListItemDto(draft.getId(), "Jean", SLUG, PrestataireStatus.DRAFT, "pro@sgilt.fr");
            PrestataireAdminListItemDto publishedDto =
                    new PrestataireAdminListItemDto(published.getId(), "Jean", SLUG, PrestataireStatus.PUBLISHED, "pro@sgilt.fr");
            when(prestataireRepository.findConfirmedByDeletedAtIsNull()).thenReturn(List.of(draft, published));
            when(prestataireMapper.toAdminListItemDto(draft)).thenReturn(draftDto);
            when(prestataireMapper.toAdminListItemDto(published)).thenReturn(publishedDto);

            List<PrestataireAdminListItemDto> result = prestataireService.getConfirmedPrestataires();

            assertThat(result).containsExactly(draftDto, publishedDto);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Prestataire prestataireWith(PrestataireStatus status) {
        return Prestataire.builder()
                .id(UUID.randomUUID())
                .utilisateur(utilisateur)
                .slug(SLUG)
                .status(status)
                .build();
    }

    private PrestataireDetailDto dummyDetailDto() {
        return new PrestataireDetailDto(
                UUID.randomUUID().toString(), "Jean Photographe", SLUG, null, null, null, "photo",
                List.of(), List.of(), List.of(), List.of(), null, null, List.of(), List.of(), List.of(),
                PrestataireStatus.PUBLISHED
        );
    }
}
