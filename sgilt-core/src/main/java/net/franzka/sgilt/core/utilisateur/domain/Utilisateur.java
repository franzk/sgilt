package net.franzka.sgilt.core.utilisateur.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant un utilisateur client de la plateforme Sgilt.
 */
@Entity
@Table(name = "utilisateurs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String avatarUrl;
    private LocalDateTime createdAt;
}
