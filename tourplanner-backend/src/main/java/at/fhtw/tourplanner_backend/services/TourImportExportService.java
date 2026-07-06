package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.dto.tour.TourExportDto;
import at.fhtw.tourplanner_backend.dto.tour.TourExportResponseDto;
import at.fhtw.tourplanner_backend.dto.tour.TourImportDto;
import at.fhtw.tourplanner_backend.dto.tour.TourImportRequestDto;
import at.fhtw.tourplanner_backend.dto.tour.TourRequestDto;
import at.fhtw.tourplanner_backend.dto.tour.TourResponseDto;
import at.fhtw.tourplanner_backend.dto.tourlog.TourLogExportDto;
import at.fhtw.tourplanner_backend.dto.tourlog.TourLogImportDto;
import at.fhtw.tourplanner_backend.dto.tourlog.TourLogRequestDto;
import at.fhtw.tourplanner_backend.entities.Tour;
import at.fhtw.tourplanner_backend.entities.TourLog;
import at.fhtw.tourplanner_backend.repositories.TourLogRepository;
import at.fhtw.tourplanner_backend.repositories.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class TourImportExportService {

    private final TourRepository tourRepository;
    private final TourLogRepository tourLogRepository;
    private final TourService tourService;
    private final TourLogService tourLogService;

    public TourExportResponseDto exportTours() {
        String username = getCurrentUsername();

        List<TourExportDto> exportedTours = tourRepository.findAllByUserUsername(username)
                .stream()
                .map(this::toExportDto)
                .toList();

        log.info("User '{}' exported {} tour(s).", username, exportedTours.size());

        return new TourExportResponseDto(exportedTours);
    }

    @Transactional
    public List<TourResponseDto> importTours(TourImportRequestDto request) {
        String username = getCurrentUsername();

        List<TourResponseDto> importedTours = new ArrayList<>();

        for (TourImportDto tourImportDto : request.getTours()) {
            TourResponseDto createdTour = tourService.createTour(toTourRequestDto(tourImportDto));

            for (TourLogImportDto logImportDto : tourImportDto.getLogs()) {
                tourLogService.createTourLog(toTourLogRequestDto(logImportDto, createdTour.getId()));
            }

            importedTours.add(createdTour);
        }

        log.info("User '{}' imported {} tour(s).", username, importedTours.size());

        return importedTours;
    }

    // HELPERS

    private TourExportDto toExportDto(Tour tour) {
        List<TourLogExportDto> logs = tourLogRepository.findByTourId(tour.getId())
                .stream()
                .map(this::toExportDto)
                .toList();

        return new TourExportDto(
                tour.getName(),
                tour.getDescription(),
                tour.getFromLocation(),
                tour.getToLocation(),
                tour.getTransportType(),
                tour.getDistanceKm(),
                tour.getEstimatedTime(),
                tour.getRouteGeometry(),
                logs
        );
    }

    private TourLogExportDto toExportDto(TourLog log) {
        return new TourLogExportDto(
                log.getLogDatetime(),
                log.getComment(),
                log.getDifficulty(),
                log.getTotalDistance(),
                log.getTotalTime(),
                log.getRating()
        );
    }

    private TourRequestDto toTourRequestDto(TourImportDto dto) {
        return new TourRequestDto(
                dto.getName(),
                dto.getDescription(),
                dto.getFrom(),
                dto.getTo(),
                dto.getTransportType(),
                dto.getDistance(),
                dto.getEstimatedTime(),
                dto.getRouteGeometry()
        );
    }

    private TourLogRequestDto toTourLogRequestDto(TourLogImportDto dto, Long tourId) {
        return new TourLogRequestDto(
                tourId,
                dto.getLogDatetime(),
                dto.getComment(),
                dto.getDifficulty(),
                dto.getTotalDistance(),
                dto.getTotalTime(),
                dto.getRating()
        );
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}
