package net.franzka.sgilt.core.survey.api;

import net.franzka.sgilt.core.survey.domain.Survey;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/v1/surveys")
public interface SurveyApi {

    @GetMapping("/{slug}")
    ResponseEntity<Survey> getConfig(@PathVariable String slug);
}
