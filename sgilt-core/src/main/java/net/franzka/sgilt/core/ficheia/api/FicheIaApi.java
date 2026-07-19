package net.franzka.sgilt.core.ficheia.api;

import net.franzka.sgilt.core.ficheia.dto.FicheIaApplyRequest;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationRequest;
import net.franzka.sgilt.core.ficheia.dto.FicheIaGenerationResultDto;
import net.franzka.sgilt.core.prestataire.dto.PrestataireDetailDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/v1/prestataires/ma-fiche/generation-ia")
public interface FicheIaApi {

    @PostMapping
    ResponseEntity<FicheIaGenerationResultDto> generate(@RequestBody FicheIaGenerationRequest request);

    @PatchMapping
    ResponseEntity<PrestataireDetailDto> apply(@RequestBody FicheIaApplyRequest request);
}
