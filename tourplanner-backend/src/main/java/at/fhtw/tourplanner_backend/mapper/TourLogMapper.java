package at.fhtw.tourplanner_backend.mapper;

import at.fhtw.tourplanner_backend.dto.tourlog.TourLogResponseDto;
import at.fhtw.tourplanner_backend.dto.tourlog.TourLogRequestDto;
import at.fhtw.tourplanner_backend.entities.Tour;
import at.fhtw.tourplanner_backend.entities.TourLog;

public class TourLogMapper {

    public static TourLog toEntity(TourLogRequestDto dto, Tour tour) {
        TourLog log = new TourLog();
        applyFields(log, dto, tour);
        return log;
    }

    public static void updateEntity(TourLog log, TourLogRequestDto dto, Tour tour) {
        applyFields(log, dto, tour);
    }

    private static void applyFields(TourLog log, TourLogRequestDto dto, Tour tour) {
        log.setTour(tour);
        log.setLogDatetime(dto.getLogDatetime());
        log.setComment(dto.getComment());
        log.setDifficulty(dto.getDifficulty());
        log.setTotalDistance(dto.getTotalDistance());
        log.setTotalTime(dto.getTotalTime());
        log.setRating(dto.getRating());
    }

    public static TourLogResponseDto toResponseDto(TourLog log) {
        return new TourLogResponseDto(
            log.getId(),
            log.getTour().getId(),
            log.getLogDatetime(),
            log.getComment(),
            log.getDifficulty(),
            log.getTotalDistance(),
            log.getTotalTime(),
            log.getRating(),
            log.getCreatedAt()
        );
    }
}