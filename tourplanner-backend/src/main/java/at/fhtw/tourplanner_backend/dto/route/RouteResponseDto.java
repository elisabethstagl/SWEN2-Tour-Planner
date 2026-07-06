package at.fhtw.tourplanner_backend.dto.route;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RouteResponseDto {

    private Double distanceKm;
    private Integer durationMinutes;
    private List<List<Double>> coordinates;
}