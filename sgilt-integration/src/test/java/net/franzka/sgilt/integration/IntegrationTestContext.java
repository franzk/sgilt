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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private static final String TEST_PASSWORD = "Test1234!";
    private static final String DB_NAME = "sgilt";
    private static final String DB_USER = "sgilt";
    private static final String DB_PASSWORD = "sgilt";
    private static final String REALM = "sgilt";
    private static final String SGILT_CORE_IMAGE = "sgilt-core:integration-test";
    private static final AtomicBoolean PROVISIONED = new AtomicBoolean(false);

    static final Network network = Network.newNetwork();

    static final PostgreSQLContainer<?> postgres;
    static final RabbitMQContainer rabbitmq;
    static final KeycloakContainer keycloak;
    static final GenericContainer<?> sgiltCore;

    static {
        postgres = new PostgreSQLContainer<>("postgres:17")
                .withNetwork(network)
                .withNetworkAliases("db")
                .withDatabaseName(DB_NAME)
                .withUsername(DB_USER)
                .withPassword(DB_PASSWORD);
        postgres.start();

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

        buildSgiltCoreImage();

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
                .withEnv("CONFIRMATION_TOKEN_SECRET", "integration-test-confirmation-secret")
                .withEnv("OPENAI_API_KEY", "unused-in-integration-tests")
                // Wait.forHttp() sur un endpoint réel s'est révélé peu fiable ici : la toute première
                // requête HTTP initialise le DispatcherServlet à la volée, et ce délai suffit parfois à
                // faire échouer/reset la connexion de probe. La ligne de log de démarrage de Spring Boot
                // est un signal de disponibilité plus fiable.
                .waitingFor(Wait.forLogMessage(".*Started SgiltCoreApplication.*\\n", 1))
                .withStartupTimeout(Duration.ofMinutes(3));
        sgiltCore.start();
    }

    /**
     * Build l'image {@code sgilt-core} via le CLI {@code docker build} (BuildKit) plutôt que
     * {@link org.testcontainers.images.builder.ImageFromDockerfile}, qui passe par l'ancienne API de
     * build du démon Docker : celle-ci rejette la ligne {@code COPY --from=build /app/build/libs/*.jar
     * ./app.jar} du {@code Dockerfile} dès que le plugin Gradle Spring Boot produit aussi le jar "plain"
     * en plus du jar exécutable (2 fichiers matchés par le glob vers une destination non-répertoire).
     * Vérifié : {@code docker build} (BuildKit, celui utilisé par {@code deploy.yml}) construit cette
     * même image sans erreur — c'est une limite de l'ancienne API, pas un bug du Dockerfile de prod.
     */
    private static void buildSgiltCoreImage() {
        try {
            Process process = new ProcessBuilder("docker", "build", "-t", SGILT_CORE_IMAGE, "../sgilt-core")
                    .inheritIO()
                    .start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IllegalStateException("docker build sgilt-core failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Failed to build sgilt-core Docker image", e);
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

        KeycloakTestClient keycloakClient = new KeycloakTestClient(keycloak.getAuthServerUrl(), REALM);

        provisionUser(keycloakClient, "user-test@sgilt.test", "karate.token.user");
        provisionUser(keycloakClient, "pro-test@sgilt.test", "karate.token.pro");
        provisionUser(keycloakClient, "admin-test@sgilt.test", "karate.token.admin");

        System.setProperty("karate.token.orphan", keycloakClient.fetchUserToken("orphan-test@sgilt.test", TEST_PASSWORD));
    }

    /**
     * Prénom/nom ne sont pas re-déclarés ici : ils sont extraits du JWT lui-même (claims {@code
     * given_name}/{@code family_name}), Keycloak les ayant déjà lus depuis le realm import — évite de
     * dupliquer la même identité à deux endroits (JSON réaliste + code Java).
     */
    private static void provisionUser(KeycloakTestClient keycloakClient, String email, String tokenSystemProperty) {
        String token = keycloakClient.fetchUserToken(email, TEST_PASSWORD);
        KeycloakTestClient.TokenIdentity identity = KeycloakTestClient.decodeIdentity(token);
        insertUtilisateur(identity.firstName(), identity.lastName(), identity.email());
        System.setProperty(tokenSystemProperty, token);
    }

    private static void insertUtilisateur(String firstName, String lastName, String email) {
        String url = postgres.getJdbcUrl();
        try (Connection connection = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO utilisateurs (id, first_name, last_name, email, status, created_at)
                     VALUES (?, ?, ?, ?, 'ACTIVE', now())
                     """)) {
            statement.setObject(1, UUID.randomUUID());
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, email);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to insert Utilisateur row for " + email, e);
        }
    }
}
