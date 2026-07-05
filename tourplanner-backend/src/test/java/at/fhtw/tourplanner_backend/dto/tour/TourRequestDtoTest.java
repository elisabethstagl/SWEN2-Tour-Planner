package at.fhtw.tourplanner_backend.dto.tour;

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
 * Verifies the Jakarta Bean Validation constraints on TourRequestDto directly.
 */
class TourRequestDtoTest {

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
        TourRequestDto dto = validDto();

        Set<ConstraintViolation<TourRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_fails_whenDistanceIsNotPositive() {
        TourRequestDto dto = validDto();
        dto.setDistance(0.0);

        Set<ConstraintViolation<TourRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("distance");
    }

    private TourRequestDto validDto() {
        return new TourRequestDto(
                "Vienna Woods",
                "A nice walk",
                "Vienna",
                "Baden",
                "bike",
                15.5,
                180,
                "http://maps.example/1"
        );
    }
}
