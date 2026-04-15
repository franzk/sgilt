package net.franzka.sgilt.core.onboarding.api;

import net.franzka.sgilt.core.onboarding.dto.SetPasswordTokenDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/confirmation")
public interface ConfirmationApi {

    @GetMapping
    ResponseEntity<SetPasswordTokenDto> verifyToken(@RequestParam String token);

}
