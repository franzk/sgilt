package net.franzka.sgilt.core.admin.controller;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.admin.api.AdminApi;
import net.franzka.sgilt.core.admin.dto.ProvisionPrestataireRequest;
import net.franzka.sgilt.core.admin.dto.ProvisionPrestataireResponse;
import net.franzka.sgilt.core.admin.exception.SlugAlreadyExistsException;
import net.franzka.sgilt.core.admin.service.AdminOnboardingService;
import net.franzka.sgilt.core.keycloak.KeycloakAdminService;
import net.franzka.sgilt.core.onboarding.dto.OnboardingPendingDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireAdminListItemDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireOnboardingPendingDto;
import net.franzka.sgilt.core.prestataire.service.PrestataireService;
import net.franzka.sgilt.core.reservation.domain.ReservationStatus;
import net.franzka.sgilt.core.reservation.dto.AdminReservationListItemDto;
import net.franzka.sgilt.core.reservation.service.ReservationService;
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
import java.util.UUID;

/**
 * Controller HTTP réservé à l'administration (rôle {@code ROLE_ADMIN}, distinct de PRO).
 * Provisionne un prestataire de bout en bout : Keycloak + DB + notification.
 * L'adaptation de la mire front {@code /verify} au parcours prestataire reste à faire (étape 4).
 * Orchestre lui-même Keycloak (hors transaction) puis la création DB (transaction courte via
 * {@link TransactionTemplate}) ; la fiche prestataire et sa notification associée sont
 * entièrement déléguées à {@link PrestataireService} — ce controller ignore quand et quel mail part.
 */
@RestController
@Slf4j
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController implements AdminApi {

    private final PrestataireService prestataireService;
    private final UtilisateurService utilisateurService;
    private final KeycloakAdminService keycloakAdminService;
    private final AdminOnboardingService adminOnboardingService;
    private final ReservationService reservationService;
    private final TransactionTemplate transactionTemplate;

    /**
     * Construit le controller avec ses dépendances.
     *
     * @param prestataireService   le service métier des prestataires
     * @param utilisateurService   le service métier des utilisateurs
     * @param keycloakAdminService le service métier des interactions Keycloak
     * @param adminOnboardingService le service de suivi et de relance des onboardings en attente
     * @param reservationService  le service métier des réservations
     * @param transactionManager   le gestionnaire de transaction Spring, utilisé pour construire le {@link TransactionTemplate}
     */
    public AdminController(
            PrestataireService prestataireService,
            UtilisateurService utilisateurService,
            KeycloakAdminService keycloakAdminService,
            AdminOnboardingService adminOnboardingService,
            ReservationService reservationService,
            PlatformTransactionManager transactionManager) {
        this.prestataireService = prestataireService;
        this.utilisateurService = utilisateurService;
        this.keycloakAdminService = keycloakAdminService;
        this.adminOnboardingService = adminOnboardingService;
        this.reservationService = reservationService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * Provisionne un prestataire :
     * 1. compte Keycloak (rôle PRO, sans mot de passe utilisable)
     * 2. transaction courte : Utilisateur + Prestataire, clé-en-main ou autonome selon
     * {@code request.cleEnMain()} (voir {@link PrestataireService#createPrestataireCleEnMain} et
     * {@link PrestataireService#createPrestataireAutonome})
     * Si la création DB échoue, le compte KC est supprimé (compensation). Si seule la notification
     * échoue, rien n'est compensé — l'endpoint renvoie juste une erreur.
     * {@link TransactionTemplate} plutôt que {@code @Transactional} : la frontière transactionnelle
     * doit démarrer précisément après l'appel Keycloak, dans la même méthode.
     *
     * @param request la requête de provisionnement
     * @return 201 Created avec les identifiants créés, ou 500 si la notification n'a pas pu être envoyée
     * @throws SlugAlreadyExistsException si le slug est déjà utilisé
     */
    @Override
    public ResponseEntity<ProvisionPrestataireResponse> provisionPrestataire(ProvisionPrestataireRequest request) {
        log.info("POST /admin/prestataires — slug={} email={} cleEnMain={}",
                request.slug(), request.email(), request.cleEnMain());

        // 1. vérifications
        if (prestataireService.existsBySlug(request.slug())) {
            throw new SlugAlreadyExistsException(request.slug());
        }

        // 2. Création du compte Keycloak sans mot de passe
        String kcUserId = keycloakAdminService.createProUserWithoutPassword(
                request.email(), request.firstName(), request.lastName());

        // 3. On ouvre une courte transaction pour créer les entités en BDD
        try {
            PrestataireService.CreationResult result =
                    transactionTemplate.execute(status -> {

                        Utilisateur utilisateur = utilisateurService.createUtilisateur(
                                request.firstName(), request.lastName(), request.email(), null
                        );

                        List<String> subcatKeys = List.of(StringUtils.tokenizeToStringArray(request.subcats(), ","));

                        return request.cleEnMain()
                                ? prestataireService.createPrestataireCleEnMain(
                                        utilisateur, request.slug(), request.prestataireName(), request.category(), subcatKeys)
                                : prestataireService.createPrestataireAutonome(
                                        utilisateur, request.slug(), request.prestataireName(), request.category(), subcatKeys);
                    });

            ProvisionPrestataireResponse response = new ProvisionPrestataireResponse(
                    result.prestataire().getId(), result.prestataire().getUtilisateur().getId(), result.prestataire().getSlug());
            log.info("Provisionnement prestataire réussi — identifiants créés : {}", response);

            if (!result.notificationDelivered()) {
                return ResponseEntity.internalServerError().build();
            }

            // l'endpoint retourne les identifiants créés, mais pas le token (transmis uniquement par email)
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Échec de la création DB après création du compte KC {} — compensation (deleteUser)", kcUserId, e);
            // en cas d'erreur, suppression du compte KC
            keycloakAdminService.deleteUser(kcUserId);
            throw e;
        }
    }

    /**
     * Liste tous les prestataires actifs avec leur statut, pour le back-office admin.
     *
     * @return la liste des fiches (id, name, slug, status)
     */
    @Override
    @Transactional
    public ResponseEntity<List<PrestataireAdminListItemDto>> listPrestataires() {
        log.info("GET /admin/prestataires");
        return ResponseEntity.ok(prestataireService.getConfirmedPrestataires());
    }

    /**
     * Publie une fiche prestataire — voir {@link PrestataireService#publish} pour le détail de la
     * notification envoyée.
     *
     * @param id identifiant du prestataire à publier
     * @return 204 No Content, ou 500 si la notification n'a pas pu être envoyée
     */
    @Override
    @Transactional
    public ResponseEntity<Void> publishPrestataire(UUID id) {
        log.info("POST /admin/prestataires/{}/publish", id);

        if (!prestataireService.publish(id)) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Renvoie une fiche publiée en revue — modération admin (passe de PUBLISHED à IN_REVIEW).
     *
     * @param id identifiant du prestataire à renvoyer en revue
     * @return 204 No Content
     */
    @Override
    @Transactional
    public ResponseEntity<Void> sendPrestataireBackToReview(UUID id) {
        log.info("POST /admin/prestataires/{}/send-to-review", id);
        prestataireService.sendBackToReview(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Liste tous les prestataires dont l'onboarding est en attente — le lien envoyé par email n'a
     * pas encore été cliqué.
     *
     * @return la liste des onboardings en attente
     */
    @Override
    @Transactional
    public ResponseEntity<List<PrestataireOnboardingPendingDto>> listPendingOnboardings() {
        log.info("GET /admin/prestataires/onboarding-pending");
        return ResponseEntity.ok(adminOnboardingService.listPendingOnboardings());
    }

    /**
     * Renvoie le mail d'activation à un prestataire dont l'onboarding est en attente, en
     * réinitialisant la période de validité du lien.
     *
     * @param id identifiant du prestataire dont l'onboarding doit être relancé
     * @return 204 No Content, ou 500 si l'email n'a pas pu être envoyé
     */
    @Override
    @Transactional
    public ResponseEntity<Void> resendOnboardingEmail(UUID id) {
        log.info("POST /admin/prestataires/{}/resend-onboarding-email", id);
        boolean mailSent = adminOnboardingService.resendOnboardingEmail(id);
        if (!mailSent) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Liste toutes les réservations pour le back-office admin, triées par date de création
     * décroissante, filtrées par statut si fourni.
     *
     * @param status le statut à filtrer, ou {@code null} pour toutes les réservations
     * @return la liste des réservations
     */
    @Override
    @Transactional
    public ResponseEntity<List<AdminReservationListItemDto>> listReservations(ReservationStatus status) {
        log.info("GET /admin/reservations — status={}", status);
        return ResponseEntity.ok(reservationService.getAdminReservations(status));
    }

    /**
     * Liste les sessions d'onboarding utilisateur (client) en attente — le lien envoyé par email
     * n'a pas encore été utilisé pour finaliser la création du compte.
     *
     * @return la liste des sessions en attente
     */
    @Override
    @Transactional
    public ResponseEntity<List<OnboardingPendingDto>> listPendingUserOnboardings() {
        log.info("GET /admin/onboarding-pending");
        return ResponseEntity.ok(adminOnboardingService.listPendingUserOnboardings());
    }
}
