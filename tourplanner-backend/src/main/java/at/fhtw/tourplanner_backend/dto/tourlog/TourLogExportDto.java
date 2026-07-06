package at.fhtw.tourplanner_backend.dto.tourlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

// export dto - what backend writes into a tour export file, nested under a TourExportDto

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourLogExportDto {

    private Instant logDatetime;
    private String comment;
    private Integer difficulty;
    private Double totalDistance;
    private Integer totalTime;
    private Integer rating;
}
