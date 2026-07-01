package at.fhtw.tourplanner_backend.dto.route;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RouteRequestDto {

    @NotBlank
    private String from;

    @NotBlank
    private String to;

    @NotBlank
    private String transportType;
}