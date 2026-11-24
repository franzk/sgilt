package net.franzka.sgilt.core.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@DiscriminatorValue("DOCUMENT")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Document extends ReservationFeed {

    @Column(nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    private String mimeType;

    /**
     * Identifiant de l'envoi, généré par le front et commun aux documents déposés ensemble : ils
     * forment une seule carte du feed et une seule notification. Null pour les documents antérieurs
     * à l'envoi multiple (chacun reste un envoi d'un seul fichier).
     */
    @Column(name = "upload_id")
    private UUID uploadId;
}
