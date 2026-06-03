package net.franzka.sgilt.core.pro.api;

import net.franzka.sgilt.core.pro.dto.ProMeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/v1/pro")
public interface ProApi {

    @GetMapping("/me")
    ResponseEntity<ProMeDto> getMe();
}
