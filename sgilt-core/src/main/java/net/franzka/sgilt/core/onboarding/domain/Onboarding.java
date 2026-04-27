package net.franzka.sgilt.core.onboarding.domain;

import jakarta.persistence.*;
import lombok.*;
import net.franzka.sgilt.core.prestataire.domain.Prestataire;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant une session d'onboarding.
 * Regroupe le token de confirmation envoyé par email et le snapshot des données saisies dans le tunnel.
 * Créée à la soumission du tunnel, consommée et supprimée à la création du compte.
 */
@Entity
@Table(name = "onboarding")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Onboarding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String hmacPayload;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "onboarding_state", nullable = false)
    private OnboardingState state;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime confirmationPeriodExpiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestataire_id", nullable = false)
    private Prestataire prestataire;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "jsonb", nullable = false)
    private String data;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.state = OnboardingState.OPEN;
    }
}
