package net.franzka.sgilt.sgilt_mailer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.franzka.sgilt.sgilt_mailer.template.MailType;

import java.util.Map;

@Data
public class MailRequest {
    @NotBlank(message = "To address is mandatory")
    private String to;

    @NotNull(message = "Mail type is mandatory")
    private MailType mailType;

    private Map<String, Object> context;

    private String locale;
}
