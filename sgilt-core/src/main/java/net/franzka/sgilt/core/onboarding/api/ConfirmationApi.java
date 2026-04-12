package net.franzka.sgilt.core.onboarding.api;

import net.franzka.sgilt.core.onboarding.dto.SetPasswordTokenDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/confirmation")
public interface ConfirmationApi {

    @GetMapping("/{token}")
    ResponseEntity<SetPasswordTokenDto> verifyToken(@PathVariable String token);

}
