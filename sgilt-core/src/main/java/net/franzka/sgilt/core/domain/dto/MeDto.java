package net.franzka.sgilt.core.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeDto {
    String name;
    String email;
}
