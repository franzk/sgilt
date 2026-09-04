package net.franzka.sgilt.integration;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.nio.charset.StandardCharsets;

/**
 * Base des tests d'intégration boîte noire : démarre Postgres, RabbitMQ et Keycloak (Testcontainers),
 * puis construit et démarre le vrai conteneur Docker de {@code sgilt-core} (celui buildé pour la prod,
 * via son propre {@code Dockerfile}) sur le même réseau Docker. Les users de test sont pré-déclarés
 * dans le realm import ({@code keycloak/realm-test.json}) ; cette classe crée seulement leur ligne
 * {@code Utilisateur} en base et publie {@code baseUrl}/des JWT réels en System properties pour
 * {@code karate-config.js}.
 */
@Tag("integration")
public abstract class IntegrationTestContext {

    protected static final String TEST_PASSWORD = "Test1234!";
    private static final String DB_NAME = "sgilt";
    private static final String DB_USER = "sgilt";
    private static final String DB_PASSWORD = "sgilt";
    private static final String REALM = "sgilt";
    private static final String SGILT_CORE_IMAGE = "sgilt-core:integration-test";
    private static final String SGILT_NOTIFICATIONS_IMAGE = "sgilt-notifications:integration-test";
    protected static final String CONFIRMATION_SECRET = "integration-test-confirmation-secret";
    private static final AtomicBoolean PROVISIONED = new AtomicBoolean(false);

    static final Network network = Network.newNetwork();

    static final PostgreSQLContainer<?> postgres;
    static final RabbitMQContainer rabbitmq;
    static final KeycloakContainer keycloak;
    static final GenericContainer<?> sgiltCore;
    static final GenericContainer<?> sgiltNotifications;
    protected static final Fixtures fixtures;

    static {
        postgres = new PostgreSQLContainer<>("postgres:17")
                .withNetwork(network)
                .withNetworkAliases("db")
                .withDatabaseName(DB_NAME)
                .withUsername(DB_USER)
                .withPassword(DB_PASSWORD);
        postgres.start();
        fixtures = new Fixtures(postgres.getJdbcUrl(), DB_USER, DB_PASSWORD);

        rabbitmq = new RabbitMQContainer("rabbitmq:4-management-alpine")
                .withNetwork(network)
                .withNetworkAliases("rabbitmq");
        rabbitmq.start();

        // KC_HOSTNAME force Keycloak à toujours annoncer ce hostname dans les tokens/discovery,
        // quel que soit le Host header réellement utilisé pour l'atteindre. Indispensable ici : le
        // client de provisioning (sur l'host) et sgilt-core (dans son conteneur) atteignent Keycloak
        // par deux chemins différents (port mappé vs alias réseau interne) — sans hostname fixe, les
        // deux verraient un `iss` différent selon le chemin emprunté, et sgilt-core rejetterait tous
        // les JWT (issuer mismatch). "host.docker.internal" est résolu par les deux (Docker Desktop
        // l'injecte dans tous les conteneurs, et il route vers le port publié sur l'host).
        keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:26.2.2")
                .withRealmImportFile("keycloak/realm-test.json")
                .withEnv("KC_HOSTNAME", "host.docker.internal")
                .withNetwork(network)
                .withNetworkAliases("keycloak");
        keycloak.start();

        String keycloakExternalUrl = "http://host.docker.internal:" + keycloak.getMappedPort(8080);

        buildImage(SGILT_CORE_IMAGE, "../sgilt-core");

        sgiltCore = new GenericContainer<>(SGILT_CORE_IMAGE)
                .withLogConsumer(frame -> System.out.print("[sgilt-core] " + frame.getUtf8String()))
                .withNetwork(network)
                .withExposedPorts(5027)
                .withEnv("DB_URL", "jdbc:postgresql://db:5432/" + DB_NAME)
                .withEnv("DB_USERNAME", DB_USER)
                .withEnv("DB_PASSWORD", DB_PASSWORD)
                .withEnv("RABBITMQ_HOST", "rabbitmq")
                .withEnv("RABBITMQ_PORT", "5672")
                .withEnv("RABBITMQ_USERNAME", rabbitmq.getAdminUsername())
                .withEnv("RABBITMQ_PASSWORD", rabbitmq.getAdminPassword())
                .withEnv("KEYCLOAK_ISSUER", keycloakExternalUrl + "/realms/" + REALM)
                .withEnv("KC_ADMIN_URL", keycloakExternalUrl)
                .withEnv("KC_ADMIN_CLIENT_SECRET", "dev-admin-secret")
                .withEnv("CONFIRMATION_TOKEN_SECRET", CONFIRMATION_SECRET)
                .withEnv("OPENAI_API_KEY", "unused-in-integration-tests")
                // Wait.forHttp() sur un endpoint réel s'est révélé peu fiable ici : la toute première
                // requête HTTP initialise le DispatcherServlet à la volée, et ce délai suffit parfois à
                // faire échouer/reset la connexion de probe. La ligne de log de démarrage de Spring Boot
                // est un signal de disponibilité plus fiable.
                .waitingFor(Wait.forLogMessage(".*Started SgiltCoreApplication.*\\n", 1))
                .withStartupTimeout(Duration.ofMinutes(3));
        sgiltCore.start();

        // sgilt-notifications consomme les évènements de domaine publiés par sgilt-core sur l'exchange
        // topic partagé "domain-events" (RabbitMQ) et partage la même base Postgres (son propre schéma
        // Flyway "sgilt-notifications") — aucun nouveau conteneur DB nécessaire, juste ce 2e conteneur
        // applicatif sur le même réseau.
        buildImage(SGILT_NOTIFICATIONS_IMAGE, "../sgilt-notifications");

        sgiltNotifications = new GenericContainer<>(SGILT_NOTIFICATIONS_IMAGE)
                .withLogConsumer(frame -> System.out.print("[sgilt-notifications] " + frame.getUtf8String()))
                .withNetwork(network)
                .withExposedPorts(5031)
                .withEnv("DB_URL", "jdbc:postgresql://db:5432/" + DB_NAME)
                .withEnv("DB_USERNAME", DB_USER)
                .withEnv("DB_PASSWORD", DB_PASSWORD)
                .withEnv("RABBITMQ_HOST", "rabbitmq")
                .withEnv("RABBITMQ_PORT", "5672")
                .withEnv("RABBITMQ_USERNAME", rabbitmq.getAdminUsername())
                .withEnv("RABBITMQ_PASSWORD", rabbitmq.getAdminPassword())
                .withEnv("KEYCLOAK_ISSUER", keycloakExternalUrl + "/realms/" + REALM)
                .waitingFor(Wait.forLogMessage(".*Started SgiltNotificationsApplication.*\\n", 1))
                .withStartupTimeout(Duration.ofMinutes(3));
        sgiltNotifications.start();
    }

    /**
     * Build une image via le CLI {@code docker build} (BuildKit) plutôt que
     * {@link org.testcontainers.images.builder.ImageFromDockerfile}, qui passe par l'ancienne API de
     * build du démon Docker : celle-ci rejette la ligne {@code COPY --from=build /app/build/libs/*.jar
     * ./app.jar} des Dockerfiles Spring Boot dès que le plugin Gradle produit aussi le jar "plain" en
     * plus du jar exécutable (2 fichiers matchés par le glob vers une destination non-répertoire).
     * Vérifié : {@code docker build} (BuildKit, celui utilisé par {@code deploy.yml}) construit ces
     * mêmes images sans erreur — c'est une limite de l'ancienne API, pas un bug des Dockerfiles de prod.
     */
    private static void buildImage(String tag, String contextPath) {
        try {
            Process process = new ProcessBuilder("docker", "build", "-t", tag, contextPath)
                    .inheritIO()
                    .start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IllegalStateException("docker build " + tag + " failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Failed to build Docker image " + tag, e);
        }
    }

    /**
     * Les 4 users de test (USER/PRO/ADMIN + un "orphelin") sont pré-déclarés dans le realm import
     * ({@code keycloak/realm-test.json}) — ce sont des fixtures fixes, pas un flow à exercer, donc
     * pas besoin de les créer via l'API Admin à chaque run. Cette méthode se contente de leur créer
     * la ligne {@code Utilisateur} correspondante en base (sauf l'orphelin, volontairement absent —
     * JWT valide sans ligne DB, cas 404) et de publier {@code baseUrl}/leurs JWT réels en System
     * properties. Idempotent (une seule fois par JVM, même si plusieurs classes de test héritent de
     * cette base).
     */
    @BeforeAll
    static void provisionOnce() {
        if (!PROVISIONED.compareAndSet(false, true)) {
            return;
        }
        System.setProperty("karate.baseUrl", "http://" + sgiltCore.getHost() + ":" + sgiltCore.getMappedPort(5027));
        System.setProperty("karate.notificationsBaseUrl",
                "http://" + sgiltNotifications.getHost() + ":" + sgiltNotifications.getMappedPort(5031));

        KeycloakTestClient keycloakClient = new KeycloakTestClient(keycloak.getAuthServerUrl(), REALM);

        provisionUser(keycloakClient, "user-test@sgilt.test", "karate.token.user");
        UUID prestataireUtilisateurId = provisionUser(keycloakClient, "prestataire-test@sgilt.test", "karate.token.prestataire");
        provisionUser(keycloakClient, "pro-test@sgilt.test", "karate.token.pro");
        provisionUser(keycloakClient, "admin-test@sgilt.test", "karate.token.admin");

        System.setProperty("karate.token.orphan", keycloakClient.fetchUserToken("orphan-test@sgilt.test", TEST_PASSWORD));

        // Prestataire publié partagé entre les parcours qui ont besoin de cibler une fiche existante
        // (onboarding client, recherche publique, édition de fiche...).
        UUID prestataireId = fixtures.insertPrestataire(prestataireUtilisateurId, "Studio Test", "studio-test");
        System.setProperty("karate.fixture.prestataireId", prestataireId.toString());

        // URL complète (vhost déjà encodé en %2F) : évite que le `path()` de Karate ré-encode le
        // "/" du vhost par défaut en %252F.
        String mailSendQueueUrl = "http://" + rabbitmq.getHost() + ":" + rabbitmq.getMappedPort(15672)
                + "/api/queues/%2F/mail.send/get";
        System.setProperty("karate.mailSendQueueUrl", mailSendQueueUrl);
        String basicAuth = Base64.getEncoder().encodeToString(
                (rabbitmq.getAdminUsername() + ":" + rabbitmq.getAdminPassword()).getBytes(StandardCharsets.UTF_8));
        System.setProperty("karate.mailQueueAuth", "Basic " + basicAuth);
    }

    /**
     * Prénom/nom ne sont pas re-déclarés ici : ils sont extraits du JWT lui-même (claims {@code
     * given_name}/{@code family_name}), Keycloak les ayant déjà lus depuis le realm import — évite de
     * dupliquer la même identité à deux endroits (JSON réaliste + code Java).
     *
     * @return l'UUID {@code Utilisateur} créé, pour lier d'autres fixtures (ex. un Prestataire)
     */
    private static UUID provisionUser(KeycloakTestClient keycloakClient, String email, String tokenSystemProperty) {
        String token = keycloakClient.fetchUserToken(email, TEST_PASSWORD);
        KeycloakTestClient.TokenIdentity identity = KeycloakTestClient.decodeIdentity(token);
        UUID utilisateurId = fixtures.insertUtilisateur(identity.firstName(), identity.lastName(), identity.email());
        System.setProperty(tokenSystemProperty, token);
        return utilisateurId;
    }

    /**
     * Reconstruit le token de confirmation {@code payload-signature} envoyé par email lors d'un
     * onboarding — voir {@link ConfirmationToken}. Appelé depuis les {@code .feature} via l'interop
     * Java de Karate (pas de MailHog dans ce module : le payload est lu en base, la signature HMAC
     * est déterministe avec le secret partagé configuré sur {@code sgiltCore}).
     */
    public static String buildConfirmationTokenForEmail(String email) {
        String payload = fixtures.getOnboardingHmacPayloadByEmail(email);
        return ConfirmationToken.build(payload, CONFIRMATION_SECRET);
    }

    /**
     * Construit un token valide (signature correcte) pour un payload arbitraire — utile pour tester
     * le cas "token bien formé mais session introuvable" (404), distinct d'un token à la signature
     * invalide (400).
     */
    public static String buildConfirmationTokenForPayload(String payload) {
        return ConfirmationToken.build(payload, CONFIRMATION_SECRET);
    }

    /** Récupère un JWT réel pour un user créé en cours de scénario (ex. via le flow onboarding). */
    public static String fetchTokenForUser(String email, String password) {
        return new KeycloakTestClient(keycloak.getAuthServerUrl(), REALM).fetchUserToken(email, password);
    }
}
