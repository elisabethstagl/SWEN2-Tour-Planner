package at.fhtw.tourplanner_backend.dto.tour;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// top-level shape expected by POST /api/tours/import - matches TourExportResponseDto
// ({"tours": [...]}) so an exported file can be uploaded again unchanged.

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourImportRequestDto {

    @NotEmpty
    @Valid
    private List<TourImportDto> tours = new ArrayList<>();
}
