package net.franzka.sgilt.core.prestataire.mapper;

import net.franzka.sgilt.core.prestataire.domain.Categorie;
import net.franzka.sgilt.core.prestataire.domain.SousCategorie;
import net.franzka.sgilt.core.prestataire.dto.CategorieDto;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper MapStruct pour le référentiel catégories / sous-catégories.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategorieMapper {

    /**
     * Mappe une sous-catégorie vers son DTO.
     *
     * @param sousCategorie l'entité source
     * @return le DTO sous-catégorie
     */
    SousCategorieDto toSousCategorieDto(SousCategorie sousCategorie);

    /**
     * Mappe une catégorie et ses sous-catégories (déjà triées par l'entité) vers son DTO.
     *
     * @param categorie l'entité source
     * @return le DTO catégorie
     */
    CategorieDto toCategorieDto(Categorie categorie);
}
