package net.franzka.sgilt.core.prestataire.mapper;

import net.franzka.sgilt.core.prestataire.domain.Categorie;
import net.franzka.sgilt.core.prestataire.domain.SousCategorie;
import net.franzka.sgilt.core.prestataire.dto.CategorieDto;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CategorieMapperTest {

    private final CategorieMapper mapper = new CategorieMapperImpl();

    // -------------------------------------------------------------------------
    // toSousCategorieDto
    // -------------------------------------------------------------------------

    @Nested
    class ToSousCategorieDto {

        @Test
        void givenSousCategorie_whenToSousCategorieDto_thenMapsKeyNameAndCategory() {
            SousCategorie sousCategorie = SousCategorie.builder()
                    .key("dj").name("DJ").categoryKey("musique").position(1).build();

            assertThat(mapper.toSousCategorieDto(sousCategorie))
                    .isEqualTo(new SousCategorieDto("dj", "DJ", "musique"));
        }
    }

    // -------------------------------------------------------------------------
    // toCategorieDto
    // -------------------------------------------------------------------------

    @Nested
    class ToCategorieDto {

        @Test
        void givenCategorieWithSubcategories_whenToCategorieDto_thenMapsSubcategoriesInOrder() {
            Categorie categorie = Categorie.builder().key("musique").name("Musique").position(1)
                    .subcategories(List.of(
                            SousCategorie.builder().key("dj").name("DJ").categoryKey("musique").position(1).build(),
                            SousCategorie.builder().key("jazz").name("Jazz").categoryKey("musique").position(2).build()))
                    .build();

            assertThat(mapper.toCategorieDto(categorie)).isEqualTo(new CategorieDto("musique", "Musique", List.of(
                    new SousCategorieDto("dj", "DJ", "musique"),
                    new SousCategorieDto("jazz", "Jazz", "musique"))));
        }
    }
}
