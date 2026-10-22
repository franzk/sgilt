package net.franzka.sgilt.core.template.repository;

import net.franzka.sgilt.core.template.domain.TemplateDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour l'entité {@link TemplateDocument}.
 */
@Repository
public interface TemplateDocumentRepository extends JpaRepository<TemplateDocument, Short> {
}
