package net.franzka.sgilt.core.onboarding.domain;

import jakarta.persistence.*;
import lombok.*;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant un token de confirmation envoyé par email lors de l'onboarding.
 * Il persiste en base la relation entre le token de confirmation et la réservation en draft créée lors de la demande initiale.
 * Une fois consommé, le champ {@code used} passe à {@code true}.
 * Le token est supprimé en fin de process après création du compte Keycloak.
 */
@Entity
@Table(name = "confirmation_tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String payload;
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "confirmation_token_state")
    private ConfirmationTokenState state;

    private LocalDateTime expiresAt;
    private LocalDateTime confirmationPeriodExpiresAt;
    private LocalDateTime createdAt;
}
