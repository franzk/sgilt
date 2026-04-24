package net.franzka.sgilt.core.evenement.domain;

import jakarta.persistence.*;
import lombok.*;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant un événement organisé par un utilisateur.
 */
@Entity
@Table(name = "evenements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate date;

    private String coverUrl;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "evenement_status", nullable = false)
    private EvenementStatus statut;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}