package net.franzka.sgilt.core.admin.controller;

import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.admin.api.AdminApi;
import net.franzka.sgilt.core.admin.dto.ProvisionPrestataireRequest;
import net.franzka.sgilt.core.admin.dto.ProvisionPrestataireResponse;
import net.franzka.sgilt.core.admin.exception.SlugAlreadyExistsException;
import net.franzka.sgilt.core.jwt.domain.ActionType;
import net.franzka.sgilt.core.jwt.service.ActionLinkService;
import net.franzka.sgilt.core.keycloak.KeycloakAdminService;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import net.franzka.sgilt.core.utilisateur.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller HTTP réservé à l'administration (rôle {@code ROLE_ADMIN}, distinct de PRO).
 * Etape 1/4 du flux de provisionnement prestataire : DB + Keycloak + token, sans mail ni front.
 * Orchestre lui-même Keycloak (hors transaction) puis la création DB (transaction courte via
 * {@link TransactionTemplate}), chaque étape déléguée à une méthode à intention unique d'un
 * service de domaine ({@link UtilisateurService}, {@link PrestataireService}, {@link ActionLinkService}).
 */
@RestController
@Slf4j
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController implements AdminApi {

    private final PrestataireService prestataireService;
    private final UtilisateurService utilisateurService;
    private final ActionLinkService actionLinkService;
    private final KeycloakAdminService keycloakAdminService;
    private final TransactionTemplate transactionTemplate;

    /**
     * Résultat interne à la transaction : la réponse HTTP et l'URL d'action créée, cette dernière
     * destinée à l'envoi du mail (étape 3, pas encore branché).
     *
     * @param response  la réponse à retourner au client
     * @param actionUrl l'URL front créée (token signé inclus)
     */
    private record ProvisioningResult(ProvisionPrestataireResponse response, String actionUrl) {}

    /**
     * Construit le controller avec ses dépendances.
     *
     * @param prestataireService   le service métier des prestataires
     * @param utilisateurService   le service métier des utilisateurs
     * @param actionLinkService    le service de création des liens d'action (token + URL front)
     * @param keycloakAdminService le service métier des interactions Keycloak
     * @param transactionManager   le gestionnaire de transaction Spring, utilisé pour construire le {@link TransactionTemplate}
     */
    public AdminController(
            PrestataireService prestataireService,
            UtilisateurService utilisateurService,
            ActionLinkService actionLinkService,
            KeycloakAdminService keycloakAdminService,
            PlatformTransactionManager transactionManager) {
        this.prestataireService = prestataireService;
        this.utilisateurService = utilisateurService;
        this.actionLinkService = actionLinkService;
        this.keycloakAdminService = keycloakAdminService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * Provisionne un prestataire :
     * 1. création du compte Keycloak (rôle PRO, sans mot de passe utilisable)
     * 2. BDD : Utilisateur + Prestataire (fiche vierge) + lien d'action en base dans une transaction courte.
     * Si la création DB échoue après que le compte KC a été créé, compense
     * en le supprimant pour ne jamais laisser de prestataire à moitié provisionné.
     * {@link TransactionTemplate} est utilisé plutôt que {@code @Transactional} : la frontière
     * transactionnelle doit démarrer précisément après l'appel Keycloak, dans la même méthode.
     *
     * @param request la requête de provisionnement
     * @return 201 Created avec les identifiants créés
     * @throws SlugAlreadyExistsException si le slug est déjà utilisé
     */
    @Override
    public ResponseEntity<ProvisionPrestataireResponse> provisionPrestataire(ProvisionPrestataireRequest request) {
        log.info("POST /admin/prestataires — slug={} email={}", request.slug(), request.email());

        // 1. vérifications
        if (prestataireService.existsBySlug(request.slug())) {
            throw new SlugAlreadyExistsException(request.slug());
        }

        // 2. Création du compte Keycloak sans mot de passe
        String kcUserId = keycloakAdminService.createProUserWithoutPassword(
                request.email(), request.firstName(), request.lastName());

        // 3. On ouvre une courte transaction pour créer les entités en BDD
        try {
            ProvisioningResult result =
                    transactionTemplate.execute(status -> {

                        // Utilisateur
                        Utilisateur utilisateur = utilisateurService.createUtilisateur(
                                request.firstName(), request.lastName(), request.email(), null
                        );

                        // Prestataire
                        Prestataire prestataire = prestataireService.createPrestataire(
                                utilisateur,
                                request.slug(),
                                request.prestataireName(),
                                request.category(),
                                List.of(StringUtils.tokenizeToStringArray(request.subcats(), ","))
                        );

                        // Lien d'action (token + URL front) — le payload est propre à ce flow
                        String actionUrl = actionLinkService.createLink(
                                ActionType.PRESTATAIRE_ONBOARDING, Map.of("email", utilisateur.getEmail()));

                        ProvisionPrestataireResponse response =
                                new ProvisionPrestataireResponse(
                                        prestataire.getId(), utilisateur.getId(), prestataire.getSlug()
                                );

                        return new ProvisioningResult(response, actionUrl);
                    });

            // TODO: envoyer le mail avec result.actionUrl() (étape 3, hors périmètre ici)
            log.info("Provisionnement prestataire réussi — identifiants créés : {}", result.response());

            // l'endpoint retourne les identifiants créés, mais pas le token (il sera envoyé par mail dans l'étape suivante)
            return ResponseEntity.status(HttpStatus.CREATED).body(result.response());
        } catch (RuntimeException e) {
            log.error("Échec de la création DB après création du compte KC {} — compensation (deleteUser)", kcUserId, e);
            // en cas d'erreur, suppression du compte KC
            keycloakAdminService.deleteUser(kcUserId);
            throw e;
        }
    }
}
