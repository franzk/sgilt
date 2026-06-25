package net.franzka.sgilt.core.prestataire.api;

import net.franzka.sgilt.core.prestataire.dto.PrestataireDetailDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireSearchResponseDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("api/v1/prestataires")
public interface PrestataireApi {

    @GetMapping
    ResponseEntity<PrestataireSearchResponseDto> search(
            @RequestParam(required = false) String categoryKey,
            @RequestParam(required = false) List<String> subcatKey
    );

    @GetMapping("/{slug}")
    ResponseEntity<PrestataireDetailDto> getBySlug(@PathVariable String slug);

    @GetMapping("/engagements")
    ResponseEntity<List<String>> getEngagementKeys();

    @PatchMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody PrestataireUpdateDto dto);
}
