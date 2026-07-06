package at.fhtw.tourplanner_backend.controllers;

import at.fhtw.tourplanner_backend.dto.tour.TourExportResponseDto;
import at.fhtw.tourplanner_backend.dto.tour.TourImportRequestDto;
import at.fhtw.tourplanner_backend.dto.tour.TourResponseDto;
import at.fhtw.tourplanner_backend.services.TourImportExportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourImportExportController {

    private final TourImportExportService tourImportExportService;

    @GetMapping("/export")
    public ResponseEntity<TourExportResponseDto> exportTours() {
        return ResponseEntity.ok(tourImportExportService.exportTours());
    }

    @PostMapping("/import")
    public ResponseEntity<List<TourResponseDto>> importTours(@Valid @RequestBody TourImportRequestDto request) {
        return ResponseEntity.ok(tourImportExportService.importTours(request));
    }
}
