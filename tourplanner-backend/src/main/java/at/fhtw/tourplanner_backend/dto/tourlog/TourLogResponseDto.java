package at.fhtw.tourplanner_backend.dto.tourlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourLogResponseDto {

    private Long id;
    private Long tourId;
    private Instant logDatetime;
    private String comment;
    private Integer difficulty;
    private Double totalDistance;
    private Integer totalTime;
    private Integer rating;
    private Instant createdAt;
}