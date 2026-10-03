package net.franzka.sgilt.core.prestataire.controller;

import net.franzka.sgilt.core.prestataire.domain.MoveDirection;
import net.franzka.sgilt.core.prestataire.dto.CategorieAdminDto;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieAdminDto;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieCreateRequest;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieUpdateRequest;
import net.franzka.sgilt.core.prestataire.service.CategorieService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategorieControllerTest {

    @Mock
    private CategorieService categorieService;

    @InjectMocks
    private CategorieController controller;

    // -------------------------------------------------------------------------
    // administration
    // -------------------------------------------------------------------------

    @Nested
    class Administration {

        @Test
        void givenCategories_whenListCategoriesAdmin_thenReturnsThem() {
            List<CategorieAdminDto> categories = List.of(new CategorieAdminDto("lieu", "Lieu", List.of()));
            when(categorieService.getCategoriesAdmin()).thenReturn(categories);

            assertThat(controller.listCategoriesAdmin().getBody()).isEqualTo(categories);
        }

        @Test
        void givenCreateRequest_whenCreateSousCategorie_thenReturns201WithTheCreatedSubcategory() {
            SousCategorieCreateRequest request = new SousCategorieCreateRequest("harpe", "Harpe", "musique");
            SousCategorieAdminDto created = new SousCategorieAdminDto("harpe", "Harpe", "musique", 0L);
            when(categorieService.createSousCategorie(request)).thenReturn(created);

            ResponseEntity<SousCategorieAdminDto> response = controller.createSousCategorie(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isEqualTo(created);
        }

        @Test
        void givenUpdateRequest_whenUpdateSousCategorie_thenReturnsTheUpdatedSubcategory() {
            SousCategorieUpdateRequest request = new SousCategorieUpdateRequest("DJ", "musique");
            SousCategorieAdminDto updated = new SousCategorieAdminDto("dj", "DJ", "musique", 2L);
            when(categorieService.updateSousCategorie("dj", request)).thenReturn(updated);

            assertThat(controller.updateSousCategorie("dj", request).getBody()).isEqualTo(updated);
        }

        @Test
        void givenDirection_whenMoveSousCategorie_thenReturnsTheMovedSubcategory() {
            SousCategorieAdminDto moved = new SousCategorieAdminDto("jazz", "Jazz", "musique", 1L);
            when(categorieService.moveSousCategorie("jazz", MoveDirection.UP)).thenReturn(moved);

            assertThat(controller.moveSousCategorie("jazz", MoveDirection.UP).getBody()).isEqualTo(moved);
        }

        @Test
        void givenKey_whenDeleteSousCategorie_thenDelegatesAndReturns204() {
            ResponseEntity<Void> response = controller.deleteSousCategorie("harpe");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(categorieService).deleteSousCategorie("harpe");
        }
    }
}
