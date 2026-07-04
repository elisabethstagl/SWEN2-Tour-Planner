package at.fhtw.tourplanner_backend.controllers;

import at.fhtw.tourplanner_backend.dto.route.GeocodeSuggestionDto;
import at.fhtw.tourplanner_backend.dto.route.RouteRequestDto;
import at.fhtw.tourplanner_backend.dto.route.RouteResponseDto;
import at.fhtw.tourplanner_backend.services.OpenRouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final OpenRouteService openRouteService;

    @PostMapping("/calculate")
    public ResponseEntity<RouteResponseDto> calculateRoute(
            @Valid @RequestBody RouteRequestDto request
    ) {
        return ResponseEntity.ok(openRouteService.calculateRoute(request));
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<List<GeocodeSuggestionDto>> autocomplete(
            @RequestParam String query
    ) {
        if (query == null || query.trim().length() < 3) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(openRouteService.autocomplete(query.trim()));
    }
}