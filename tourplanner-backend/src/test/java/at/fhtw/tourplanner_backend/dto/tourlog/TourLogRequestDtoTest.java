package at.fhtw.tourplanner_backend.dto.tourlog;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the Jakarta Bean Validation constraints on TourLogRequestDto directly.
 */
class TourLogRequestDtoTest {

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
        TourLogRequestDto dto = validDto();

        Set<ConstraintViolation<TourLogRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_fails_whenDifficultyIsBelowMinimum() {
        TourLogRequestDto dto = validDto();
        dto.setDifficulty(0);

        Set<ConstraintViolation<TourLogRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("difficulty");
    }

    @Test
    void validate_fails_whenDifficultyIsAboveMaximum() {
        TourLogRequestDto dto = validDto();
        dto.setDifficulty(6);

        Set<ConstraintViolation<TourLogRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("difficulty");
    }

    @Test
    void validate_fails_whenRatingIsBelowMinimum() {
        TourLogRequestDto dto = validDto();
        dto.setRating(0);

        Set<ConstraintViolation<TourLogRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("rating");
    }

    @Test
    void validate_fails_whenRatingIsAboveMaximum() {
        TourLogRequestDto dto = validDto();
        dto.setRating(6);

        Set<ConstraintViolation<TourLogRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("rating");
    }

    @Test
    void validate_fails_whenTourIdIsMissing() {
        TourLogRequestDto dto = validDto();
        dto.setTourId(null);

        Set<ConstraintViolation<TourLogRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("tourId");
    }

    @Test
    void validate_fails_whenTotalDistanceIsNotPositive() {
        TourLogRequestDto dto = validDto();
        dto.setTotalDistance(0.0);

        Set<ConstraintViolation<TourLogRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("totalDistance");
    }

    @Test
    void validate_fails_whenTotalTimeIsNotPositive() {
        TourLogRequestDto dto = validDto();
        dto.setTotalTime(0);

        Set<ConstraintViolation<TourLogRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("totalTime");
    }

    private TourLogRequestDto validDto() {
        return new TourLogRequestDto(
                1L,
                Instant.parse("2026-01-01T10:00:00Z"),
                "Great hike",
                3,
                12.5,
                90,
                4
        );
    }
}
