package net.franzka.sgilt.core.prestataire.service;

import net.franzka.sgilt.core.jwt.domain.ActionType;
import net.franzka.sgilt.core.jwt.service.ActionLinkService;
import net.franzka.sgilt.core.prestataire.domain.MediaType;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.domain.PrestataireFlow;
import net.franzka.sgilt.core.prestataire.domain.PrestataireStatus;
import net.franzka.sgilt.core.prestataire.dto.DetailDto;
import net.franzka.sgilt.core.prestataire.dto.FaqItemDto;
import net.franzka.sgilt.core.prestataire.dto.IdentityDto;
import net.franzka.sgilt.core.prestataire.dto.MediaDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireAdminListItemDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireDetailDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireReservationCountsDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireUpdateDto;
import net.franzka.sgilt.core.prestataire.dto.TestimonialDto;
import net.franzka.sgilt.core.prestataire.exception.MediasInvalidException;
import net.franzka.sgilt.core.prestataire.exception.PrestataireForbiddenException;
import net.franzka.sgilt.core.prestataire.exception.PrestataireInvalidStateException;
import net.franzka.sgilt.core.prestataire.exception.PrestataireNotFoundException;
import net.franzka.sgilt.core.prestataire.mailer.PrestataireMailerService;
import net.franzka.sgilt.core.prestataire.mapper.PrestataireMapper;
import net.franzka.sgilt.core.prestataire.repository.PrestataireRepository;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.service.ReservationService;
import net.franzka.sgilt.core.storage.FileStorageException;
import net.franzka.sgilt.core.storage.FileStorageService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
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

    @Mock
    private ReservationService reservationService;

    @Mock
    private ActionLinkService actionLinkService;

    @Mock
    private PrestataireMailerService prestataireMailerService;

    @InjectMocks
    private PrestataireService prestataireService;

    private static final String SLUG = "photographe-jean";
    private static final String ACTION_URL = "https://sgilt.fr/onboarding/verify?token=abc";

    private final Utilisateur utilisateur = Utilisateur.builder()
            .id(UUID.randomUUID()).email("pro@sgilt.fr").firstName("Jean").build();

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
    // getPublishedById
    // -------------------------------------------------------------------------

    @Nested
    class GetPublishedById {

        @Test
        void givenPublishedPrestataire_whenGetPublishedById_thenReturnsPrestataire() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.PUBLISHED);
            when(prestataireRepository.findByIdAndStatusAndDeletedAtIsNull(prestataire.getId(), PrestataireStatus.PUBLISHED))
                    .thenReturn(Optional.of(prestataire));

            assertThat(prestataireService.getPublishedById(prestataire.getId())).isEqualTo(prestataire);
        }

        @Test
        void givenNoPublishedPrestataireForId_whenGetPublishedById_thenThrowsNotFound() {
            // la requête filtre déjà par statut PUBLISHED et deletedAt IS NULL : id inconnu,
            // non publié (DRAFT/IN_REVIEW) et soft-deleted produisent tous Optional.empty() ici —
            // même exception dans les 3 cas, pour ne pas exposer la distinction à l'appelant
            UUID id = UUID.randomUUID();
            when(prestataireRepository.findByIdAndStatusAndDeletedAtIsNull(id, PrestataireStatus.PUBLISHED))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> prestataireService.getPublishedById(id))
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
    class CreatePrestataireCleEnMain {

        @Test
        void givenCleEnMainFlow_whenCreatePrestataire_thenStatusIsWaitingForCreationServiceAndNoMailSent() {
            ArgumentCaptor<Prestataire> captor = ArgumentCaptor.forClass(Prestataire.class);
            when(prestataireRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            PrestataireService.CreationResult result = prestataireService.createPrestataireCleEnMain(
                    utilisateur, SLUG, "Jean Photographe", "photo", List.of());

            assertThat(captor.getValue().getStatus()).isEqualTo(PrestataireStatus.WAITING_FOR_CREATION_SERVICE);
            assertThat(captor.getValue().getFlow()).isEqualTo(PrestataireFlow.CREATION_CLE_EN_MAIN);
            assertThat(result.notificationDelivered()).isTrue();
            verify(prestataireMailerService, never()).sendPrestataireOnboardingEmail(any(), any(), any());
        }
    }

    @Nested
    class CreatePrestataireAutonome {

        @Test
        void givenAutonomeFlow_whenCreatePrestataire_thenStatusIsDraftAndOnboardingMailSent() {
            ArgumentCaptor<Prestataire> captor = ArgumentCaptor.forClass(Prestataire.class);
            when(prestataireRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
            when(actionLinkService.createLink(ActionType.PRESTATAIRE_ONBOARDING, Map.of("email", utilisateur.getEmail())))
                    .thenReturn(ACTION_URL);
            when(prestataireMailerService.sendPrestataireOnboardingEmail(utilisateur.getEmail(), "Jean", ACTION_URL))
                    .thenReturn(true);

            PrestataireService.CreationResult result = prestataireService.createPrestataireAutonome(
                    utilisateur, SLUG, "Jean Photographe", "photo", List.of());

            assertThat(captor.getValue().getStatus()).isEqualTo(PrestataireStatus.DRAFT);
            assertThat(captor.getValue().getFlow()).isEqualTo(PrestataireFlow.CREATION_AUTONOME);
            assertThat(result.notificationDelivered()).isTrue();
        }

        @Test
        void givenMailerFailure_whenCreatePrestataire_thenNotificationDeliveredIsFalse() {
            when(prestataireRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(actionLinkService.createLink(ActionType.PRESTATAIRE_ONBOARDING, Map.of("email", utilisateur.getEmail())))
                    .thenReturn(ACTION_URL);
            when(prestataireMailerService.sendPrestataireOnboardingEmail(utilisateur.getEmail(), "Jean", ACTION_URL))
                    .thenReturn(false);

            PrestataireService.CreationResult result = prestataireService.createPrestataireAutonome(
                    utilisateur, SLUG, "Jean Photographe", "photo", List.of());

            assertThat(result.notificationDelivered()).isFalse();
        }
    }

    // -------------------------------------------------------------------------
    // publish
    // -------------------------------------------------------------------------

    @Nested
    class Publish {

        @Test
        void givenInReviewPrestataire_whenPublish_thenStatusBecomesPublishedAndSendsPublishedEmail() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.IN_REVIEW, PrestataireFlow.CREATION_AUTONOME);
            when(prestataireRepository.findById(prestataire.getId())).thenReturn(Optional.of(prestataire));
            when(prestataireMailerService.sendPrestatairePublishedEmail(utilisateur.getEmail(), "Jean", SLUG))
                    .thenReturn(true);

            boolean delivered = prestataireService.publish(prestataire.getId());

            assertThat(prestataire.getStatus()).isEqualTo(PrestataireStatus.PUBLISHED);
            assertThat(delivered).isTrue();
            verify(prestataireRepository).save(prestataire);
            verify(prestataireMailerService, never()).sendPrestatairePageReadyEmail(any(), any(), any(), any());
        }

        @Test
        void givenWaitingForCreationServicePrestataire_whenPublish_thenStatusBecomesPublishedAndSendsPageReadyEmail() {
            Prestataire prestataire = prestataireWith(
                    PrestataireStatus.WAITING_FOR_CREATION_SERVICE, PrestataireFlow.CREATION_CLE_EN_MAIN);
            when(prestataireRepository.findById(prestataire.getId())).thenReturn(Optional.of(prestataire));
            when(actionLinkService.createLink(ActionType.PRESTATAIRE_ONBOARDING, Map.of("email", utilisateur.getEmail())))
                    .thenReturn(ACTION_URL);
            when(prestataireMailerService.sendPrestatairePageReadyEmail(utilisateur.getEmail(), "Jean", ACTION_URL, SLUG))
                    .thenReturn(true);

            boolean delivered = prestataireService.publish(prestataire.getId());

            assertThat(prestataire.getStatus()).isEqualTo(PrestataireStatus.PUBLISHED);
            assertThat(delivered).isTrue();
            verify(prestataireRepository).save(prestataire);
            verify(prestataireMailerService, never()).sendPrestatairePublishedEmail(any(), any(), any());
        }

        @Test
        void givenMailerFailure_whenPublish_thenNotificationDeliveredIsFalse() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.IN_REVIEW, PrestataireFlow.CREATION_AUTONOME);
            when(prestataireRepository.findById(prestataire.getId())).thenReturn(Optional.of(prestataire));
            when(prestataireMailerService.sendPrestatairePublishedEmail(utilisateur.getEmail(), "Jean", SLUG))
                    .thenReturn(false);

            boolean delivered = prestataireService.publish(prestataire.getId());

            assertThat(delivered).isFalse();
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
    // resendOnboardingEmail
    // -------------------------------------------------------------------------

    @Nested
    class ResendOnboardingEmail {

        @Test
        void givenAutonomeFlow_whenResendOnboardingEmail_thenSendsOnboardingEmail() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.DRAFT, PrestataireFlow.CREATION_AUTONOME);

            when(prestataireMailerService.sendPrestataireOnboardingEmail(utilisateur.getEmail(), "Jean", ACTION_URL))
                    .thenReturn(true);

            boolean delivered = prestataireService.resendOnboardingEmail(prestataire, ACTION_URL);

            assertThat(delivered).isTrue();
            verify(prestataireMailerService, never()).sendPrestatairePageReadyEmail(any(), any(), any(), any());
        }

        @Test
        void givenCleEnMainFlow_whenResendOnboardingEmail_thenSendsPageReadyEmail() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.PUBLISHED, PrestataireFlow.CREATION_CLE_EN_MAIN);

            when(prestataireMailerService.sendPrestatairePageReadyEmail(utilisateur.getEmail(), "Jean", ACTION_URL, SLUG))
                    .thenReturn(true);

            boolean delivered = prestataireService.resendOnboardingEmail(prestataire, ACTION_URL);

            assertThat(delivered).isTrue();
            verify(prestataireMailerService, never()).sendPrestataireOnboardingEmail(any(), any(), any());
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
            PrestataireAdminListItemDto draftDto = new PrestataireAdminListItemDto(
                    draft.getId(), "Jean", SLUG, PrestataireStatus.DRAFT, "pro@sgilt.fr", "photo", List.of(), emptyCounts());
            PrestataireAdminListItemDto publishedDto = new PrestataireAdminListItemDto(
                    published.getId(), "Jean", SLUG, PrestataireStatus.PUBLISHED, "pro@sgilt.fr", "photo", List.of(), emptyCounts());
            when(prestataireRepository.findConfirmedByDeletedAtIsNull()).thenReturn(List.of(draft, published));
            when(reservationService.getStatusCountsByPrestataire(any())).thenReturn(Map.of());
            when(prestataireMapper.toAdminListItemDto(eq(draft), any())).thenReturn(draftDto);
            when(prestataireMapper.toAdminListItemDto(eq(published), any())).thenReturn(publishedDto);

            List<PrestataireAdminListItemDto> result = prestataireService.getConfirmedPrestataires();

            assertThat(result).containsExactly(draftDto, publishedDto);
        }

        @Test
        void givenPrestataireWithReservations_whenGetConfirmedPrestataires_thenBuildsCountsFromReservationService() {
            Prestataire published = prestataireWith(PrestataireStatus.PUBLISHED);
            when(prestataireRepository.findConfirmedByDeletedAtIsNull()).thenReturn(List.of(published));
            when(reservationService.getStatusCountsByPrestataire(published.getId())).thenReturn(Map.of(
                    ReservationStatus.NEW, 2,
                    ReservationStatus.IN_DISCUSSION, 1
            ));
            ArgumentCaptor<PrestataireReservationCountsDto> countsCaptor =
                    ArgumentCaptor.forClass(PrestataireReservationCountsDto.class);
            when(prestataireMapper.toAdminListItemDto(eq(published), countsCaptor.capture()))
                    .thenReturn(new PrestataireAdminListItemDto(
                            published.getId(), "Jean", SLUG, PrestataireStatus.PUBLISHED, "pro@sgilt.fr",
                            "photo", List.of(), emptyCounts()));

            prestataireService.getConfirmedPrestataires();

            assertThat(countsCaptor.getValue().nouvelleCount()).isEqualTo(2);
            assertThat(countsCaptor.getValue().inDiscussionCount()).isEqualTo(1);
            assertThat(countsCaptor.getValue().confirmedCount()).isZero();
        }
    }

    // -------------------------------------------------------------------------
    // getEntityByUtilisateurOwner
    // -------------------------------------------------------------------------

    @Nested
    class GetEntityByUtilisateurOwner {

        @Test
        void givenLinkedPrestataire_whenGetEntityByUtilisateurOwner_thenReturnsEntity() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.DRAFT);
            when(prestataireRepository.findByUtilisateurAndDeletedAtIsNull(utilisateur)).thenReturn(Optional.of(prestataire));

            assertThat(prestataireService.getEntityByUtilisateurOwner(utilisateur)).isEqualTo(prestataire);
        }

        @Test
        void givenNoLinkedPrestataire_whenGetEntityByUtilisateurOwner_thenThrowsNotFound() {
            when(prestataireRepository.findByUtilisateurAndDeletedAtIsNull(utilisateur)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> prestataireService.getEntityByUtilisateurOwner(utilisateur))
                    .isInstanceOf(PrestataireNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Nested
    class Update {

        private final PrestataireUpdateDto patch = new PrestataireUpdateDto(
                "Nouveau nom", null, null, null, null, null, null, null, null, null, null, null);

        @Test
        void givenOwner_whenUpdate_thenMapsPatchAndSaves() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.DRAFT);
            when(prestataireRepository.findById(prestataire.getId())).thenReturn(Optional.of(prestataire));

            prestataireService.update(prestataire.getId(), patch, utilisateur);

            verify(prestataireMapper).updatePrestataire(prestataire, patch);
            verify(prestataireRepository).save(prestataire);
        }

        @Test
        void givenSoftDeletedPrestataire_whenUpdate_thenThrowsNotFound() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.DRAFT);
            prestataire.setDeletedAt(java.time.LocalDateTime.now());
            when(prestataireRepository.findById(prestataire.getId())).thenReturn(Optional.of(prestataire));

            assertThatThrownBy(() -> prestataireService.update(prestataire.getId(), patch, utilisateur))
                    .isInstanceOf(PrestataireNotFoundException.class);
            verify(prestataireRepository, never()).save(any());
        }

        @Test
        void givenNotOwner_whenUpdate_thenThrowsForbidden() {
            Utilisateur other = Utilisateur.builder().id(UUID.randomUUID()).email("autre@sgilt.fr").build();
            Prestataire prestataire = prestataireWith(PrestataireStatus.DRAFT);
            when(prestataireRepository.findById(prestataire.getId())).thenReturn(Optional.of(prestataire));

            assertThatThrownBy(() -> prestataireService.update(prestataire.getId(), patch, other))
                    .isInstanceOf(PrestataireForbiddenException.class);
            verify(prestataireRepository, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // existsBySlug
    // -------------------------------------------------------------------------

    @Nested
    class ExistsBySlug {

        @Test
        void givenExistingSlug_whenExistsBySlug_thenReturnsTrue() {
            when(prestataireRepository.existsBySlug(SLUG)).thenReturn(true);

            assertThat(prestataireService.existsBySlug(SLUG)).isTrue();
        }

        @Test
        void givenUnknownSlug_whenExistsBySlug_thenReturnsFalse() {
            when(prestataireRepository.existsBySlug(SLUG)).thenReturn(false);

            assertThat(prestataireService.existsBySlug(SLUG)).isFalse();
        }
    }

    // -------------------------------------------------------------------------
    // getSlugByUtilisateur
    // -------------------------------------------------------------------------

    @Nested
    class GetSlugByUtilisateur {

        @Test
        void givenLinkedPrestataire_whenGetSlugByUtilisateur_thenReturnsSlug() {
            Prestataire prestataire = prestataireWith(PrestataireStatus.DRAFT);
            when(prestataireRepository.findByUtilisateurAndDeletedAtIsNull(utilisateur)).thenReturn(Optional.of(prestataire));

            assertThat(prestataireService.getSlugByUtilisateur(utilisateur)).isEqualTo(SLUG);
        }

        @Test
        void givenNoLinkedPrestataire_whenGetSlugByUtilisateur_thenReturnsNull() {
            when(prestataireRepository.findByUtilisateurAndDeletedAtIsNull(utilisateur)).thenReturn(Optional.empty());

            assertThat(prestataireService.getSlugByUtilisateur(utilisateur)).isNull();
        }
    }

    // -------------------------------------------------------------------------
    // linkBootstrapUtilisateur
    // -------------------------------------------------------------------------

    @Nested
    class LinkBootstrapUtilisateur {

        @Test
        void givenPrestataireLinkedToDifferentUtilisateur_whenLinkBootstrapUtilisateur_thenRelinksAndSaves() {
            Utilisateur ancien = Utilisateur.builder().id(UUID.randomUUID()).email("ancien@sgilt.fr").build();
            Prestataire prestataire = Prestataire.builder().id(UUID.randomUUID()).utilisateur(ancien).slug(SLUG).build();
            when(prestataireRepository.findBySlugAndDeletedAtIsNull(SLUG)).thenReturn(Optional.of(prestataire));

            prestataireService.linkBootstrapUtilisateur(SLUG, utilisateur);

            assertThat(prestataire.getUtilisateur()).isEqualTo(utilisateur);
            verify(prestataireRepository).save(prestataire);
        }

        @Test
        void givenPrestataireAlreadyLinkedToUtilisateur_whenLinkBootstrapUtilisateur_thenDoesNothing() {
            Prestataire prestataire = Prestataire.builder().id(UUID.randomUUID()).utilisateur(utilisateur).slug(SLUG).build();
            when(prestataireRepository.findBySlugAndDeletedAtIsNull(SLUG)).thenReturn(Optional.of(prestataire));

            prestataireService.linkBootstrapUtilisateur(SLUG, utilisateur);

            verify(prestataireRepository, never()).save(any());
        }

        @Test
        void givenUnknownSlug_whenLinkBootstrapUtilisateur_thenThrowsNotFound() {
            when(prestataireRepository.findBySlugAndDeletedAtIsNull(SLUG)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> prestataireService.linkBootstrapUtilisateur(SLUG, utilisateur))
                    .isInstanceOf(PrestataireNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // uploadMedia
    // -------------------------------------------------------------------------

    @Nested
    class UploadMedia {

        private final MockMultipartFile file =
                new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});

        @Test
        void givenValidFile_whenUploadMedia_thenReturnsStorageKey() throws IOException {
            when(fileStorageService.upload(file, "uploads")).thenReturn("uploads/abc.jpg");

            var result = prestataireService.uploadMedia(utilisateur, file);

            assertThat(result.key()).isEqualTo("uploads/abc.jpg");
        }

        @Test
        void givenStorageFailure_whenUploadMedia_thenThrowsFileStorageException() throws IOException {
            when(fileStorageService.upload(file, "uploads")).thenThrow(new IOException("disk full"));

            assertThatThrownBy(() -> prestataireService.uploadMedia(utilisateur, file))
                    .isInstanceOf(FileStorageException.class);
        }
    }

    // -------------------------------------------------------------------------
    // updateMedias
    // -------------------------------------------------------------------------

    @Nested
    class UpdateMedias {

        @Test
        void givenHeroImageAtPositionZero_whenUpdateMedias_thenSavesAndReturnsDto() {
            List<MediaDto> medias = List.of(
                    new MediaDto(MediaType.IMAGE, "img1.jpg", 0),
                    new MediaDto(MediaType.YOUTUBE, "abc123", 1));
            Prestataire prestataire = prestataireWith(PrestataireStatus.DRAFT);
            PrestataireDetailDto dto = dummyDetailDto();
            when(prestataireRepository.findByUtilisateurAndDeletedAtIsNull(utilisateur)).thenReturn(Optional.of(prestataire));
            when(prestataireRepository.save(prestataire)).thenReturn(prestataire);
            when(prestataireMapper.toDetailDto(prestataire)).thenReturn(dto);

            PrestataireDetailDto result = prestataireService.updateMedias(utilisateur, medias);

            assertThat(result).isEqualTo(dto);
            assertThat(prestataire.getMedias()).contains("img1.jpg").contains("abc123");
        }

        @Test
        void givenNoImageAtPositionZero_whenUpdateMedias_thenThrowsMediasInvalid() {
            List<MediaDto> medias = List.of(new MediaDto(MediaType.YOUTUBE, "abc123", 0));

            assertThatThrownBy(() -> prestataireService.updateMedias(utilisateur, medias))
                    .isInstanceOf(MediasInvalidException.class);
            verify(prestataireRepository, never()).save(any());
        }

        @Test
        void givenEmptyMediaList_whenUpdateMedias_thenThrowsMediasInvalid() {
            assertThatThrownBy(() -> prestataireService.updateMedias(utilisateur, List.of()))
                    .isInstanceOf(MediasInvalidException.class);
        }
    }

    // -------------------------------------------------------------------------
    // Application de contenu généré (replace/append)
    // -------------------------------------------------------------------------

    @Nested
    class ContentApplication {

        private Prestataire prestataire;
        private PrestataireDetailDto dto;

        @BeforeEach
        void setUp() {
            prestataire = prestataireWith(PrestataireStatus.DRAFT);
            dto = dummyDetailDto();
            lenient().when(prestataireRepository.save(prestataire)).thenReturn(prestataire);
            lenient().when(prestataireMapper.toDetailDto(prestataire)).thenReturn(dto);
        }

        @Test
        void givenBaseline_whenReplaceBaseline_thenSetsFieldAndSaves() {
            assertThat(prestataireService.replaceBaseline(prestataire, "Nouvelle baseline")).isEqualTo(dto);
            assertThat(prestataire.getBaseline()).isEqualTo("Nouvelle baseline");
        }

        @Test
        void givenShortDescription_whenReplaceShortDescription_thenSetsFieldAndSaves() {
            assertThat(prestataireService.replaceShortDescription(prestataire, "Résumé")).isEqualTo(dto);
            assertThat(prestataire.getShortDescription()).isEqualTo("Résumé");
        }

        @Test
        void givenIdentity_whenReplaceIdentity_thenSerializesAndSaves() {
            IdentityDto identity = new IdentityDto("Citation", "Bio");
            assertThat(prestataireService.replaceIdentity(prestataire, identity)).isEqualTo(dto);
            assertThat(prestataire.getIdentity()).contains("Citation").contains("Bio");
        }

        @Test
        void givenBudget_whenReplaceBudget_thenSerializesAndSaves() {
            assertThat(prestataireService.replaceBudget(prestataire, "500-1000€")).isEqualTo(dto);
            assertThat(prestataire.getBudget()).contains("500-1000");
        }

        @Test
        void givenOfferings_whenReplaceOfferings_thenSerializesAndSaves() {
            assertThat(prestataireService.replaceOfferings(prestataire, List.of("Offre A"))).isEqualTo(dto);
            assertThat(prestataire.getOfferings()).contains("Offre A");
        }

        @Test
        void givenExistingOfferings_whenAppendOfferings_thenConcatenatesToExisting() {
            prestataire.setOfferings("[\"Offre A\"]");
            assertThat(prestataireService.appendOfferings(prestataire, List.of("Offre B"))).isEqualTo(dto);
            assertThat(prestataire.getOfferings()).contains("Offre A").contains("Offre B");
        }

        @Test
        void givenNoExistingOfferings_whenAppendOfferings_thenListContainsOnlyAppended() {
            prestataire.setOfferings(null);
            assertThat(prestataireService.appendOfferings(prestataire, List.of("Offre B"))).isEqualTo(dto);
            assertThat(prestataire.getOfferings()).contains("Offre B");
        }

        @Test
        void givenTestimonials_whenReplaceTestimonials_thenSerializesAndSaves() {
            List<TestimonialDto> testimonials = List.of(new TestimonialDto("Marie", "Super prestataire"));
            assertThat(prestataireService.replaceTestimonials(prestataire, testimonials)).isEqualTo(dto);
            assertThat(prestataire.getTestimonials()).contains("Marie");
        }

        @Test
        void givenExistingTestimonials_whenAppendTestimonials_thenConcatenatesToExisting() {
            prestataire.setTestimonials("[{\"author\":\"Marie\",\"text\":\"Top\"}]");
            List<TestimonialDto> toAppend = List.of(new TestimonialDto("Paul", "Génial"));

            assertThat(prestataireService.appendTestimonials(prestataire, toAppend)).isEqualTo(dto);
            assertThat(prestataire.getTestimonials()).contains("Marie").contains("Paul");
        }

        @Test
        void givenDetails_whenReplaceDetails_thenSerializesAndSaves() {
            List<DetailDto> details = List.of(new DetailDto("Contenu", null));
            assertThat(prestataireService.replaceDetails(prestataire, details)).isEqualTo(dto);
            assertThat(prestataire.getDetails()).contains("Contenu");
        }

        @Test
        void givenExistingDetails_whenAppendDetails_thenConcatenatesToExisting() {
            prestataire.setDetails("[{\"content\":\"Existant\"}]");
            List<DetailDto> toAppend = List.of(new DetailDto("Nouveau", null));

            assertThat(prestataireService.appendDetails(prestataire, toAppend)).isEqualTo(dto);
            assertThat(prestataire.getDetails()).contains("Existant").contains("Nouveau");
        }

        @Test
        void givenFaq_whenReplaceFaq_thenSerializesAndSaves() {
            List<FaqItemDto> faq = List.of(new FaqItemDto("Question ?", "Réponse."));
            assertThat(prestataireService.replaceFaq(prestataire, faq)).isEqualTo(dto);
            assertThat(prestataire.getFaq()).contains("Question ?");
        }

        @Test
        void givenExistingFaq_whenAppendFaq_thenConcatenatesToExisting() {
            prestataire.setFaq("[{\"question\":\"Q1\",\"answer\":\"R1\"}]");
            List<FaqItemDto> toAppend = List.of(new FaqItemDto("Q2", "R2"));

            assertThat(prestataireService.appendFaq(prestataire, toAppend)).isEqualTo(dto);
            assertThat(prestataire.getFaq()).contains("Q1").contains("Q2");
        }

        @Test
        void givenCorruptedStoredJson_whenAppendOfferings_thenThrowsIllegalStateException() {
            prestataire.setOfferings("{invalide");

            assertThatThrownBy(() -> prestataireService.appendOfferings(prestataire, List.of("Offre B")))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    // -------------------------------------------------------------------------
    // search — compteurs par catégorie et sous-catégorie
    // -------------------------------------------------------------------------

    @Nested
    class SearchCounts {

        @Test
        void givenPrestatairesInDifferentCategories_whenSearchWithoutFilter_thenCountsPerCategoryAndTotal() {
            Prestataire photo1 = Prestataire.builder().id(UUID.randomUUID()).categoryKey("photo").subcatKeys(List.of()).build();
            Prestataire photo2 = Prestataire.builder().id(UUID.randomUUID()).categoryKey("photo").subcatKeys(List.of()).build();
            Prestataire musique = Prestataire.builder().id(UUID.randomUUID()).categoryKey("musique").subcatKeys(List.of()).build();
            when(prestataireRepository.findByStatusAndDeletedAtIsNull(PrestataireStatus.PUBLISHED))
                    .thenReturn(List.of(photo1, photo2, musique));

            var result = prestataireService.search(null, null);

            assertThat(result.countsByCategory()).containsEntry("all", 3L).containsEntry("photo", 2L).containsEntry("musique", 1L);
            assertThat(result.subcatCounts()).isEmpty();
        }

        @Test
        void givenSubcatFilterMatchingPrestataires_whenSearch_thenCountsSubcatsForActiveCategory() {
            List<String> subcats = List.of("dj");
            Prestataire dj = Prestataire.builder().id(UUID.randomUUID())
                    .categoryKey("musique").subcatKeys(List.of("dj", "orchestre")).build();
            when(prestataireRepository.findByStatusAndDeletedAtIsNull(PrestataireStatus.PUBLISHED)).thenReturn(List.of(dj));
            when(prestataireRepository.findBySubcatKeysInAndStatusAndDeletedAtIsNull(subcats, PrestataireStatus.PUBLISHED))
                    .thenReturn(List.of(dj));

            var result = prestataireService.search(null, subcats);

            assertThat(result.subcatCounts()).containsEntry("dj", 1L).containsEntry("orchestre", 1L);
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

    private Prestataire prestataireWith(PrestataireStatus status, PrestataireFlow flow) {
        return Prestataire.builder()
                .id(UUID.randomUUID())
                .utilisateur(utilisateur)
                .slug(SLUG)
                .status(status)
                .flow(flow)
                .build();
    }

    private PrestataireReservationCountsDto emptyCounts() {
        return new PrestataireReservationCountsDto(0, 0, 0, 0, 0, 0);
    }

    private PrestataireDetailDto dummyDetailDto() {
        return new PrestataireDetailDto(
                UUID.randomUUID().toString(), "Jean Photographe", SLUG, null, null, null, null, null, "photo",
                List.of(), List.of(), List.of(), List.of(), null, null, List.of(), List.of(), List.of(),
                PrestataireStatus.PUBLISHED
        );
    }
}
