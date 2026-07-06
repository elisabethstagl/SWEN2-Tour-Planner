package at.fhtw.tourplanner_backend.controllers;

import at.fhtw.tourplanner_backend.dto.tourlog.TourLogRequestDto;
import at.fhtw.tourplanner_backend.dto.tourlog.TourLogResponseDto;
import at.fhtw.tourplanner_backend.services.TourLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tourLogs")
@RequiredArgsConstructor
public class TourLogController {

    private final TourLogService tourLogService;

    @GetMapping
    public ResponseEntity<List<TourLogResponseDto>> getAllTourLogs() {
        return ResponseEntity.ok(tourLogService.getAllTourLogs());
    }

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<TourLogResponseDto>> getTourLogsByTourId(@PathVariable Long tourId) {
        return ResponseEntity.ok(tourLogService.getTourLogsByTourId(tourId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourLogResponseDto> getTourLogById(@PathVariable Long id) {
        return ResponseEntity.ok(tourLogService.getTourLogById(id));
    }

    @PostMapping
    public ResponseEntity<TourLogResponseDto> createTourLog(@Valid @RequestBody TourLogRequestDto tourLog) {
        return ResponseEntity.ok(tourLogService.createTourLog(tourLog));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourLogResponseDto> updateTourLog(
            @PathVariable Long id,
            @Valid @RequestBody TourLogRequestDto tourLog) {

        return ResponseEntity.ok(tourLogService.updateTourLog(id, tourLog));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTourLog(@PathVariable Long id) {
        tourLogService.deleteTourLog(id);
        return ResponseEntity.noContent().build();
    }
}