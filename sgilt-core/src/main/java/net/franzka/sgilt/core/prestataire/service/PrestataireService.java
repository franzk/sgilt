package net.franzka.sgilt.core.prestataire.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.jwt.domain.ActionType;
import net.franzka.sgilt.core.jwt.service.ActionLinkService;
import net.franzka.sgilt.core.prestataire.domain.MediaType;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.domain.PrestataireFlow;
import net.franzka.sgilt.core.prestataire.domain.PrestataireStatus;
import net.franzka.sgilt.core.prestataire.dto.*;
import net.franzka.sgilt.core.prestataire.exception.MediasInvalidException;
import net.franzka.sgilt.core.prestataire.exception.PrestataireForbiddenException;
import net.franzka.sgilt.core.prestataire.exception.PrestataireInvalidStateException;
import net.franzka.sgilt.core.prestataire.exception.PrestataireNotFoundException;
import net.franzka.sgilt.core.prestataire.mailer.PrestataireMailerService;
import net.franzka.sgilt.core.prestataire.mapper.PrestataireMapper;
import net.franzka.sgilt.core.prestataire.repository.PrestataireRepository;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.service.ReservationService;
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
    private final ReservationService reservationService;
    private final ActionLinkService actionLinkService;
    private final PrestataireMailerService prestataireMailerService;
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
     * Charge un prestataire publié par son identifiant.
     * Utilisé pour valider une cible avant création d'une ressource publique (ex. onboarding) —
     * lève la même exception qu'un identifiant inexistant, qu'il soit inexistant, non publié ou
     * supprimé, pour ne pas exposer cette distinction à un appelant non authentifié.
     *
     * @param id l'identifiant du prestataire
     * @return le prestataire correspondant
     * @throws PrestataireNotFoundException si aucun prestataire publié ne correspond à cet identifiant
     */
    public Prestataire getPublishedById(UUID id) {
        return prestataireRepository.findByIdAndStatusAndDeletedAtIsNull(id, PrestataireStatus.PUBLISHED)
                .orElseThrow(() -> new PrestataireNotFoundException(id.toString()));
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
     * Charge l'entité prestataire active liée à un utilisateur PRO.
     *
     * @param utilisateur l'utilisateur PRO
     * @return l'entité prestataire
     * @throws PrestataireNotFoundException si aucun prestataire n'est lié à cet utilisateur
     */
    public Prestataire getEntityByUtilisateurOwner(Utilisateur utilisateur) {
        return findPrestataire(utilisateur);
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
     * Retourne tous les prestataires confirmés (onboarding terminé) avec leur statut de fiche.
     * Exclut les prestataires dont l'onboarding est encore en attente (lien non cliqué, compte pas
     * encore activé) — filtré au niveau de la requête, voir {@link PrestataireRepository#findConfirmedByDeletedAtIsNull()}.
     *
     * @return la liste des fiches confirmées (id, name, slug, status)
     */
    public List<PrestataireAdminListItemDto> getConfirmedPrestataires() {
        return prestataireRepository.findConfirmedByDeletedAtIsNull().stream()
                .map(p -> prestataireMapper.toAdminListItemDto(p, buildReservationCounts(p.getId())))
                .toList();
    }

    /**
     * Publie une fiche prestataire — passe de IN_REVIEW ou WAITING_FOR_CREATION_SERVICE à
     * PUBLISHED — et notifie le prestataire par mail :
     * - lien d'activation si la fiche vient de
     * {@link PrestataireStatus#WAITING_FOR_CREATION_SERVICE} (flow clé-en-main, jamais reçu de
     * mail auparavant),
     * - simple notification si elle vient de IN_REVIEW (flow autonome, mot de
     * passe déjà existant).
     * Le flow clé-en-main saute IN_REVIEW : l'admin y construit et publie
     * lui-même la fiche, sans qu'un prestataire distinct n'ait besoin de la soumettre.
     *
     * @param id identifiant du prestataire à publier
     * @return {@code true} si la notification a bien été délivrée
     * @throws EntityNotFoundException si aucun prestataire ne correspond
     * @throws PrestataireInvalidStateException si le statut courant n'est ni IN_REVIEW ni WAITING_FOR_CREATION_SERVICE
     */
    public boolean publish(UUID id) {
        Prestataire prestataire = getById(id);
        PrestataireStatus previousStatus = prestataire.getStatus();
        if (previousStatus != PrestataireStatus.IN_REVIEW
                && previousStatus != PrestataireStatus.WAITING_FOR_CREATION_SERVICE) {
            throw new PrestataireInvalidStateException(
                    "La fiche ne peut pas être publiée depuis le statut " + previousStatus);
        }
        prestataire.setStatus(PrestataireStatus.PUBLISHED);
        prestataireRepository.save(prestataire);

        Utilisateur utilisateur = prestataire.getUtilisateur();
        boolean delivered;
        if (prestataire.getFlow() == PrestataireFlow.CREATION_CLE_EN_MAIN) {
            String actionUrl = actionLinkService.createLink(
                    ActionType.PRESTATAIRE_ONBOARDING, Map.of("email", utilisateur.getEmail()));
            delivered = prestataireMailerService.sendPrestatairePageReadyEmail(
                    utilisateur.getEmail(), utilisateur.getFirstName(), actionUrl, prestataire.getSlug());
        } else {
            delivered = prestataireMailerService.sendPrestatairePublishedEmail(
                    utilisateur.getEmail(), utilisateur.getFirstName(), prestataire.getSlug());
        }
        return delivered;
    }

    /**
     * Relance la notification d'onboarding d'une fiche dont le lien d'activation est en attente
     * (déjà reconstruit par l'appelant, période de validité déjà réinitialisée). Le mail envoyé
     * dépend du {@link PrestataireFlow flow} d'origine de la fiche : clé-en-main (voir
     * {@link #publish}) inclut le lien vers la page déjà en ligne ; sinon c'est le simple mail
     * d'activation.
     *
     * @param prestataire la fiche dont l'onboarding doit être relancé
     * @param actionUrl   le lien d'action reconstruit à inclure dans le mail
     * @return {@code true} si le mail a bien été délivré
     */
    public boolean resendOnboardingEmail(Prestataire prestataire, String actionUrl) {
        Utilisateur utilisateur = prestataire.getUtilisateur();
        if (PrestataireFlow.CREATION_CLE_EN_MAIN.equals(prestataire.getFlow())) {
            return prestataireMailerService.sendPrestatairePageReadyEmail(
                    utilisateur.getEmail(), utilisateur.getFirstName(), actionUrl, prestataire.getSlug());
        }
        return prestataireMailerService.sendPrestataireOnboardingEmail(
                utilisateur.getEmail(), utilisateur.getFirstName(), actionUrl);
    }

    /**
     * Renvoie une fiche publiée en revue (modération admin) — passe de PUBLISHED à IN_REVIEW.
     *
     * @param id identifiant du prestataire à renvoyer en revue
     * @throws EntityNotFoundException si aucun prestataire ne correspond
     * @throws PrestataireInvalidStateException si le statut courant n'est pas PUBLISHED
     */
    public void sendBackToReview(UUID id) {
        Prestataire prestataire = getById(id);
        if (prestataire.getStatus() != PrestataireStatus.PUBLISHED) {
            throw new PrestataireInvalidStateException(
                    "La fiche ne peut pas être renvoyée en revue depuis le statut " + prestataire.getStatus());
        }
        prestataire.setStatus(PrestataireStatus.IN_REVIEW);
        prestataireRepository.save(prestataire);
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
     * Charge le prestataire actif dont l'utilisateur lié a l'email donné.
     * Utilisé par le back-office pour retrouver la fiche associée à un token d'action en attente,
     * dont le payload ne porte que l'email du destinataire.
     *
     * @param email l'email de l'utilisateur propriétaire de la fiche
     * @return le prestataire correspondant
     * @throws PrestataireNotFoundException si aucune fiche active ne correspond à cet email
     */
    public Prestataire getByUtilisateurEmail(String email) {
        return prestataireRepository.findByUtilisateur_EmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new PrestataireNotFoundException(email));
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
     * Résultat d'une création de fiche : la fiche créée et si la notification associée a bien été
     * délivrée — {@code true} aussi si aucune notification n'était due à ce stade (flow
     * clé-en-main, voir {@link #createPrestataireCleEnMain}).
     *
     * @param prestataire            la fiche créée et persistée
     * @param notificationDelivered {@code true} si le mail a bien été envoyé, ou si aucun n'était dû
     */
    public record CreationResult(Prestataire prestataire, boolean notificationDelivered) {}

    /**
     * Crée une fiche prestataire vierge en flow clé-en-main — statut
     * {@link PrestataireStatus#WAITING_FOR_CREATION_SERVICE}. Le prestataire n'a jamais interagi
     * avec Sgilt à ce stade : aucune notification n'est envoyée, le mail (avec lien d'activation)
     * part à la publication (voir {@link #publish}).
     *
     * @param utilisateur l'utilisateur déjà créé et lié à ce prestataire
     * @param slug        le slug public de la fiche
     * @param name        le nom du prestataire
     * @param categoryKey la clé de catégorie
     * @param subcatKeys  les clés de sous-catégories (peut être vide)
     * @return la fiche créée ; la notification est toujours considérée comme délivrée (aucune n'est due)
     */
    public CreationResult createPrestataireCleEnMain(
            Utilisateur utilisateur, String slug, String name, String categoryKey, List<String> subcatKeys) {
        Prestataire prestataire = buildAndSave(
                utilisateur, slug, name, categoryKey, subcatKeys,
                PrestataireStatus.WAITING_FOR_CREATION_SERVICE, PrestataireFlow.CREATION_CLE_EN_MAIN);
        return new CreationResult(prestataire, true);
    }

    /**
     * Crée une fiche prestataire vierge en flow autonome — statut {@link PrestataireStatus#DRAFT}
     * — et envoie immédiatement le mail d'activation (lien pour définir le mot de passe).
     *
     * @param utilisateur l'utilisateur déjà créé et lié à ce prestataire
     * @param slug        le slug public de la fiche
     * @param name        le nom du prestataire
     * @param categoryKey la clé de catégorie
     * @param subcatKeys  les clés de sous-catégories (peut être vide)
     * @return la fiche créée et si le mail d'activation a bien été délivré
     */
    public CreationResult createPrestataireAutonome(
            Utilisateur utilisateur, String slug, String name, String categoryKey, List<String> subcatKeys) {
        Prestataire prestataire = buildAndSave(
                utilisateur, slug, name, categoryKey, subcatKeys,
                PrestataireStatus.DRAFT, PrestataireFlow.CREATION_AUTONOME);
        String actionUrl = actionLinkService.createLink(
                ActionType.PRESTATAIRE_ONBOARDING, Map.of("email", utilisateur.getEmail()));
        boolean delivered = prestataireMailerService.sendPrestataireOnboardingEmail(
                utilisateur.getEmail(), utilisateur.getFirstName(), actionUrl);
        return new CreationResult(prestataire, delivered);
    }

    private Prestataire buildAndSave(
            Utilisateur utilisateur, String slug, String name, String categoryKey, List<String> subcatKeys,
            PrestataireStatus initialStatus, PrestataireFlow flow) {
        Prestataire prestataire = Prestataire.builder()
                .utilisateur(utilisateur)
                .slug(slug)
                .name(name)
                .categoryKey(categoryKey)
                .subcatKeys(subcatKeys)
                .status(initialStatus)
                .flow(flow)
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

    // ── Application de contenu généré (ex. génération IA) ─────────────────────
    // Chaque section expose un remplacement direct ; les sections liste exposent en plus un ajout
    // (concaténation à la liste existante). Les valeurs viennent de l'appelant — ce service ignore
    // leur origine (génération IA ou autre).

    /**
     * Remplace la baseline du prestataire.
     *
     * @param prestataire l'entité à mettre à jour
     * @param baseline    la nouvelle valeur
     * @return la fiche complète après sauvegarde
     */
    public PrestataireDetailDto replaceBaseline(Prestataire prestataire, String baseline) {
        prestataire.setBaseline(baseline);
        return saveAndMap(prestataire);
    }

    /**
     * Remplace le résumé court du prestataire.
     *
     * @param prestataire      l'entité à mettre à jour
     * @param shortDescription la nouvelle valeur
     * @return la fiche complète après sauvegarde
     */
    public PrestataireDetailDto replaceShortDescription(Prestataire prestataire, String shortDescription) {
        prestataire.setShortDescription(shortDescription);
        return saveAndMap(prestataire);
    }

    /**
     * Remplace l'identité (citation + bio) du prestataire.
     *
     * @param prestataire l'entité à mettre à jour
     * @param identity    la nouvelle identité
     * @return la fiche complète après sauvegarde
     */
    public PrestataireDetailDto replaceIdentity(Prestataire prestataire, IdentityDto identity) {
        prestataire.setIdentity(serializeJson(identity));
        return saveAndMap(prestataire);
    }

    /**
     * Remplace le texte de budget du prestataire.
     *
     * @param prestataire l'entité à mettre à jour
     * @param budget      la nouvelle valeur
     * @return la fiche complète après sauvegarde
     */
    public PrestataireDetailDto replaceBudget(Prestataire prestataire, String budget) {
        prestataire.setBudget(serializeJson(budget));
        return saveAndMap(prestataire);
    }

    /**
     * Remplace intégralement la liste des offres du prestataire.
     *
     * @param prestataire l'entité à mettre à jour
     * @param offerings   la nouvelle liste
     * @return la fiche complète après sauvegarde
     */
    public PrestataireDetailDto replaceOfferings(Prestataire prestataire, List<String> offerings) {
        prestataire.setOfferings(serializeJson(offerings));
        return saveAndMap(prestataire);
    }

    /**
     * Concatène des offres à la fin de la liste existante du prestataire.
     *
     * @param prestataire l'entité à mettre à jour
     * @param offerings   les offres à ajouter
     * @return la fiche complète après sauvegarde
     */
    public PrestataireDetailDto appendOfferings(Prestataire prestataire, List<String> offerings) {
        return replaceOfferings(prestataire, concat(parseJson(prestataire.getOfferings(), new TypeReference<>() {
        }), offerings));
    }

    /**
     * Remplace intégralement la liste des témoignages du prestataire.
     *
     * @param prestataire l'entité à mettre à jour
     * @param testimonials la nouvelle liste
     * @return la fiche complète après sauvegarde
     */
    public PrestataireDetailDto replaceTestimonials(Prestataire prestataire, List<TestimonialDto> testimonials) {
        prestataire.setTestimonials(serializeJson(testimonials));
        return saveAndMap(prestataire);
    }

    /**
     * Concatène des témoignages à la fin de la liste existante du prestataire.
     *
     * @param prestataire  l'entité à mettre à jour
     * @param testimonials les témoignages à ajouter
     * @return la fiche complète après sauvegarde
     */
    public PrestataireDetailDto appendTestimonials(Prestataire prestataire, List<TestimonialDto> testimonials) {
        return replaceTestimonials(prestataire, concat(parseJson(prestataire.getTestimonials(), new TypeReference<>() {
        }), testimonials));
    }

    /**
     * Remplace intégralement la liste des informations pratiques du prestataire.
     *
     * @param prestataire l'entité à mettre à jour
     * @param details     la nouvelle liste
     * @return la fiche complète après sauvegarde
     */
    public PrestataireDetailDto replaceDetails(Prestataire prestataire, List<DetailDto> details) {
        prestataire.setDetails(serializeJson(details));
        return saveAndMap(prestataire);
    }

    /**
     * Concatène des informations pratiques à la fin de la liste existante du prestataire.
     *
     * @param prestataire l'entité à mettre à jour
     * @param details     les informations à ajouter
     * @return la fiche complète après sauvegarde
     */
    public PrestataireDetailDto appendDetails(Prestataire prestataire, List<DetailDto> details) {
        return replaceDetails(prestataire, concat(parseJson(prestataire.getDetails(), new TypeReference<>() {
        }), details));
    }

    /**
     * Remplace intégralement la FAQ du prestataire.
     *
     * @param prestataire l'entité à mettre à jour
     * @param faq         la nouvelle liste
     * @return la fiche complète après sauvegarde
     */
    public PrestataireDetailDto replaceFaq(Prestataire prestataire, List<FaqItemDto> faq) {
        prestataire.setFaq(serializeJson(faq));
        return saveAndMap(prestataire);
    }

    /**
     * Concatène des entrées de FAQ à la fin de la liste existante du prestataire.
     *
     * @param prestataire l'entité à mettre à jour
     * @param faq         les entrées à ajouter
     * @return la fiche complète après sauvegarde
     */
    public PrestataireDetailDto appendFaq(Prestataire prestataire, List<FaqItemDto> faq) {
        return replaceFaq(prestataire, concat(parseJson(prestataire.getFaq(), new TypeReference<>() {
        }), faq));
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

    // ── JSONB pour l'application de contenu généré ─────────────────────────────

    private PrestataireDetailDto saveAndMap(Prestataire prestataire) {
        return prestataireMapper.toDetailDto(prestataireRepository.save(prestataire));
    }

    private <T> List<T> concat(List<T> existing, List<T> toAppend) {
        List<T> merged = new ArrayList<>(existing != null ? existing : List.of());
        merged.addAll(toAppend);
        return merged;
    }

    private <T> List<T> parseJson(String json, TypeReference<List<T>> typeRef) {
        if (json == null) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Échec de désérialisation JSONB", e);
        }
    }

    private String serializeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Échec de sérialisation JSONB", e);
        }
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

    /**
     * Mappe les comptes par statut de réservation d'un prestataire en DTO de compteurs pour la
     * liste admin.
     *
     * @param prestataireId l'identifiant du prestataire
     * @return un DTO contenant les compteurs par statut
     */
    private PrestataireReservationCountsDto buildReservationCounts(UUID prestataireId) {
        Map<ReservationStatus, Integer> counts = reservationService.getStatusCountsByPrestataire(prestataireId);
        return new PrestataireReservationCountsDto(
                counts.getOrDefault(ReservationStatus.CONFIRMED, 0),
                counts.getOrDefault(ReservationStatus.IN_DISCUSSION, 0),
                counts.getOrDefault(ReservationStatus.NEW, 0),
                counts.getOrDefault(ReservationStatus.REFUSED_PRE_CONTACT, 0)
                        + counts.getOrDefault(ReservationStatus.REFUSED_POST_CONTACT, 0),
                counts.getOrDefault(ReservationStatus.CANCELED_BY_CLIENT_PRE_CONTACT, 0)
                        + counts.getOrDefault(ReservationStatus.CANCELED_BY_CLIENT_POST_CONTACT, 0)
                        + counts.getOrDefault(ReservationStatus.CANCELED_BY_CLIENT_POST_CONFIRMATION, 0)
                        + counts.getOrDefault(ReservationStatus.CANCELED_BY_PRO_POST_CONFIRMATION, 0),
                counts.getOrDefault(ReservationStatus.DONE, 0)
        );
    }
}
