package at.fhtw.tourplanner_backend.mapper;

import at.fhtw.tourplanner_backend.dto.tour.TourRequestDto;
import at.fhtw.tourplanner_backend.dto.tour.TourResponseDto;
import at.fhtw.tourplanner_backend.entities.Tour;
import at.fhtw.tourplanner_backend.entities.User;

public class TourMapper {

    public static Tour toEntity(TourRequestDto dto, User user) {
        Tour tour = new Tour();
        applyFields(tour, dto, user);
        return tour;
    }

    public static void updateEntity(Tour tour, TourRequestDto dto, User user) {
        applyFields(tour, dto, user);
    }

    private static void applyFields(Tour tour, TourRequestDto dto, User user) {
        tour.setUser(user);
        tour.setName(dto.getName());
        tour.setDescription(dto.getDescription());
        tour.setFromLocation(dto.getFrom());
        tour.setToLocation(dto.getTo());
        tour.setTransportType(dto.getTransportType());
        tour.setDistanceKm(dto.getDistance());
        tour.setEstimatedTime(dto.getEstimatedTime());

        //only overwrites the stored route if something changed (to, from, transport type)
        if (dto.getRouteGeometry() != null) {
            tour.setRouteGeometry(dto.getRouteGeometry());
        }
    }

    public static TourResponseDto toResponseDto(Tour tour) {
        return new TourResponseDto(
            tour.getId(),
            tour.getUser().getId(),
            tour.getName(),
            tour.getDescription(),
            tour.getFromLocation(),
            tour.getToLocation(),
            tour.getTransportType(),
            tour.getDistanceKm(),
            tour.getEstimatedTime(),
            tour.getMapImagePath(),
            tour.getPopularity(),
            tour.getChildFriendliness()
        );
    }
}