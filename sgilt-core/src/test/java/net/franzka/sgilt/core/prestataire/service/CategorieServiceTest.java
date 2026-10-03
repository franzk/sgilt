package net.franzka.sgilt.core.prestataire.service;

import net.franzka.sgilt.core.prestataire.domain.Categorie;
import net.franzka.sgilt.core.prestataire.domain.MoveDirection;
import net.franzka.sgilt.core.prestataire.domain.SousCategorieError;
import net.franzka.sgilt.core.prestataire.domain.SousCategorie;
import net.franzka.sgilt.core.prestataire.dto.CategorieAdminDto;
import net.franzka.sgilt.core.prestataire.dto.CategorieDto;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieAdminDto;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieCreateRequest;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieUpdateRequest;
import net.franzka.sgilt.core.prestataire.exception.SousCategorieInvalidException;
import net.franzka.sgilt.core.prestataire.exception.SousCategorieNotFoundException;
import net.franzka.sgilt.core.prestataire.mapper.CategorieMapper;
import net.franzka.sgilt.core.prestataire.repository.CategorieRepository;
import net.franzka.sgilt.core.prestataire.repository.SousCategorieRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategorieServiceTest {

    private static final Sort BY_POSITION = Sort.by("position");

    @Mock
    private CategorieRepository categorieRepository;

    @Mock
    private SousCategorieRepository sousCategorieRepository;

    @Mock
    private CategorieMapper categorieMapper;

    @Mock
    private PrestataireService prestataireService;

    @InjectMocks
    private CategorieService categorieService;

    private static SousCategorie sousCategorie(String key, String categoryKey, int position) {
        return SousCategorie.builder().key(key).name(key).categoryKey(categoryKey).position(position).build();
    }

    private static Categorie categorie(String key, SousCategorie... subcategories) {
        return Categorie.builder().key(key).name(key).subcategories(List.of(subcategories)).build();
    }

    private static void assertReason(Throwable thrown, SousCategorieError reason) {
        assertThat(thrown).isInstanceOf(SousCategorieInvalidException.class);
        assertThat(((SousCategorieInvalidException) thrown).getReason()).isEqualTo(reason);
    }

    // -------------------------------------------------------------------------
    // getCategories
    // -------------------------------------------------------------------------

    @Nested
    class GetCategories {

        @Test
        void givenReferential_whenGetCategories_thenMapsCategoriesSortedByPosition() {
            Categorie musique = categorie("musique");
            Categorie lieu = categorie("lieu");
            CategorieDto musiqueDto = new CategorieDto("musique", "Musique", List.of());
            CategorieDto lieuDto = new CategorieDto("lieu", "Lieu", List.of());
            when(categorieRepository.findAll(BY_POSITION)).thenReturn(List.of(musique, lieu));
            when(categorieMapper.toCategorieDto(musique)).thenReturn(musiqueDto);
            when(categorieMapper.toCategorieDto(lieu)).thenReturn(lieuDto);

            assertThat(categorieService.getCategories()).containsExactly(musiqueDto, lieuDto);
        }
    }

    // -------------------------------------------------------------------------
    // createSousCategorie
    // -------------------------------------------------------------------------

    @Nested
    class CreateSousCategorie {

        @Test
        void givenNewKey_whenCreateSousCategorie_thenSavesItLastInItsCategoryAndReturnsIt() {
            SousCategorieAdminDto harpeDto = new SousCategorieAdminDto("harpe", "Harpe", "musique", 0L);
            when(sousCategorieRepository.existsById("harpe")).thenReturn(false);
            when(categorieRepository.existsById("musique")).thenReturn(true);
            when(sousCategorieRepository.findByCategoryKeyOrderByPositionAsc("musique"))
                    .thenReturn(List.of(sousCategorie("dj", "musique", 1), sousCategorie("jazz", "musique", 2)));
            ArgumentCaptor<SousCategorie> captor = ArgumentCaptor.forClass(SousCategorie.class);
            when(sousCategorieRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
            when(categorieMapper.toSousCategorieAdminDto(any(SousCategorie.class), eq(0L)))
                    .thenReturn(harpeDto);

            SousCategorieAdminDto result =
                    categorieService.createSousCategorie(new SousCategorieCreateRequest("harpe", "Harpe", "musique"));

            assertThat(result).isEqualTo(harpeDto);
            assertThat(captor.getValue().getKey()).isEqualTo("harpe");
            assertThat(captor.getValue().getName()).isEqualTo("Harpe");
            assertThat(captor.getValue().getPosition()).isEqualTo(3);
        }

        @Test
        void givenExistingKey_whenCreateSousCategorie_thenRefuses() {
            when(sousCategorieRepository.existsById("dj")).thenReturn(true);

            Throwable thrown = catchThrowable(() ->
                    categorieService.createSousCategorie(new SousCategorieCreateRequest("dj", "DJ", "musique")));

            assertReason(thrown, SousCategorieError.KEY_ALREADY_EXISTS);
            verify(sousCategorieRepository, never()).save(any());
        }

        @Test
        void givenUnknownCategorie_whenCreateSousCategorie_thenRefuses() {
            when(sousCategorieRepository.existsById("harpe")).thenReturn(false);
            when(categorieRepository.existsById("inconnue")).thenReturn(false);

            Throwable thrown = catchThrowable(() ->
                    categorieService.createSousCategorie(new SousCategorieCreateRequest("harpe", "Harpe", "inconnue")));

            assertReason(thrown, SousCategorieError.UNKNOWN_CATEGORIE);
        }
    }

    // -------------------------------------------------------------------------
    // updateSousCategorie
    // -------------------------------------------------------------------------

    @Nested
    class UpdateSousCategorie {

        @Test
        void givenSameCategorie_whenUpdateSousCategorie_thenOnlyRenamesAndReturnsIt() {
            SousCategorie dj = sousCategorie("dj", "musique", 1);
            SousCategorieAdminDto djDto = new SousCategorieAdminDto("dj", "DJ & mix", "musique", 3L);
            when(sousCategorieRepository.findById("dj")).thenReturn(Optional.of(dj));
            when(sousCategorieRepository.save(dj)).thenReturn(dj);
            when(prestataireService.countUsingSubcategory("dj")).thenReturn(3L);
            when(categorieMapper.toSousCategorieAdminDto(dj, 3L)).thenReturn(djDto);

            SousCategorieAdminDto result =
                    categorieService.updateSousCategorie("dj", new SousCategorieUpdateRequest("DJ & mix", "musique"));

            assertThat(result).isEqualTo(djDto);
            assertThat(dj.getName()).isEqualTo("DJ & mix");
            assertThat(dj.getPosition()).isEqualTo(1);
        }

        @Test
        void givenOtherCategorie_whenUpdateSousCategorie_thenMovesItLastInTheNewCategorie() {
            SousCategorie animation = sousCategorie("animation", "services", 2);
            when(sousCategorieRepository.findById("animation")).thenReturn(Optional.of(animation));
            when(categorieRepository.existsById("musique")).thenReturn(true);
            when(sousCategorieRepository.findByCategoryKeyOrderByPositionAsc("musique"))
                    .thenReturn(List.of(sousCategorie("dj", "musique", 1)));
            when(sousCategorieRepository.save(animation)).thenReturn(animation);

            categorieService.updateSousCategorie("animation", new SousCategorieUpdateRequest("Animation", "musique"));

            assertThat(animation.getCategoryKey()).isEqualTo("musique");
            assertThat(animation.getPosition()).isEqualTo(2);
        }

        @Test
        void givenUnknownSousCategorie_whenUpdateSousCategorie_thenThrowsNotFound() {
            when(sousCategorieRepository.findById("inconnue")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categorieService.updateSousCategorie(
                    "inconnue", new SousCategorieUpdateRequest("X", "musique")))
                    .isInstanceOf(SousCategorieNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // moveSousCategorie
    // -------------------------------------------------------------------------

    @Nested
    class MoveSousCategorie {

        @Test
        void givenMiddleSubcategory_whenMoveUp_thenSwapsPositionWithPrevious() {
            SousCategorie dj = sousCategorie("dj", "musique", 1);
            SousCategorie jazz = sousCategorie("jazz", "musique", 2);
            when(sousCategorieRepository.findById("jazz")).thenReturn(Optional.of(jazz));
            when(sousCategorieRepository.findByCategoryKeyOrderByPositionAsc("musique")).thenReturn(List.of(dj, jazz));

            SousCategorieAdminDto jazzDto = new SousCategorieAdminDto("jazz", "jazz", "musique", 0L);
            when(categorieMapper.toSousCategorieAdminDto(jazz, 0L)).thenReturn(jazzDto);

            assertThat(categorieService.moveSousCategorie("jazz", MoveDirection.UP)).isEqualTo(jazzDto);
            assertThat(jazz.getPosition()).isEqualTo(1);
            assertThat(dj.getPosition()).isEqualTo(2);
            verify(sousCategorieRepository).saveAll(List.of(jazz, dj));
        }

        @Test
        void givenLastSubcategory_whenMoveDown_thenNothingChanges() {
            SousCategorie dj = sousCategorie("dj", "musique", 1);
            SousCategorie jazz = sousCategorie("jazz", "musique", 2);
            when(sousCategorieRepository.findById("jazz")).thenReturn(Optional.of(jazz));
            when(sousCategorieRepository.findByCategoryKeyOrderByPositionAsc("musique")).thenReturn(List.of(dj, jazz));

            categorieService.moveSousCategorie("jazz", MoveDirection.DOWN);

            assertThat(jazz.getPosition()).isEqualTo(2);
            verify(sousCategorieRepository, never()).saveAll(any());
        }
    }

    // -------------------------------------------------------------------------
    // deleteSousCategorie
    // -------------------------------------------------------------------------

    @Nested
    class DeleteSousCategorie {

        @Test
        void givenUnusedSubcategory_whenDeleteSousCategorie_thenDeletesIt() {
            SousCategorie harpe = sousCategorie("harpe", "musique", 4);
            when(sousCategorieRepository.findById("harpe")).thenReturn(Optional.of(harpe));
            when(prestataireService.countUsingSubcategory("harpe")).thenReturn(0L);

            categorieService.deleteSousCategorie("harpe");

            verify(sousCategorieRepository).delete(harpe);
        }

        @Test
        void givenUsedSubcategory_whenDeleteSousCategorie_thenRefuses() {
            when(sousCategorieRepository.findById("dj")).thenReturn(Optional.of(sousCategorie("dj", "musique", 1)));
            when(prestataireService.countUsingSubcategory("dj")).thenReturn(3L);

            Throwable thrown = catchThrowable(() ->
                    categorieService.deleteSousCategorie("dj"));

            assertReason(thrown, SousCategorieError.SOUS_CATEGORIE_IN_USE);
            verify(sousCategorieRepository, never()).delete(any());
        }
    }

    // -------------------------------------------------------------------------
    // getCategoriesAdmin
    // -------------------------------------------------------------------------

    @Nested
    class GetCategoriesAdmin {

        @Test
        void givenCategories_whenGetCategoriesAdmin_thenCountsPrestatairesPerSubcategory() {
            Categorie musique = categorie("musique", sousCategorie("dj", "musique", 1));
            CategorieAdminDto musiqueDto = new CategorieAdminDto("musique", "Musique", List.of());
            when(categorieRepository.findAll(BY_POSITION)).thenReturn(List.of(musique));
            when(prestataireService.countUsingSubcategory("dj")).thenReturn(4L);
            when(categorieMapper.toCategorieAdminDto(musique, Map.of("dj", 4L))).thenReturn(musiqueDto);

            assertThat(categorieService.getCategoriesAdmin()).containsExactly(musiqueDto);
        }
    }

    // -------------------------------------------------------------------------
    // getCategoryKeyOf
    // -------------------------------------------------------------------------

    @Nested
    class GetCategoryKeyOf {

        @Test
        void givenSubcategory_whenGetCategoryKeyOf_thenReturnsItsCategory() {
            when(sousCategorieRepository.findById("dj")).thenReturn(Optional.of(sousCategorie("dj", "musique", 1)));

            assertThat(categorieService.getCategoryKeyOf("dj")).isEqualTo("musique");
        }

        @Test
        void givenUnknownSubcategory_whenGetCategoryKeyOf_thenThrowsNotFound() {
            when(sousCategorieRepository.findById("inconnue")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categorieService.getCategoryKeyOf("inconnue"))
                    .isInstanceOf(SousCategorieNotFoundException.class);
        }
    }
}
