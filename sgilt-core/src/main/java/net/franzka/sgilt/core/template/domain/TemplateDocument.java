package net.franzka.sgilt.core.template.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

/**
 * Entité JPA du document des templates d'événement : un seul document (table à une ligne), qui
 * contient le template de chaque type d'événement.
 */
@Entity
@Table(name = "templates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDocument {

    /** Identifiant de l'unique ligne de la table. */
    public static final short SINGLE_ID = 1;

    @Id
    @Column(updatable = false, nullable = false)
    private Short id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<Template> content;
}
