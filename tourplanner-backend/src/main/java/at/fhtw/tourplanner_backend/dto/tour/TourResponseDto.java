package at.fhtw.tourplanner_backend.dto.tour;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourResponseDto {

    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String from;
    private String to;
    private String transportType;
    private Double distance;
    private Integer estimatedTime;
    private String routeGeometry;
    private Integer popularity;
    private Double childFriendliness;
}