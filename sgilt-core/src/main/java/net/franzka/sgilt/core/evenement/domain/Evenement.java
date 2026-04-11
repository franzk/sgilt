package net.franzka.sgilt.core.evenement.domain;

import jakarta.persistence.*;
import lombok.*;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entité JPA représentant un événement.
 */
@Entity
@Table(name = "evenements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;
    private String lastName;
    private String email;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "evenement_status")
    private EvenementStatus status;

    @Builder.Default
    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    private LocalDateTime createdAt;
}
