package at.fhtw.tourplanner_backend.dto.tour;

import at.fhtw.tourplanner_backend.dto.tourlog.TourLogImportDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TourImportDto {

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

    private String routeGeometry;

    @Valid
    private List<TourLogImportDto> logs = new ArrayList<>();
}
