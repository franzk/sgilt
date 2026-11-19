package net.franzka.sgilt.core.admin.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CreateClientInvitationRequestTest {

    @Test
    void givenMixedCaseEmailWithSpaces_whenCreateRequest_thenEmailIsTrimmedAndLowercased() {
        CreateClientInvitationRequest request = new CreateClientInvitationRequest(
                "  Julie.Martin@Example.COM ", "Julie", "Martin", "mariage", LocalDate.of(2027, 6, 12), UUID.randomUUID());

        assertThat(request.email()).isEqualTo("julie.martin@example.com");
    }

    @Test
    void givenNullEmail_whenCreateRequest_thenEmailStaysNullForValidation() {
        CreateClientInvitationRequest request = new CreateClientInvitationRequest(
                null, "Julie", "Martin", "mariage", LocalDate.of(2027, 6, 12), UUID.randomUUID());

        assertThat(request.email()).isNull();
    }
}
