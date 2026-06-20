package at.fhtw.tourplanner_backend.controllers;

import at.fhtw.tourplanner_backend.entities.Tour;
import at.fhtw.tourplanner_backend.entities.TourLog;
import at.fhtw.tourplanner_backend.services.TourLogService;
import at.fhtw.tourplanner_backend.services.TourService;
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
    public ResponseEntity<List<TourLog>> getAllTourLogs() {
        return ResponseEntity.ok(tourLogService.getAllTourLogs());
    }

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<TourLog>> getTourLogsByTourId(@PathVariable Long tourId) {
        return ResponseEntity.ok(tourLogService.getTourLogsByTourId(tourId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourLog> getTourLogById(@PathVariable Long id) {
        return ResponseEntity.ok(tourLogService.getTourLogById(id));
    }


    @PostMapping
    public ResponseEntity<TourLog> createTourLog(@RequestBody TourLog tourLog) {
        return ResponseEntity.ok(tourLogService.createTourLog(tourLog));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourLog> updateTourLog(@PathVariable Long id, @RequestBody TourLog tourLog) {
        return ResponseEntity.ok(tourLogService.updateTourLog(id, tourLog));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourLogService.deleteTourLog(id);
        return ResponseEntity.noContent().build();
    }
}