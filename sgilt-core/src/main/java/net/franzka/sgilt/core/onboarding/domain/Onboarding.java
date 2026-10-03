package net.franzka.sgilt.core.onboarding.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant une session d'onboarding : un visiteur a commencé l'organisation d'un
 * événement et doit confirmer son adresse email pour créer son compte.
 * Regroupe le token de confirmation envoyé par email et l'événement complet (coordonnées, données,
 * rubriques et demandes initiées). Créée à l'envoi de l'événement, consommée et supprimée à la
 * création du compte.
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
