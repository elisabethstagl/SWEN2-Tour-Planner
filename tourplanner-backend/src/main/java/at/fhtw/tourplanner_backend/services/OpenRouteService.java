package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.dto.route.RouteRequestDto;
import at.fhtw.tourplanner_backend.dto.route.RouteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenRouteService {

    private final WebClient openRouteServiceWebClient;

    @Value("${openrouteservice.api-key}")
    private String apiKey;

    public RouteResponseDto calculateRoute(RouteRequestDto request) {
        List<Double> start = geocode(request.getFrom());
        List<Double> end = geocode(request.getTo());

        String profile = mapTransportType(request.getTransportType());

        Map<String, Object> body = Map.of(
                "coordinates", List.of(start, end)
        );


        Map response = openRouteServiceWebClient.post()
                .uri("/v2/directions/{profile}/geojson", profile)
                .header("Authorization", apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Map feature = (Map) ((List<?>) response.get("features")).get(0);
        Map properties = (Map) feature.get("properties");
        Map summary = (Map) properties.get("summary");
        Map geometry = (Map) feature.get("geometry");

        Double distanceKm = ((Number) summary.get("distance")).doubleValue() / 1000;
        Integer durationMinutes = (int) Math.round(
                ((Number) summary.get("duration")).doubleValue() / 60
        );

        List<List<Double>> coordinates = (List<List<Double>>) geometry.get("coordinates");

        return new RouteResponseDto(distanceKm, durationMinutes, coordinates);
    }

    private List<Double> geocode(String address) {
        Map response = openRouteServiceWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/geocode/search")
                        .queryParam("text", address)
                        .queryParam("size", 1)
                        .build()
                )
                .header("Authorization", apiKey)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Map feature = (Map) ((List<?>) response.get("features")).get(0);
        Map geometry = (Map) feature.get("geometry");

        return (List<Double>) geometry.get("coordinates");
    }

    private String mapTransportType(String transportType) {
        return switch (transportType.toLowerCase()) {
            case "bike" -> "cycling-regular";
            case "hike" -> "foot-hiking";
            case "walk" -> "foot-walking";
            case "car" -> "driving-car";
            case "wheelchair" -> "wheelchair";
            default -> "driving-car";
        };
    }
}