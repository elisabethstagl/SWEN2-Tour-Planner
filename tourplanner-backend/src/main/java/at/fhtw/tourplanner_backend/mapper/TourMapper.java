package at.fhtw.tourplanner_backend.mapper;

import at.fhtw.tourplanner_backend.dto.tour.TourRequestDto;
import at.fhtw.tourplanner_backend.dto.tour.TourResponseDto;
import at.fhtw.tourplanner_backend.entities.Tour;
import at.fhtw.tourplanner_backend.entities.User;

public class TourMapper {

    public static Tour toEntity(TourRequestDto dto, User user) {
        Tour tour = new Tour();

        tour.setUser(user);
        tour.setName(dto.getName());
        tour.setDescription(dto.getDescription());
        tour.setFromLocation(dto.getFrom());
        tour.setToLocation(dto.getTo());
        tour.setTransportType(dto.getTransportType());
        tour.setDistanceKm(dto.getDistance());
        tour.setEstimatedTime(dto.getEstimatedTime());
        tour.setMapImagePath(dto.getMapUrl());

        return tour;
    }

    public static void updateEntity(Tour tour, TourRequestDto dto, User user) {
        tour.setUser(user);
        tour.setName(dto.getName());
        tour.setDescription(dto.getDescription());
        tour.setFromLocation(dto.getFrom());
        tour.setToLocation(dto.getTo());
        tour.setTransportType(dto.getTransportType());
        tour.setDistanceKm(dto.getDistance());
        tour.setEstimatedTime(dto.getEstimatedTime());
        tour.setMapImagePath(dto.getMapUrl());
    }

    public static TourResponseDto toResponseDto(Tour tour) {
        return new TourResponseDto(
                tour.getId(),
                tour.getUser() != null ? tour.getUser().getId() : null,
                tour.getName(),
                tour.getDescription(),
                tour.getFromLocation(),
                tour.getToLocation(),
                tour.getTransportType(),
                tour.getDistanceKm(),
                tour.getEstimatedTime(),
                tour.getMapImagePath()
        );
    }
}