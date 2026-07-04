package at.fhtw.tourplanner_backend.dto.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the Jakarta Bean Validation constraints on UserRequestDto directly.
 */
class UserRequestDtoTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        factory.close();
    }

    @Test
    void validate_passes_whenAllFieldsAreValid() {
        UserRequestDto dto = new UserRequestDto("bob", "bob@example.com", "plainPassword1");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_fails_whenEmailExceedsMaxLength() {
        String tooLongEmail = "b".repeat(95) + "@example.com"; // > 100 chars, DB column is VARCHAR(100)
        UserRequestDto dto = new UserRequestDto("bob", tooLongEmail, "plainPassword1");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("email");
    }

    @Test
    void validate_fails_whenUsernameTooShort() {
        UserRequestDto dto = new UserRequestDto("ab", "bob@example.com", "plainPassword1");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("username");
    }

    @Test
    void validate_fails_whenPasswordTooShort() {
        UserRequestDto dto = new UserRequestDto("bob", "bob@example.com", "short");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("password");
    }
}
