package net.franzka.sgilt.core.prestataire.mapper;

import net.franzka.sgilt.core.prestataire.domain.Categorie;
import net.franzka.sgilt.core.prestataire.domain.SousCategorie;
import net.franzka.sgilt.core.prestataire.dto.CategorieAdminDto;
import net.franzka.sgilt.core.prestataire.dto.CategorieDto;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieAdminDto;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CategorieMapperTest {

    private final CategorieMapper mapper = new CategorieMapperImpl();

    // -------------------------------------------------------------------------
    // toCategorieDto
    // -------------------------------------------------------------------------

    @Nested
    class ToCategorieDto {

        @Test
        void givenCategorieWithSubcategories_whenToCategorieDto_thenMapsThemInOrder() {
            Categorie categorie = Categorie.builder().key("musique").name("Musique")
                    .subcategories(List.of(
                            SousCategorie.builder().key("dj").name("DJ").categoryKey("musique").build(),
                            SousCategorie.builder().key("jazz").name("Jazz").categoryKey("musique").build()))
                    .build();

            assertThat(mapper.toCategorieDto(categorie)).isEqualTo(new CategorieDto("musique", "Musique", List.of(
                    new SousCategorieDto("dj", "DJ", "musique"),
                    new SousCategorieDto("jazz", "Jazz", "musique"))));
        }
    }

    // -------------------------------------------------------------------------
    // toCategorieAdminDto
    // -------------------------------------------------------------------------

    @Nested
    class ToCategorieAdminDto {

        @Test
        void givenCategorieAndCounts_whenToCategorieAdminDto_thenMapsSubcategoriesWithCounts() {
            Categorie categorie = Categorie.builder().key("services").name("Services")
                    .subcategories(List.of(
                            SousCategorie.builder().key("decoration").name("Décoration").categoryKey("services").build(),
                            SousCategorie.builder().key("video").name("Vidéo").categoryKey("services").build()))
                    .build();

            CategorieAdminDto dto = mapper.toCategorieAdminDto(categorie, Map.of("decoration", 3L));

            assertThat(dto).isEqualTo(new CategorieAdminDto("services", "Services", List.of(
                    new SousCategorieAdminDto("decoration", "Décoration", "services", 3L),
                    new SousCategorieAdminDto("video", "Vidéo", "services", 0L))));
        }
    }
}
