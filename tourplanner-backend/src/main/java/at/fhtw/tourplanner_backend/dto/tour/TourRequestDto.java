package at.fhtw.tourplanner_backend.dto.tour;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// request dto - what frontend sends to backend

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourRequestDto {

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Size(min = 2, max = 1000)
    private String description;

    @NotBlank
    private String from;

    @NotBlank
    private String to;

    @NotBlank
    private String transportType;

    @NotNull
    @Positive
    private Double distance;

    @NotNull
    @Positive
    private Integer estimatedTime;

    private String mapUrl;
}