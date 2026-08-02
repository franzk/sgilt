package net.franzka.sgilt.core.utilisateur.api;

import net.franzka.sgilt.core.utilisateur.dto.UtilisateurEditDto;
import net.franzka.sgilt.core.utilisateur.dto.UtilisateurProfileDto;
import net.franzka.sgilt.core.utilisateur.dto.UtilisateurUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/v1/users")
public interface UtilisateurApi {

    @GetMapping("/me")
    ResponseEntity<UtilisateurProfileDto> getMe();

    @GetMapping("/me/edit")
    ResponseEntity<UtilisateurEditDto> getEditProfile();

    @PatchMapping("/me/edit")
    ResponseEntity<Void> updateMe(@RequestBody UtilisateurUpdateDto dto);
}
