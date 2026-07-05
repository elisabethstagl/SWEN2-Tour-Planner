package at.fhtw.tourplanner_backend.mapper;

import at.fhtw.tourplanner_backend.dto.tour.TourRequestDto;
import at.fhtw.tourplanner_backend.dto.tour.TourResponseDto;
import at.fhtw.tourplanner_backend.entities.Tour;
import at.fhtw.tourplanner_backend.entities.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TourMapperTest {

    @Test
    void toEntity_mapsAllFieldsFromDtoAndUser() {
        User user = new User();
        user.setId(1L);
        TourRequestDto dto = new TourRequestDto(
                "Vienna Woods", "A nice walk", "Vienna", "Baden",
                "hike", 15.5, 180, "http://maps.example/1"
        );

        Tour tour = TourMapper.toEntity(dto, user);

        assertThat(tour.getUser()).isEqualTo(user);
        assertThat(tour.getName()).isEqualTo("Vienna Woods");
        assertThat(tour.getDescription()).isEqualTo("A nice walk");
        assertThat(tour.getFromLocation()).isEqualTo("Vienna");
        assertThat(tour.getToLocation()).isEqualTo("Baden");
        assertThat(tour.getTransportType()).isEqualTo("hike");
        assertThat(tour.getDistanceKm()).isEqualTo(15.5);
        assertThat(tour.getEstimatedTime()).isEqualTo(180);
        assertThat(tour.getMapImagePath()).isEqualTo("http://maps.example/1");
    }

    @Test
    void updateEntity_overwritesExistingTourFieldsInPlace() {
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setName("Old Name");
        User user = new User();

        TourRequestDto dto = new TourRequestDto(
                "New Name", "New description", "A", "B",
                "bike", 5.0, 30, null
        );

        TourMapper.updateEntity(tour, dto, user);

        assertThat(tour.getId()).isEqualTo(1L); // id is untouched by update
        assertThat(tour.getName()).isEqualTo("New Name");
        assertThat(tour.getUser()).isEqualTo(user);
    }

    @Test
    void toResponseDto_includesComputedValues() {
        Tour tour = new Tour();
        tour.setId(1L);
        User user = new User();
        user.setId(2L);
        tour.setUser(user);
        tour.setName("Vienna Woods");
        tour.setPopularity(5);
        tour.setChildFriendliness(87.5);

        TourResponseDto dto = TourMapper.toResponseDto(tour);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUserId()).isEqualTo(2L);
        assertThat(dto.getPopularity()).isEqualTo(5);
        assertThat(dto.getChildFriendliness()).isEqualTo(87.5);
    }
}
