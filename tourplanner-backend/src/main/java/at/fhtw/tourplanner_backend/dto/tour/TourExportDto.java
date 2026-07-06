package at.fhtw.tourplanner_backend.dto.tour;

import at.fhtw.tourplanner_backend.dto.tourlog.TourLogExportDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourExportDto {

    private String name;
    private String description;
    private String from;
    private String to;
    private String transportType;
    private Double distance;
    private Integer estimatedTime;
    private String routeGeometry;
    private List<TourLogExportDto> logs;
}
