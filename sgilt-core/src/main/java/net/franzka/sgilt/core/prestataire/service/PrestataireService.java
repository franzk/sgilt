package net.franzka.sgilt.core.prestataire.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.prestataire.domain.MediaType;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.domain.PrestataireStatus;
import net.franzka.sgilt.core.prestataire.dto.*;
import net.franzka.sgilt.core.prestataire.exception.MediasInvalidException;
import net.franzka.sgilt.core.prestataire.exception.PrestataireForbiddenException;
import net.franzka.sgilt.core.prestataire.exception.PrestataireInvalidStateException;
import net.franzka.sgilt.core.prestataire.exception.PrestataireNotFoundException;
import net.franzka.sgilt.core.prestataire.mapper.PrestataireMapper;
import net.franzka.sgilt.core.prestataire.repository.PrestataireRepository;
import net.franzka.sgilt.core.storage.FileStorageException;
import net.franzka.sgilt.core.storage.FileStorageService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
     * Charge la fiche complète d'un prestataire publié par son slug.
     *
     * @param slug le slug du prestataire
     * @return le DTO complet
     * @throws PrestataireNotFoundException si aucun prestataire publié ne correspond
     */
    public PrestataireDetailDto getBySlug(String slug) {
        Prestataire p = prestataireRepository.findBySlugAndStatusAndDeletedAtIsNull(slug, PrestataireStatus.PUBLISHED)
                .orElseThrow(() -> new PrestataireNotFoundException(slug));
        return prestataireMapper.toDetailDto(p);
    }

    /**
     * Charge la fiche complète de l'utilisateur PRO, quel que soit son statut.
     *
     * @param utilisateur l'utilisateur PRO
     * @return le DTO complet
     * @throws PrestataireNotFoundException si aucun prestataire n'est lié à cet utilisateur
     */
    public PrestataireDetailDto getByUtilisateurOwner(Utilisateur utilisateur) {
        return prestataireMapper.toDetailDto(findPrestataire(utilisateur));
    }

    /**
     * Soumet la fiche du prestataire connecté pour revue admin — passe de DRAFT à IN_REVIEW.
     * Aucune validation de complétude : la confiance est accordée au prestataire.
     *
     * @param utilisateur l'utilisateur PRO connecté
     * @throws PrestataireNotFoundException si aucun prestataire n'est lié à cet utilisateur
     * @throws PrestataireInvalidStateException si le statut courant n'est pas DRAFT
     */
    public void submitMaFiche(Utilisateur utilisateur) {
        Prestataire prestataire = findPrestataire(utilisateur);
        if (prestataire.getStatus() != PrestataireStatus.DRAFT) {
            throw new PrestataireInvalidStateException(
                    "La fiche ne peut pas être soumise depuis le statut " + prestataire.getStatus());
        }
        prestataire.setStatus(PrestataireStatus.IN_REVIEW);
        prestataireRepository.save(prestataire);
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
        List<Prestataire> all = prestataireRepository.findByStatusAndDeletedAtIsNull(PrestataireStatus.PUBLISHED);

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
     * Vérifie si un slug est déjà utilisé par un prestataire existant.
     *
     * @param slug le slug à vérifier
     * @return {@code true} si le slug est déjà pris
     */
    public boolean existsBySlug(String slug) {
        return prestataireRepository.existsBySlug(slug);
    }

    /**
     * Crée et persiste une fiche prestataire vierge.
     * Seuls {@code slug}, {@code name}, {@code categoryKey} et {@code subcatKeys} sont renseignés —
     * tous les autres champs restent vides.
     *
     * @param utilisateur l'utilisateur déjà créé et lié à ce prestataire
     * @param slug        le slug public de la fiche
     * @param name        le nom du prestataire
     * @param categoryKey la clé de catégorie
     * @param subcatKeys  les clés de sous-catégories (peut être vide).
     * @return le prestataire créé et persisté
     */
    public Prestataire createPrestataire(
            Utilisateur utilisateur, String slug, String name, String categoryKey, List<String> subcatKeys) {
        Prestataire prestataire = Prestataire.builder()
                .utilisateur(utilisateur)
                .slug(slug)
                .name(name)
                .categoryKey(categoryKey)
                .subcatKeys(subcatKeys)
                .status(PrestataireStatus.DRAFT)
                .build();

        return prestataireRepository.save(prestataire);
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

    /**
     * Upload une image vers R2 pour le compte du prestataire connecté.
     * Retourne la clé de stockage du fichier uploadé.
     *
     * @param utilisateur l'utilisateur PRO connecté
     * @param file        le fichier image à uploader
     * @return le DTO contenant la clé R2 du fichier
     * @throws FileStorageException en cas d'erreur de communication avec R2
     */
    public MediaUploadDto uploadMedia(Utilisateur utilisateur, MultipartFile file) {
        try {
            String key = fileStorageService.upload(file, "uploads");
            return new MediaUploadDto(key);
        } catch (IOException e) {
            throw new FileStorageException("Erreur de stockage du média pour " + utilisateur.getEmail(), e);
        }
    }

    /**
     * Remplace la collection complète de médias du prestataire lié à l'utilisateur connecté.
     * Valide que la position 0 est bien de type IMAGE avant toute persistance.
     * Retourne la fiche complète à jour pour permettre au front de resynchroniser son state.
     *
     * @param utilisateur l'utilisateur PRO connecté
     * @param medias      la liste complète des médias à persister (remplacement total)
     * @return la fiche prestataire complète après sauvegarde
     * @throws MediasInvalidException     si la position 0 est absente ou n'est pas de type IMAGE
     * @throws PrestataireNotFoundException si aucun prestataire actif n'est lié à cet utilisateur
     */
    public PrestataireDetailDto updateMedias(Utilisateur utilisateur, List<MediaDto> medias) {
        boolean heroPresent = medias.stream()
                .anyMatch(m -> m.position() == 0 && m.type() == MediaType.IMAGE);
        if (!heroPresent) {
            throw new MediasInvalidException("La position 0 doit être une image principale (IMAGE)");
        }
        Prestataire prestataire = findPrestataire(utilisateur);
        try {
            prestataire.setMedias(objectMapper.writeValueAsString(medias));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Échec de sérialisation des médias", e);
        }

        return prestataireMapper.toDetailDto(prestataireRepository.save(prestataire));
    }

    // ── Résolution du filtre exclusif ─────────────────────────────────────────

    private List<Prestataire> resolveFiltered(String categoryKey, List<String> subcatKeys) {
        if (subcatKeys != null && !subcatKeys.isEmpty()) {
            return prestataireRepository.findBySubcatKeysInAndStatusAndDeletedAtIsNull(subcatKeys, PrestataireStatus.PUBLISHED);
        }
        if (categoryKey != null) {
            return prestataireRepository.findByCategoryKeyAndStatusAndDeletedAtIsNull(categoryKey, PrestataireStatus.PUBLISHED);
        }
        return prestataireRepository.findByStatusAndDeletedAtIsNull(PrestataireStatus.PUBLISHED);
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

    // ── Lookup interne ────────────────────────────────────────────────────────

    /** Charge le prestataire actif lié à un utilisateur, ou lève une exception. */
    private Prestataire findPrestataire(Utilisateur utilisateur) {
        return prestataireRepository.findByUtilisateurAndDeletedAtIsNull(utilisateur)
                .orElseThrow(() -> new PrestataireNotFoundException(utilisateur.getEmail()));
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
