package net.franzka.sgilt.core.prestataire.mapper;

import net.franzka.sgilt.core.prestataire.domain.Engagement;
import net.franzka.sgilt.core.prestataire.domain.MediaType;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.domain.PrestataireStatus;
import net.franzka.sgilt.core.prestataire.dto.DetailDto;
import net.franzka.sgilt.core.prestataire.dto.FaqItemDto;
import net.franzka.sgilt.core.prestataire.dto.IdentityDto;
import net.franzka.sgilt.core.prestataire.dto.MediaDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireAdminListItemDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireCardDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireDetailDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireOnboardingPendingDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireReservationCountsDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireUpdateDto;
import net.franzka.sgilt.core.prestataire.dto.TestimonialDto;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PrestataireMapperTest {

    private final PrestataireMapper mapper = new PrestataireMapperImpl();

    // -------------------------------------------------------------------------
    // toCardDto
    // -------------------------------------------------------------------------

    @Nested
    class ToCardDto {

        @Test
        void givenPrestataireWithMedias_whenToCardDto_thenResolvesHeroImageFromLowestPosition() {
            Prestataire prestataire = Prestataire.builder()
                    .id(UUID.randomUUID()).name("Studio Fleur").shortDescription("Résumé")
                    .slug("studio-fleur").categoryKey("photo")
                    .medias("[{\"type\":\"IMAGE\",\"ref\":\"second.jpg\",\"position\":1},"
                            + "{\"type\":\"IMAGE\",\"ref\":\"hero.jpg\",\"position\":0}]")
                    .build();

            PrestataireCardDto dto = mapper.toCardDto(prestataire);

            assertThat(dto.heroImage()).isEqualTo("hero.jpg");
            assertThat(dto.id()).isEqualTo(prestataire.getId().toString());
            assertThat(dto.name()).isEqualTo("Studio Fleur");
            assertThat(dto.slug()).isEqualTo("studio-fleur");
            assertThat(dto.categoryKey()).isEqualTo("photo");
        }

        @Test
        void givenNoMedias_whenToCardDto_thenHeroImageIsNull() {
            Prestataire prestataire = Prestataire.builder().id(UUID.randomUUID()).build();

            assertThat(mapper.toCardDto(prestataire).heroImage()).isNull();
        }
    }

    // -------------------------------------------------------------------------
    // toAdminListItemDto
    // -------------------------------------------------------------------------

    @Nested
    class ToAdminListItemDto {

        @Test
        void givenPrestataireWithUtilisateur_whenToAdminListItemDto_thenResolvesEmailAndFields() {
            Utilisateur utilisateur = Utilisateur.builder().email("pro@sgilt.fr").build();
            Prestataire prestataire = Prestataire.builder()
                    .id(UUID.randomUUID()).name("Studio Fleur").slug("studio-fleur")
                    .status(PrestataireStatus.PUBLISHED).categoryKey("photo")
                    .subcatKeys(List.of("mariage")).utilisateur(utilisateur).build();
            PrestataireReservationCountsDto counts = new PrestataireReservationCountsDto(1, 0, 0, 0, 0, 0);

            PrestataireAdminListItemDto dto = mapper.toAdminListItemDto(prestataire, counts);

            assertThat(dto).isEqualTo(new PrestataireAdminListItemDto(
                    prestataire.getId(), "Studio Fleur", "studio-fleur", PrestataireStatus.PUBLISHED,
                    "pro@sgilt.fr", "photo", List.of("mariage"), counts));
        }

        @Test
        void givenNullPrestataire_whenToAdminListItemDto_thenPrestataireFieldsAreNullButCountsKept() {
            PrestataireReservationCountsDto counts = new PrestataireReservationCountsDto(0, 0, 0, 0, 0, 0);

            PrestataireAdminListItemDto dto = mapper.toAdminListItemDto(null, counts);

            assertThat(dto.id()).isNull();
            assertThat(dto.reservationCounts()).isEqualTo(counts);
        }
    }

    // -------------------------------------------------------------------------
    // toOnboardingPendingDto
    // -------------------------------------------------------------------------

    @Nested
    class ToOnboardingPendingDto {

        @Test
        void givenPrestataireAndLinkDates_whenToOnboardingPendingDto_thenMapsAllFields() {
            Utilisateur utilisateur = Utilisateur.builder().email("pro@sgilt.fr").build();
            Prestataire prestataire = Prestataire.builder()
                    .id(UUID.randomUUID()).name("Studio Fleur").utilisateur(utilisateur).build();
            LocalDateTime sentAt = LocalDateTime.of(2027, 1, 1, 10, 0);
            LocalDateTime expiresAt = LocalDateTime.of(2027, 1, 8, 10, 0);

            PrestataireOnboardingPendingDto dto = mapper.toOnboardingPendingDto(prestataire, sentAt, expiresAt);

            assertThat(dto).isEqualTo(new PrestataireOnboardingPendingDto(
                    prestataire.getId(), "Studio Fleur", "pro@sgilt.fr", sentAt, expiresAt));
        }
    }

    // -------------------------------------------------------------------------
    // toDetailDto
    // -------------------------------------------------------------------------

    @Nested
    class ToDetailDto {

        @Test
        void givenPrestataireWithAllJsonbFieldsPopulated_whenToDetailDto_thenDeserializesEveryField() {
            Prestataire prestataire = Prestataire.builder()
                    .id(UUID.randomUUID()).name("Studio Fleur").slug("studio-fleur")
                    .baseline("Baseline").avatar("avatar.jpg").shortDescription("Résumé")
                    .metaTitle("Titre").metaDescription("Description").categoryKey("photo")
                    .subcatKeys(List.of("mariage"))
                    .medias("[{\"type\":\"IMAGE\",\"ref\":\"hero.jpg\",\"position\":0}]")
                    .badges("[\"REPONSE_48H\"]")
                    .offerings("[\"Offre A\"]")
                    .identity("{\"quote\":\"Citation\",\"bio\":\"Bio\"}")
                    .budget("\"500-1000\"")
                    .testimonials("[{\"author\":\"Marie\",\"text\":\"Top\"}]")
                    .details("[{\"content\":\"Detail\",\"category\":null}]")
                    .faq("[{\"question\":\"Q?\",\"answer\":\"R.\"}]")
                    .status(PrestataireStatus.PUBLISHED)
                    .build();

            PrestataireDetailDto dto = mapper.toDetailDto(prestataire);

            assertThat(dto.id()).isEqualTo(prestataire.getId().toString());
            assertThat(dto.name()).isEqualTo("Studio Fleur");
            assertThat(dto.baseline()).isEqualTo("Baseline");
            assertThat(dto.medias()).containsExactly(new MediaDto(MediaType.IMAGE, "hero.jpg", 0));
            assertThat(dto.badges()).containsExactly(Engagement.REPONSE_48H);
            assertThat(dto.offerings()).containsExactly("Offre A");
            assertThat(dto.identity()).isEqualTo(new IdentityDto("Citation", "Bio"));
            assertThat(dto.budget()).isEqualTo("500-1000");
            assertThat(dto.testimonials()).containsExactly(new TestimonialDto("Marie", "Top"));
            assertThat(dto.details()).containsExactly(new DetailDto("Detail", null));
            assertThat(dto.faq()).containsExactly(new FaqItemDto("Q?", "R."));
            assertThat(dto.status()).isEqualTo(PrestataireStatus.PUBLISHED);
        }

        @Test
        void givenPrestataireWithNoJsonbFieldsSet_whenToDetailDto_thenListFieldsAreNullOrEmpty() {
            Prestataire prestataire = Prestataire.builder().id(UUID.randomUUID()).build();

            PrestataireDetailDto dto = mapper.toDetailDto(prestataire);

            assertThat(dto.medias()).isEmpty();
            assertThat(dto.badges()).isNull();
            assertThat(dto.offerings()).isNull();
            assertThat(dto.identity()).isNull();
            assertThat(dto.testimonials()).isNull();
        }

        @Test
        void givenCorruptedOfferingsJson_whenToDetailDto_thenOfferingsIsNullRestOfDtoStillMapped() {
            Prestataire prestataire = Prestataire.builder()
                    .id(UUID.randomUUID()).name("Studio Fleur").offerings("{corrompu").build();

            PrestataireDetailDto dto = mapper.toDetailDto(prestataire);

            assertThat(dto.offerings()).isNull();
            assertThat(dto.name()).isEqualTo("Studio Fleur");
        }
    }

    // -------------------------------------------------------------------------
    // updatePrestataire
    // -------------------------------------------------------------------------

    @Nested
    class UpdatePrestataire {

        @Test
        void givenPartialUpdateDto_whenUpdatePrestataire_thenOnlyNonNullFieldsAreApplied() {
            Prestataire prestataire = Prestataire.builder()
                    .id(UUID.randomUUID()).slug("studio-fleur").name("Ancien nom").baseline("Ancienne baseline")
                    .status(PrestataireStatus.DRAFT).build();
            PrestataireUpdateDto dto = new PrestataireUpdateDto(
                    "Nouveau nom", null, null, null, null, null, null, null, null, null, null, null);

            mapper.updatePrestataire(prestataire, dto);

            assertThat(prestataire.getName()).isEqualTo("Nouveau nom");
            assertThat(prestataire.getBaseline()).isEqualTo("Ancienne baseline");
        }

        @Test
        void givenUpdateDto_whenUpdatePrestataire_thenSystemFieldsAreNeverTouched() {
            UUID id = UUID.randomUUID();
            Prestataire prestataire = Prestataire.builder()
                    .id(id).slug("studio-fleur").status(PrestataireStatus.DRAFT).build();
            PrestataireUpdateDto dto = new PrestataireUpdateDto(
                    "Nouveau nom", null, null, null, null, null, null, null, null, null, null, null);

            mapper.updatePrestataire(prestataire, dto);

            assertThat(prestataire.getId()).isEqualTo(id);
            assertThat(prestataire.getSlug()).isEqualTo("studio-fleur");
            assertThat(prestataire.getStatus()).isEqualTo(PrestataireStatus.DRAFT);
        }

        @Test
        void givenListAndObjectFields_whenUpdatePrestataire_thenSerializesToJsonb() {
            Prestataire prestataire = Prestataire.builder().id(UUID.randomUUID()).build();
            PrestataireUpdateDto dto = new PrestataireUpdateDto(
                    null, null, null, null, null, List.of(Engagement.ADAPTABLE), List.of("Offre A"),
                    new IdentityDto("Citation", "Bio"), "500-1000",
                    List.of(new TestimonialDto("Marie", "Top")),
                    List.of(new DetailDto("Detail", null)),
                    List.of(new FaqItemDto("Q?", "R.")));

            mapper.updatePrestataire(prestataire, dto);

            assertThat(prestataire.getBadges()).contains("ADAPTABLE");
            assertThat(prestataire.getOfferings()).contains("Offre A");
            assertThat(prestataire.getIdentity()).contains("Citation");
            assertThat(prestataire.getBudget()).contains("500-1000");
            assertThat(prestataire.getTestimonials()).contains("Marie");
            assertThat(prestataire.getDetails()).contains("Detail");
            assertThat(prestataire.getFaq()).contains("Q?");
        }

        @Test
        void givenNullDto_whenUpdatePrestataire_thenNothingIsChanged() {
            Prestataire prestataire = Prestataire.builder().id(UUID.randomUUID()).name("Nom").build();

            mapper.updatePrestataire(prestataire, null);

            assertThat(prestataire.getName()).isEqualTo("Nom");
        }
    }
}
