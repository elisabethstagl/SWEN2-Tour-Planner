package at.fhtw.tourplanner_backend.dto.route;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeocodeSuggestionDto {

    private String label;
    private Double latitude;
    private Double longitude;
}