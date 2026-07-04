package at.fhtw.tourplanner_backend.mapper;

import at.fhtw.tourplanner_backend.dto.tourlog.TourLogRequestDto;
import at.fhtw.tourplanner_backend.dto.tourlog.TourLogResponseDto;
import at.fhtw.tourplanner_backend.entities.Tour;
import at.fhtw.tourplanner_backend.entities.TourLog;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TourLogMapperTest {

    @Test
    void toEntity_mapsAllFieldsFromDtoAndTour() {
        Tour tour = new Tour();
        tour.setId(10L);
        Instant now = Instant.parse("2026-01-01T10:00:00Z");
        TourLogRequestDto dto = new TourLogRequestDto(10L, now, "Great hike", 3, 12.5, 90, 4);

        TourLog log = TourLogMapper.toEntity(dto, tour);

        assertThat(log.getTour()).isEqualTo(tour);
        assertThat(log.getLogDatetime()).isEqualTo(now);
        assertThat(log.getComment()).isEqualTo("Great hike");
        assertThat(log.getDifficulty()).isEqualTo(3);
        assertThat(log.getTotalDistance()).isEqualTo(12.5);
        assertThat(log.getTotalTime()).isEqualTo(90);
        assertThat(log.getRating()).isEqualTo(4);
    }

    @Test
    void updateEntity_overwritesExistingLogFieldsInPlace() {
        Tour originalTour = new Tour();
        originalTour.setId(10L);
        Tour newTour = new Tour();
        newTour.setId(20L);

        TourLog log = new TourLog();
        log.setId(1L);
        log.setTour(originalTour);
        log.setComment("Old comment");

        TourLogRequestDto dto = new TourLogRequestDto(
                20L, Instant.parse("2026-02-01T08:00:00Z"), "Updated comment", 4, 20.0, 120, 5
        );

        TourLogMapper.updateEntity(log, dto, newTour);

        assertThat(log.getId()).isEqualTo(1L); // id is untouched by update
        assertThat(log.getTour()).isEqualTo(newTour);
        assertThat(log.getComment()).isEqualTo("Updated comment");
        assertThat(log.getRating()).isEqualTo(5);
    }

    @Test
    void toResponseDto_mapsAllFieldsIncludingParentTourId() {
        Tour tour = new Tour();
        tour.setId(10L);
        TourLog log = new TourLog();
        log.setId(1L);
        log.setTour(tour);
        log.setLogDatetime(Instant.parse("2026-01-01T10:00:00Z"));
        log.setComment("Great hike");
        log.setDifficulty(3);
        log.setTotalDistance(12.5);
        log.setTotalTime(90);
        log.setRating(4);
        log.setCreatedAt(Instant.parse("2026-01-01T09:00:00Z"));

        TourLogResponseDto dto = TourLogMapper.toResponseDto(log);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTourId()).isEqualTo(10L);
        assertThat(dto.getComment()).isEqualTo("Great hike");
        assertThat(dto.getDifficulty()).isEqualTo(3);
        assertThat(dto.getRating()).isEqualTo(4);
    }
}
