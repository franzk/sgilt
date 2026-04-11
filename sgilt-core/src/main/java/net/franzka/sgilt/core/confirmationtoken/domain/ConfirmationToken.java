package net.franzka.sgilt.core.confirmationtoken.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.franzka.sgilt.core.reservation.domain.Reservation;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "confirmation_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String jti;
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private boolean used;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public static ConfirmationToken create(String jti, String email, Reservation reservation, int expirationHours) {
        ConfirmationToken t = new ConfirmationToken();
        t.jti = jti;
        t.email = email;
        t.reservation = reservation;
        t.used = false;
        t.expiresAt = LocalDateTime.now().plusHours(expirationHours);
        t.createdAt = LocalDateTime.now();
        return t;
    }
}
