package at.fhtw.tourplanner_backend.dto.tourlog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TourLogImportDto {

    @NotNull
    private Instant logDatetime;

    private String comment;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer difficulty;

    @NotNull
    @Positive
    private Double totalDistance;

    @NotNull
    @Positive
    private Integer totalTime;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;
}
