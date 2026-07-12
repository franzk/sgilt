package net.franzka.sgilt.core.prestataire.repository;

import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.domain.PrestataireStatus;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
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
     * Retourne tous les prestataires actifs dans un statut donné.
     *
     * @param status le statut requis
     * @return prestataires correspondants
     */
    List<Prestataire> findByStatusAndDeletedAtIsNull(PrestataireStatus status);

    /**
     * Retourne les prestataires actifs d'une catégorie, restreints à un statut donné.
     *
     * @param categoryKey clé de catégorie ('musique', 'restauration'…)
     * @param status      le statut requis
     * @return prestataires correspondants
     */
    List<Prestataire> findByCategoryKeyAndStatusAndDeletedAtIsNull(String categoryKey, PrestataireStatus status);

    /**
     * Retourne les prestataires actifs ayant au moins une des sous-catégories données, restreints à
     * un statut donné.
     *
     * @param subcatKeys clés de sous-catégories ('dj', 'pop-rock'…)
     * @param status     le statut requis
     * @return prestataires correspondants
     */
    List<Prestataire> findBySubcatKeysInAndStatusAndDeletedAtIsNull(Collection<String> subcatKeys, PrestataireStatus status);

    /**
     * Recherche le prestataire actif dont l'utilisateur lié a l'email donné.
     *
     * @param email l'email de l'utilisateur propriétaire de la fiche
     * @return le prestataire correspondant, ou vide si aucun n'est lié
     */
    Optional<Prestataire> findByUtilisateur_EmailAndDeletedAtIsNull(String email);
}
