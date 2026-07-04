package at.fhtw.tourplanner_backend.controllers;

import at.fhtw.tourplanner_backend.dto.route.RouteRequestDto;
import at.fhtw.tourplanner_backend.dto.route.RouteResponseDto;
import at.fhtw.tourplanner_backend.services.OpenRouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}