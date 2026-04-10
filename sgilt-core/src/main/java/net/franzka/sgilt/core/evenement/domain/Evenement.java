package net.franzka.sgilt.core.evenement.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.franzka.sgilt.core.onboarding.api.dto.NewEvenementRequest;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "evenements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    private LocalDateTime createdAt;

    public static Evenement createDraft(NewEvenementRequest request) {
        Evenement e = new Evenement();
        e.firstName = request.firstname();
        e.lastName = request.lastname();
        e.email = request.email();
        e.status = EvenementStatus.DRAFT;
        e.createdAt = LocalDateTime.now();
        return e;
    }
}
