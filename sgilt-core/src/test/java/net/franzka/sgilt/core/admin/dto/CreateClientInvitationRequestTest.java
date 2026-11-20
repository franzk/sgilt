package net.franzka.sgilt.core.admin.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CreateClientInvitationRequestTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    // -------------------------------------------------------------------------
    // email
    // -------------------------------------------------------------------------

    @Nested
    class Email {

        @Test
        void givenMixedCaseEmailWithSpaces_whenCreateRequest_thenEmailIsTrimmedAndLowercased() {
            CreateClientInvitationRequest request = requestWith("  Julie.Martin@Example.COM ", null);

            assertThat(request.email()).isEqualTo("julie.martin@example.com");
        }

        @Test
        void givenNullEmail_whenCreateRequest_thenEmailStaysNullForValidation() {
            CreateClientInvitationRequest request = requestWith(null, null);

            assertThat(request.email()).isNull();
        }
    }

    // -------------------------------------------------------------------------
    // telephone
    // -------------------------------------------------------------------------

    @Nested
    class Telephone {

        @Test
        void givenNoTelephone_whenValidate_thenNoViolations() {
            assertThat(validate(null)).isEmpty();
        }

        @Test
        void givenSwissMobile_whenValidate_thenNoViolations() {
            assertThat(validate("+41790000000")).isEmpty();
        }

        @Test
        void givenGermanNumberWithSeparators_whenValidate_thenNoViolations() {
            assertThat(validate("+49 (0)30 1234567")).isEmpty();
        }

        @Test
        void givenTooFewDigits_whenValidate_thenReportsTelephoneViolation() {
            assertThat(validate("12 34"))
                    .extracting(violation -> violation.getPropertyPath().toString())
                    .containsExactly("telephone");
        }

        @Test
        void givenLetters_whenValidate_thenReportsTelephoneViolation() {
            assertThat(validate("06 12 AB 56 78"))
                    .extracting(violation -> violation.getPropertyPath().toString())
                    .containsExactly("telephone");
        }

        private Set<ConstraintViolation<CreateClientInvitationRequest>> validate(String telephone) {
            return validator.validate(requestWith("julie@example.com", telephone));
        }
    }

    private static CreateClientInvitationRequest requestWith(String email, String telephone) {
        return new CreateClientInvitationRequest(
                email, "Julie", "Martin", telephone, "mariage", LocalDate.of(2027, 6, 12), UUID.randomUUID());
    }
}
