package at.fhtw.tourplanner_backend.dto.tour;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// top-level shape of a tour export file - wraps the list so the same
// structure ({"tours": [...]}) can be sent straight back in on import.

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourExportResponseDto {

    private List<TourExportDto> tours;
}
