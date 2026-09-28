package net.franzka.sgilt.core.prestataire.service;

import net.franzka.sgilt.core.prestataire.domain.Categorie;
import net.franzka.sgilt.core.prestataire.dto.CategorieDto;
import net.franzka.sgilt.core.prestataire.mapper.CategorieMapper;
import net.franzka.sgilt.core.prestataire.repository.CategorieRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategorieServiceTest {

    @Mock
    private CategorieRepository categorieRepository;

    @Mock
    private CategorieMapper categorieMapper;

    @InjectMocks
    private CategorieService categorieService;

    // -------------------------------------------------------------------------
    // getCategories
    // -------------------------------------------------------------------------

    @Nested
    class GetCategories {

        @Test
        void givenReferential_whenGetCategories_thenMapsCategoriesSortedByPosition() {
            Categorie musique = Categorie.builder().key("musique").position(1).build();
            Categorie lieu = Categorie.builder().key("lieu").position(2).build();
            CategorieDto musiqueDto = new CategorieDto("musique", "Musique", List.of());
            CategorieDto lieuDto = new CategorieDto("lieu", "Lieu", List.of());
            when(categorieRepository.findAll(Sort.by("position"))).thenReturn(List.of(musique, lieu));
            when(categorieMapper.toCategorieDto(musique)).thenReturn(musiqueDto);
            when(categorieMapper.toCategorieDto(lieu)).thenReturn(lieuDto);

            assertThat(categorieService.getCategories()).containsExactly(musiqueDto, lieuDto);
        }
    }
}
