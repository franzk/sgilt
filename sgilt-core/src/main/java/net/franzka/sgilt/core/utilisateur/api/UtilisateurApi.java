package net.franzka.sgilt.core.utilisateur.api;

import net.franzka.sgilt.core.utilisateur.dto.UtilisateurProfileDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/v1/users")
public interface UtilisateurApi {

    @GetMapping("/me")
    ResponseEntity<UtilisateurProfileDto> getMe();
}
