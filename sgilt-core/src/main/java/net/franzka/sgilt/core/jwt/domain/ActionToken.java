package net.franzka.sgilt.core.jwt.domain;

import jakarta.persistence.*;
import lombok.*;
import net.franzka.sgilt.core.jwt.service.VerificationTokenHmacService;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant un token d'action générique, signé HMAC.
 * Le champ {@code type} discrimine le flow auquel le token se rattache — les étapes suivantes
 * du flow (mail, vérification) s'en servent pour router vers le bon traitement front.
 * {@code payload} est un jsonb libre propre à chaque flow (ex. l'email du destinataire) : le
 * mécanisme générique ne présuppose pas que le flow ait déjà un {@code Utilisateur} en base.
 * Seul le payload HMAC (à ne pas confondre avec {@code payload}) est stocké pour le token lui-même :
 * le token complet ({@code payload-signature}) est dérivable via
 * {@link VerificationTokenHmacService#buildToken(String)}, comme pour {@code Onboarding}.
 */
@Entity
@Table(name = "action_tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String hmacPayload;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "action_type", nullable = false)
    private ActionType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String payload;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
