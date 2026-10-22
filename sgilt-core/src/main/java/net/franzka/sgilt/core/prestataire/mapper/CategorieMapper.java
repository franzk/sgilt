package net.franzka.sgilt.core.prestataire.mapper;

import net.franzka.sgilt.core.prestataire.domain.Categorie;
import net.franzka.sgilt.core.prestataire.domain.SousCategorie;
import net.franzka.sgilt.core.prestataire.dto.CategorieAdminDto;
import net.franzka.sgilt.core.prestataire.dto.CategorieDto;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieAdminDto;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.Map;

/**
 * Mapper MapStruct pour les catégories et sous-catégories prestataire.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategorieMapper {

    /**
     * Mappe une catégorie et ses sous-catégories (déjà triées par l'entité) vers son DTO public.
     *
     * @param categorie l'entité source
     * @return le DTO catégorie
     */
    CategorieDto toCategorieDto(Categorie categorie);

    /**
     * Mappe une sous-catégorie vers son DTO public.
     *
     * @param sousCategorie l'entité source
     * @return le DTO sous-catégorie
     */
    SousCategorieDto toSousCategorieDto(SousCategorie sousCategorie);

    /**
     * Mappe une sous-catégorie vers son DTO d'administration.
     *
     * @param sousCategorie    l'entité source
     * @param prestataireCount le nombre de prestataires qui l'utilisent
     * @return le DTO d'administration
     */
    SousCategorieAdminDto toSousCategorieAdminDto(SousCategorie sousCategorie, long prestataireCount);

    /**
     * Mappe une catégorie et ses sous-catégories vers son DTO d'administration.
     *
     * @param categorie         l'entité source
     * @param prestataireCounts le nombre de prestataires par clé de sous-catégorie
     * @return le DTO d'administration
     */
    default CategorieAdminDto toCategorieAdminDto(Categorie categorie, Map<String, Long> prestataireCounts) {
        return new CategorieAdminDto(
                categorie.getKey(),
                categorie.getName(),
                categorie.getSubcategories().stream()
                        .map(sousCategorie -> toSousCategorieAdminDto(
                                sousCategorie, prestataireCounts.getOrDefault(sousCategorie.getKey(), 0L)))
                        .toList());
    }
}
