package net.franzka.sgilt.core.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class SomethingDto {
    Date date;
    String message;
}
