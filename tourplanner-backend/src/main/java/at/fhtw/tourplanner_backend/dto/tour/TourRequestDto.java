package at.fhtw.tourplanner_backend.dto.tour;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// request dto - what frontend sends to backend

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourRequestDto {

    private Long userId;

    private String name;
    private String description;

    private String from;
    private String to;

    private String transportType;

    private Double distance;

    private Integer estimatedTime;

    private String mapUrl;
}