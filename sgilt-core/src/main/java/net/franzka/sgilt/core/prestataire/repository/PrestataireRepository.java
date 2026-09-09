package net.franzka.sgilt.core.prestataire.repository;

import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.domain.PrestataireStatus;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link Prestataire}.
 */
@Repository
public interface PrestataireRepository extends JpaRepository<Prestataire, UUID> {

    /**
     * Recherche un prestataire actif par son slug unique.
     *
     * @param slug le slug du prestataire
     * @return le prestataire correspondant, ou vide
     */
    Optional<Prestataire> findBySlugAndDeletedAtIsNull(String slug);

    /**
     * Vérifie si un slug est déjà utilisé, tous prestataires confondus (y compris supprimés,
     * puisque la contrainte d'unicité en base porte sur la colonne sans filtre).
     *
     * @param slug le slug à vérifier
     * @return {@code true} si le slug est déjà pris
     */
    boolean existsBySlug(String slug);

    /**
     * Retourne tous les prestataires actifs confirmés (onboarding terminé), tous statuts de fiche
     * confondus — exclut ceux ayant encore un {@code ActionToken} de type
     * {@code PRESTATAIRE_ONBOARDING} en attente.
     * Requête SQL native, exceptionnellement : le rapprochement se fait sur le contenu jsonb
     * {@code action_tokens.payload->>'email'}, une autre table/agrégat non lié par une relation
     * JPA, non exprimable par une derived query ni par du JPQL portable.
     *
     * @return liste des prestataires confirmés, non supprimés
     */
    @Query(value = """
            SELECT p.* FROM prestataires p
            JOIN utilisateurs u ON u.id = p.utilisateur_id
            WHERE p.deleted_at IS NULL
              AND NOT EXISTS (
                  SELECT 1 FROM action_tokens t
                  WHERE t.type = 'PRESTATAIRE_ONBOARDING'
                    AND t.payload ->> 'email' = u.email
              )
            """, nativeQuery = true)
    List<Prestataire> findConfirmedByDeletedAtIsNull();

    /**
     * Recherche le prestataire actif lié à un utilisateur donné.
     *
     * @param utilisateur l'utilisateur propriétaire du compte pro
     * @return le prestataire correspondant, ou vide si aucun n'est lié
     */
    Optional<Prestataire> findByUtilisateurAndDeletedAtIsNull(Utilisateur utilisateur);

    /**
     * Recherche un prestataire actif par son slug, restreint à un statut donné.
     *
     * @param slug   le slug du prestataire
     * @param status le statut requis
     * @return le prestataire correspondant, ou vide
     */
    Optional<Prestataire> findBySlugAndStatusAndDeletedAtIsNull(String slug, PrestataireStatus status);

    /**
     * Recherche un prestataire actif par son identifiant, restreint à un statut donné.
     *
     * @param id     l'identifiant du prestataire
     * @param status le statut requis
     * @return le prestataire correspondant, ou vide
     */
    Optional<Prestataire> findByIdAndStatusAndDeletedAtIsNull(UUID id, PrestataireStatus status);

    /**
     * Retourne tous les prestataires actifs dans un statut donné, triés par ordre d'affichage
     * (voir {@link Prestataire#getDisplayOrder()}).
     *
     * @param status le statut requis
     * @return prestataires correspondants
     */
    List<Prestataire> findByStatusAndDeletedAtIsNullOrderByDisplayOrderAsc(PrestataireStatus status);

    /**
     * Retourne les prestataires actifs d'une catégorie, restreints à un statut donné, triés par
     * ordre d'affichage (voir {@link Prestataire#getDisplayOrder()}).
     *
     * @param categoryKey clé de catégorie ('musique', 'restauration'…)
     * @param status      le statut requis
     * @return prestataires correspondants
     */
    List<Prestataire> findByCategoryKeyAndStatusAndDeletedAtIsNullOrderByDisplayOrderAsc(String categoryKey, PrestataireStatus status);

    /**
     * Retourne les prestataires actifs ayant au moins une des sous-catégories données, restreints à
     * un statut donné, triés par ordre d'affichage (voir {@link Prestataire#getDisplayOrder()}).
     *
     * @param subcatKeys clés de sous-catégories ('dj', 'pop-rock'…)
     * @param status     le statut requis
     * @return prestataires correspondants
     */
    List<Prestataire> findBySubcatKeysInAndStatusAndDeletedAtIsNullOrderByDisplayOrderAsc(Collection<String> subcatKeys, PrestataireStatus status);

    /**
     * Réattribue en une seule opération de masse un rang d'affichage aléatoire unique à tous les
     * prestataires actifs, et remet à {@code null} celui des prestataires supprimés (soft delete)
     * entre-temps — pour qu'une fiche supprimée ne garde jamais indéfiniment un rang obsolète.
     * Le partitionnement sur {@code deleted_at IS NULL} sépare les deux populations : la fenêtre
     * {@code ROW_NUMBER} ne numérote que parmi les actifs, et sa valeur est ignorée (mise à
     * {@code null}) pour les supprimés.
     * SQL natif, exceptionnellement : mise à jour de masse sur toute la table, non exprimable par
     * une opération sur entité individuelle ni par une derived query.
     */
    @Modifying
    @Query(value = """
            UPDATE prestataires p
            SET display_order = CASE WHEN sub.deleted_at IS NULL THEN sub.rn ELSE NULL END
            FROM (
                SELECT id, deleted_at,
                       ROW_NUMBER() OVER (PARTITION BY (deleted_at IS NULL) ORDER BY random()) AS rn
                FROM prestataires
            ) sub
            WHERE p.id = sub.id
            """, nativeQuery = true)
    void shuffleDisplayOrder();

    /**
     * Recherche le prestataire actif dont l'utilisateur lié a l'email donné.
     *
     * @param email l'email de l'utilisateur propriétaire de la fiche
     * @return le prestataire correspondant, ou vide si aucun n'est lié
     */
    Optional<Prestataire> findByUtilisateur_EmailAndDeletedAtIsNull(String email);
}
