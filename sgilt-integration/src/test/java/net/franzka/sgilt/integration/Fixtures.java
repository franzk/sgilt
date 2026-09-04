package net.franzka.sgilt.integration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Helpers JDBC pour créer des fixtures réalistes en base (JPA/Hibernate n'est pas disponible ici —
 * ce module n'a pas de dépendance Spring, il communique avec le vrai conteneur {@code sgilt-core}
 * uniquement en HTTP). Un parcours de test insère l'état initial nécessaire (ex. un prestataire publié
 * avant de soumettre une demande d'onboarding le ciblant), puis exerce le vrai flow via l'API HTTP —
 * jamais l'inverse (pas de fixture pour l'état que le flow est censé produire lui-même).
 */
public final class Fixtures {

    private final String jdbcUrl;
    private final String user;
    private final String password;

    public Fixtures(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
    }

    public UUID insertUtilisateur(String firstName, String lastName, String email) {
        UUID id = UUID.randomUUID();
        update("""
                INSERT INTO utilisateurs (id, first_name, last_name, email, status, created_at)
                VALUES (?, ?, ?, ?, 'ACTIVE', now())
                """, id, firstName, lastName, email);
        return id;
    }

    /** Prestataire publié par défaut — visible en recherche publique et ciblable par un onboarding. */
    public UUID insertPrestataire(UUID utilisateurId, String name, String slug) {
        UUID id = UUID.randomUUID();
        update("""
                INSERT INTO prestataires (id, utilisateur_id, name, slug, category_key, status, flow, created_at)
                VALUES (?, ?, ?, ?, 'photographe', 'PUBLISHED', 'AUCUN', now())
                """, id, utilisateurId, name, slug);
        return id;
    }

    public UUID insertEvenement(UUID utilisateurId, String name, LocalDate date) {
        UUID id = UUID.randomUUID();
        update("""
                INSERT INTO evenements (id, utilisateur_id, name, date, status, created_at)
                VALUES (?, ?, ?, ?, 'ACTIVE', now())
                """, id, utilisateurId, name, date);
        return id;
    }

    public UUID insertReservation(UUID evenementId, UUID prestataireId, UUID utilisateurId, LocalDate date, String status) {
        UUID id = UUID.randomUUID();
        update("""
                INSERT INTO reservations (id, evenement_id, prestataire_id, utilisateur_id, date, status, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?::reservation_status, now(), now())
                """, id, evenementId, prestataireId, utilisateurId, date, status);
        return id;
    }

    /** Payload HMAC de la session d'onboarding créée par le dernier {@code POST /onboarding} pour cet email. */
    public String getOnboardingHmacPayloadByEmail(String email) {
        return query("SELECT hmac_payload FROM onboarding WHERE email = ? ORDER BY created_at DESC LIMIT 1",
                statement -> statement.setString(1, email),
                rs -> rs.getString("hmac_payload"));
    }

    /** Force l'expiration de la session d'onboarding OPEN d'un email, pour tester le cas 410. */
    public void expireOnboardingByEmail(String email) {
        update("UPDATE onboarding SET expires_at = now() - interval '1 hour' WHERE email = ?", email);
    }

    /**
     * Payload HMAC du dernier {@code ActionToken} (flow onboarding prestataire) créé pour cet email
     * — même mécanisme HMAC que {@link #getOnboardingHmacPayloadByEmail}, table différente (l'email
     * est dans le jsonb {@code payload}, pas une colonne dédiée).
     */
    public String getActionTokenHmacPayloadByEmail(String email) {
        return query("""
                SELECT hmac_payload FROM action_tokens
                WHERE payload ->> 'email' = ? ORDER BY created_at DESC LIMIT 1
                """,
                statement -> statement.setString(1, email),
                rs -> rs.getString("hmac_payload"));
    }

    private void update(String sql, Object... params) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            statement.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException("Fixture insert failed: " + sql, e);
        }
    }

    private <T> T query(String sql, SqlConsumer<PreparedStatement> binder, SqlFunction<ResultSet, T> mapper) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            binder.accept(statement);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException("No row found for query: " + sql);
                }
                return mapper.apply(rs);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Fixture query failed: " + sql, e);
        }
    }

    @FunctionalInterface
    private interface SqlConsumer<T> {
        void accept(T t) throws Exception;
    }

    @FunctionalInterface
    private interface SqlFunction<T, R> {
        R apply(T t) throws Exception;
    }
}
