package net.franzka.sgilt.core.admin.controller;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.admin.api.AdminApi;
import net.franzka.sgilt.core.admin.dto.ProvisionPrestataireRequest;
import net.franzka.sgilt.core.admin.dto.ProvisionPrestataireResponse;
import net.franzka.sgilt.core.admin.exception.SlugAlreadyExistsException;
import net.franzka.sgilt.core.admin.mailer.AdminMailerService;
import net.franzka.sgilt.core.admin.service.AdminOnboardingService;
import net.franzka.sgilt.core.jwt.domain.ActionType;
import net.franzka.sgilt.core.jwt.service.ActionLinkService;
import net.franzka.sgilt.core.keycloak.KeycloakAdminService;
import net.franzka.sgilt.core.onboarding.dto.OnboardingPendingDto;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import net.franzka.sgilt.core.prestataire.domain.PrestataireStatus;
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
import java.util.Map;
import java.util.UUID;

/**
 * Controller HTTP réservé à l'administration (rôle {@code ROLE_ADMIN}, distinct de PRO).
 * Provisionne un prestataire de bout en bout : Keycloak + DB + email d'activation.
 * L'adaptation de la mire front {@code /verify} au parcours prestataire reste à faire (étape 4).
 * Orchestre lui-même Keycloak (hors transaction) puis la création DB (transaction courte via
 * {@link TransactionTemplate}), chaque étape déléguée à une méthode à intention unique d'un
 * service de domaine ({@link UtilisateurService}, {@link PrestataireService}, {@link ActionLinkService},
 * {@link AdminMailerService}).
 */
@RestController
@Slf4j
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController implements AdminApi {

    private final PrestataireService prestataireService;
    private final UtilisateurService utilisateurService;
    private final ActionLinkService actionLinkService;
    private final KeycloakAdminService keycloakAdminService;
    private final AdminMailerService adminMailerService;
    private final AdminOnboardingService adminOnboardingService;
    private final ReservationService reservationService;
    private final TransactionTemplate transactionTemplate;

    /**
     * Résultat interne à la transaction : la réponse HTTP et l'URL d'action créée, cette dernière
     * utilisée juste après pour l'envoi du mail d'activation.
     *
     * @param response  la réponse à retourner au client
     * @param actionUrl l'URL front créée (token signé inclus), ou {@code null} en mode clé-en-main
     *                  (aucun token créé, aucun mail envoyé à ce stade)
     */
    private record ProvisioningResult(ProvisionPrestataireResponse response, String actionUrl) {}

    /**
     * Construit le controller avec ses dépendances.
     *
     * @param prestataireService   le service métier des prestataires
     * @param utilisateurService   le service métier des utilisateurs
     * @param actionLinkService    le service de création des liens d'action (token + URL front)
     * @param keycloakAdminService le service métier des interactions Keycloak
     * @param adminMailerService   le service d'envoi de l'email d'activation prestataire
     * @param adminOnboardingService le service de suivi et de relance des onboardings en attente
     * @param reservationService  le service métier des réservations
     * @param transactionManager   le gestionnaire de transaction Spring, utilisé pour construire le {@link TransactionTemplate}
     */
    public AdminController(
            PrestataireService prestataireService,
            UtilisateurService utilisateurService,
            ActionLinkService actionLinkService,
            KeycloakAdminService keycloakAdminService,
            AdminMailerService adminMailerService,
            AdminOnboardingService adminOnboardingService,
            ReservationService reservationService,
            PlatformTransactionManager transactionManager) {
        this.prestataireService = prestataireService;
        this.utilisateurService = utilisateurService;
        this.actionLinkService = actionLinkService;
        this.keycloakAdminService = keycloakAdminService;
        this.adminMailerService = adminMailerService;
        this.adminOnboardingService = adminOnboardingService;
        this.reservationService = reservationService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * Provisionne un prestataire :
     * 1. création du compte Keycloak (rôle PRO, sans mot de passe utilisable)
     * 2. BDD : Utilisateur + Prestataire (fiche vierge) dans une transaction courte.
     * 3. si {@code !request.cleEnMain()} (flow autonome) : lien d'action + envoi de l'email d'activation,
     * une fois la transaction commitée. Si {@code request.cleEnMain()} (flow clé-en-main), ni l'un ni
     * l'autre — la fiche est créée avec le statut {@link PrestataireStatus#WAITING_FOR_CREATION_SERVICE},
     * le token et le mail sont différés jusqu'à la publication (voir {@link PrestataireService#publish}).
     * Si la création DB échoue après que le compte KC a été créé, compense
     * en le supprimant pour ne jamais laisser de prestataire à moitié provisionné.
     * Si l'envoi du mail échoue (flow autonome uniquement), rien n'est compensé (le compte KC,
     * l'utilisateur, le prestataire et le token restent en l'état — un nouvel appel recréerait un
     * conflit de slug/email), mais l'endpoint renvoie une erreur pour que l'appelant sache que le
     * mail n'est pas parti.
     * {@link TransactionTemplate} est utilisé plutôt que {@code @Transactional} : la frontière
     * transactionnelle doit démarrer précisément après l'appel Keycloak, dans la même méthode.
     *
     * @param request la requête de provisionnement
     * @return 201 Created avec les identifiants créés, ou 500 si l'email n'a pas pu être envoyé
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
            ProvisioningResult result =
                    transactionTemplate.execute(status -> {

                        // Utilisateur
                        Utilisateur utilisateur = utilisateurService.createUtilisateur(
                                request.firstName(), request.lastName(), request.email(), null
                        );

                        // Prestataire — statut de départ selon le flow choisi
                        PrestataireStatus initialStatus = request.cleEnMain()
                                ? PrestataireStatus.WAITING_FOR_CREATION_SERVICE
                                : PrestataireStatus.DRAFT;
                        Prestataire prestataire = prestataireService.createPrestataire(
                                utilisateur,
                                request.slug(),
                                request.prestataireName(),
                                request.category(),
                                List.of(StringUtils.tokenizeToStringArray(request.subcats(), ",")),
                                initialStatus
                        );

                        // Lien d'action (token + URL front) — uniquement pour le flow autonome ;
                        // en clé-en-main, ni le token ni le mail ne sont créés avant la publication
                        String actionUrl = request.cleEnMain()
                                ? null
                                : actionLinkService.createLink(
                                        ActionType.PRESTATAIRE_ONBOARDING, Map.of("email", utilisateur.getEmail()));

                        ProvisionPrestataireResponse response =
                                new ProvisionPrestataireResponse(
                                        prestataire.getId(), utilisateur.getId(), prestataire.getSlug()
                                );

                        return new ProvisioningResult(response, actionUrl);
                    });

            log.info("Provisionnement prestataire réussi — identifiants créés : {}", result.response());

            // 4. envoi du mail d'activation (flow autonome uniquement), une fois la transaction commitée
            if (!request.cleEnMain()) {
                boolean mailSent = adminMailerService.sendPrestataireOnboardingEmail(
                        request.email(), request.firstName(), result.actionUrl());
                if (!mailSent) {
                    return ResponseEntity.internalServerError().build();
                }
            }

            // l'endpoint retourne les identifiants créés, mais pas le token (transmis uniquement par email)
            return ResponseEntity.status(HttpStatus.CREATED).body(result.response());
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
     * Publie une fiche prestataire (passe de IN_REVIEW à PUBLISHED).
     *
     * @param id identifiant du prestataire à publier
     * @return 204 No Content
     */
    @Override
    @Transactional
    public ResponseEntity<Void> publishPrestataire(UUID id) {
        log.info("POST /admin/prestataires/{}/publish", id);
        prestataireService.publish(id);
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
