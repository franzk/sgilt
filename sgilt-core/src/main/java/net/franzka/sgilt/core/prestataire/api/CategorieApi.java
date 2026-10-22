package net.franzka.sgilt.core.prestataire.api;

import jakarta.validation.Valid;
import net.franzka.sgilt.core.prestataire.domain.MoveDirection;
import net.franzka.sgilt.core.prestataire.dto.CategorieAdminDto;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieAdminDto;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieCreateRequest;
import net.franzka.sgilt.core.prestataire.dto.SousCategorieUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// Administration des catégories et sous-catégories. La lecture publique est servie par PrestataireApi.
@RequestMapping("api/v1/admin")
public interface CategorieApi {

    @GetMapping("/categories")
    ResponseEntity<List<CategorieAdminDto>> listCategoriesAdmin();

    @PostMapping("/sous-categories")
    ResponseEntity<SousCategorieAdminDto> createSousCategorie(@RequestBody @Valid SousCategorieCreateRequest request);

    @PatchMapping("/sous-categories/{key}")
    ResponseEntity<SousCategorieAdminDto> updateSousCategorie(
            @PathVariable String key, @RequestBody @Valid SousCategorieUpdateRequest request);

    @PostMapping("/sous-categories/{key}/move")
    ResponseEntity<SousCategorieAdminDto> moveSousCategorie(@PathVariable String key, @RequestParam MoveDirection direction);

    @DeleteMapping("/sous-categories/{key}")
    ResponseEntity<Void> deleteSousCategorie(@PathVariable String key);
}
