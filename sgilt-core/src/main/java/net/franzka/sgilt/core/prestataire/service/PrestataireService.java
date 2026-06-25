package net.franzka.sgilt.core.prestataire.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.dto.*;
import net.franzka.sgilt.core.prestataire.exception.PrestataireForbiddenException;
import net.franzka.sgilt.core.prestataire.exception.PrestataireNotFoundException;
import net.franzka.sgilt.core.prestataire.mapper.PrestataireMapper;
import net.franzka.sgilt.core.prestataire.repository.PrestataireRepository;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service métier pour l'entité {@link Prestataire}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrestataireService {

    private final PrestataireRepository prestataireRepository;
    private final PrestataireMapper prestataireMapper;

    /**
     * Charge un prestataire par son identifiant.
     *
     * @param id l'identifiant du prestataire
     * @return le prestataire correspondant
     * @throws EntityNotFoundException si aucun prestataire ne correspond
     */
    public Prestataire getById(UUID id) {
        return prestataireRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Prestataire with id {} not found", id);
                    return new EntityNotFoundException();
                });
    }

    /**
     * Charge la fiche complète d'un prestataire par son slug.
     *
     * @param slug le slug du prestataire
     * @return le DTO complet
     * @throws PrestataireNotFoundException si aucun prestataire ne correspond
     */
    public PrestataireDetailDto getBySlug(String slug) {
        Prestataire p = prestataireRepository.findBySlugAndDeletedAtIsNull(slug)
                .orElseThrow(() -> new PrestataireNotFoundException(slug));
        return prestataireMapper.toDetailDto(p);
    }

    /**
     * Recherche des prestataires avec un filtre exclusif : soit par catégorie, soit par
     * sous-catégories, jamais les deux simultanément.
     * Calcule toujours les compteurs globaux par catégorie (pour les tabs de navigation),
     * et les compteurs par sous-catégorie pour la catégorie active.
     *
     * @param categoryKey clé de catégorie (null si filtre par subcatKeys)
     * @param subcatKeys  clés de sous-catégories (null/vide si filtre par categoryKey)
     * @return réponse avec résultats, compteurs catégorie et compteurs sous-catégorie
     */
    public PrestataireSearchResponseDto search(String categoryKey, List<String> subcatKeys) {
        List<Prestataire> all = prestataireRepository.findByDeletedAtIsNull();

        List<Prestataire> filtered = resolveFiltered(categoryKey, subcatKeys);
        String activeCategoryKey = resolveActiveCategoryKey(categoryKey, subcatKeys, filtered);

        return new PrestataireSearchResponseDto(
                filtered.stream().map(prestataireMapper::toCardDto).toList(),
                buildCategoryCounts(all),
                buildSubcatCounts(all, activeCategoryKey)
        );
    }

    /**
     * Applique les modifications sur la fiche d'un prestataire.
     * Seuls les champs non-null du DTO sont écrits (nullValuePropertyMappingStrategy = IGNORE).
     *
     * @param id          identifiant du prestataire à modifier
     * @param dto         les champs à mettre à jour
     * @param utilisateur l'utilisateur connecté — utilisé pour vérifier la propriété de la fiche
     * @throws PrestataireNotFoundException si aucune fiche active ne correspond à cet id
     * @throws PrestataireForbiddenException si la fiche n'appartient pas à l'utilisateur
     */
    public void update(UUID id, PrestataireUpdateDto dto, Utilisateur utilisateur) {
        Prestataire prestataire = getById(id);
        if (prestataire.getDeletedAt() != null) {
            throw new PrestataireNotFoundException(id.toString());
        }
        if (!prestataire.getUtilisateur().getId().equals(utilisateur.getId())) {
            throw new PrestataireForbiddenException(id.toString());
        }
        prestataireMapper.updatePrestataire(prestataire, dto);
        prestataireRepository.save(prestataire);
    }

    /**
     * Retourne le slug du prestataire lié à un utilisateur PRO.
     *
     * @param utilisateur l'utilisateur propriétaire du compte pro
     * @return le slug, ou {@code null} si aucun prestataire n'est encore lié
     */
    public String getSlugByUtilisateur(Utilisateur utilisateur) {
        return prestataireRepository.findByUtilisateurAndDeletedAtIsNull(utilisateur)
                .map(Prestataire::getSlug)
                .orElse(null);
    }

    /**
     * Lie un utilisateur à un prestataire lors du bootstrap du compte PRO.
     * Sans effet si le prestataire est déjà lié à cet utilisateur.
     *
     * @param slug        le slug du prestataire fourni via l'attribut KC {@code bootstrap_prestataire_slug}
     * @param utilisateur l'utilisateur authentifié à lier
     * @throws PrestataireNotFoundException si aucun prestataire actif ne correspond au slug
     */
    public void linkBootstrapUtilisateur(String slug, Utilisateur utilisateur) {
        Prestataire prestataire = prestataireRepository.findBySlugAndDeletedAtIsNull(slug)
                .orElseThrow(() -> new PrestataireNotFoundException(slug));
        if (utilisateur.getId().equals(prestataire.getUtilisateur().getId())) return;
        prestataire.setUtilisateur(utilisateur);
        prestataireRepository.save(prestataire);
    }

    // ── Résolution du filtre exclusif ─────────────────────────────────────────

    private List<Prestataire> resolveFiltered(String categoryKey, List<String> subcatKeys) {
        if (subcatKeys != null && !subcatKeys.isEmpty()) {
            return prestataireRepository.findBySubcatKeysInAndDeletedAtIsNull(subcatKeys);
        }
        if (categoryKey != null) {
            return prestataireRepository.findByCategoryKeyAndDeletedAtIsNull(categoryKey);
        }
        return prestataireRepository.findByDeletedAtIsNull();
    }

    private String resolveActiveCategoryKey(String categoryKey, List<String> subcatKeys, List<Prestataire> filtered) {
        if (categoryKey != null) return categoryKey;
        if (subcatKeys != null && !subcatKeys.isEmpty()) {
            // Dérivé depuis les résultats : tous les prestataires d'une même catégorie
            return filtered.stream()
                    .map(Prestataire::getCategoryKey)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    // ── Compteurs ─────────────────────────────────────────────────────────────

    private Map<String, Long> buildCategoryCounts(List<Prestataire> all) {
        Map<String, Long> counts = new HashMap<>();
        counts.put("all", (long) all.size());
        all.forEach(p -> counts.merge(p.getCategoryKey(), 1L, Long::sum));
        return counts;
    }

    private Map<String, Long> buildSubcatCounts(List<Prestataire> all, String activeCategoryKey) {
        if (activeCategoryKey == null) return new HashMap<>();
        Map<String, Long> counts = new HashMap<>();
        all.stream()
                .filter(p -> activeCategoryKey.equals(p.getCategoryKey()))
                .forEach(p -> p.getSubcatKeys().forEach(key -> counts.merge(key, 1L, Long::sum)));
        return counts;
    }
}
