package net.franzka.sgilt.core.template.api;

import net.franzka.sgilt.core.evenement.dto.RubriqueDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("api/v1/templates")
public interface TemplateApi {

    @GetMapping("/{eventType}/rubriques")
    ResponseEntity<List<RubriqueDto>> getNewEventRubriques(@PathVariable String eventType);
}
