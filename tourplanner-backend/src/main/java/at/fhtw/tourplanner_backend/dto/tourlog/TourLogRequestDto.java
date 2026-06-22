package at.fhtw.tourplanner_backend.dto.tourlog;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourLogRequestDto {

    @NotNull
    private Long tourId;

    @NotNull
    private Instant logDatetime;
    private String comment;

    @NotNull
    private Integer difficulty;

    @NotNull
    @Positive
    private Double totalDistance;

    @NotNull
    @Positive
    private Integer totalTime;

    @NotNull
    private Integer rating;
}