package net.franzka.sgilt.core.prestataire.api;

import net.franzka.sgilt.core.prestataire.dto.PrestataireDetailDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireSearchResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("api/v1/prestataires")
public interface PrestataireApi {

    @GetMapping
    ResponseEntity<PrestataireSearchResponseDto> search(
            @RequestParam(required = false) String categoryKey,
            @RequestParam(required = false) List<String> subcatKey
    );

    @GetMapping("/{slug}")
    ResponseEntity<PrestataireDetailDto> getBySlug(@PathVariable String slug);
}
