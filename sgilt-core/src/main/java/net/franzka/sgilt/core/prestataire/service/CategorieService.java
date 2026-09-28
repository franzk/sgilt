package net.franzka.sgilt.core.prestataire.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.prestataire.dto.CategorieDto;
import net.franzka.sgilt.core.prestataire.mapper.CategorieMapper;
import net.franzka.sgilt.core.prestataire.repository.CategorieRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service de lecture du référentiel catégories / sous-catégories prestataire.
 */
@Service
@RequiredArgsConstructor
public class CategorieService {

    private final CategorieRepository categorieRepository;
    private final CategorieMapper categorieMapper;

    /**
     * Retourne le référentiel complet : les catégories triées, chacune avec ses sous-catégories
     * triées.
     *
     * @return les catégories du référentiel
     */
    public List<CategorieDto> getCategories() {
        return categorieRepository.findAll(Sort.by("position")).stream()
                .map(categorieMapper::toCategorieDto)
                .toList();
    }
}
