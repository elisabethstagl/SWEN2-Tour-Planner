package at.fhtw.tourplanner_backend.controllers;

import at.fhtw.tourplanner_backend.dto.tour.TourRequestDto;
import at.fhtw.tourplanner_backend.dto.tour.TourResponseDto;
import at.fhtw.tourplanner_backend.services.TourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    @GetMapping
    public ResponseEntity<List<TourResponseDto>> getAllTours() {
        return ResponseEntity.ok(tourService.getAllTours());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourResponseDto> getTourById(@PathVariable Long id) {
        return ResponseEntity.ok(tourService.getTourById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TourResponseDto>> searchTours(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(tourService.searchTours(query));
    }

    @PostMapping
    public ResponseEntity<TourResponseDto> createTour(@Valid @RequestBody TourRequestDto tour) {
        return ResponseEntity.ok(tourService.createTour(tour));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourResponseDto> updateTour(
            @PathVariable Long id,
            @Valid @RequestBody TourRequestDto tour) {

        return ResponseEntity.ok(tourService.updateTour(id, tour));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourService.deleteTour(id);
        return ResponseEntity.noContent().build();
    }
}