package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.dto.tourlog.TourLogRequestDto;
import at.fhtw.tourplanner_backend.dto.tourlog.TourLogResponseDto;
import at.fhtw.tourplanner_backend.entities.Tour;
import at.fhtw.tourplanner_backend.entities.TourLog;
import at.fhtw.tourplanner_backend.exceptions.ResourceNotFoundException;
import at.fhtw.tourplanner_backend.mapper.TourLogMapper;
import at.fhtw.tourplanner_backend.repositories.TourLogRepository;
import at.fhtw.tourplanner_backend.repositories.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class TourLogService {

    private final TourLogRepository tourLogRepository;
    private final TourRepository tourRepository;

    public List<TourLogResponseDto> getAllTourLogs() {
        String username = getCurrentUsername();

        return tourLogRepository.findAllByTourUserUsername(username)
                .stream()
                .map(tourLog -> TourLogMapper.toResponseDto(tourLog))
                .toList();
    }

    public List<TourLogResponseDto> getTourLogsByTourId(Long tourId) {
        String username = getCurrentUsername();

        return tourLogRepository.findByTourIdAndTourUserUsername(tourId, username)
                .stream()
                .map(log -> TourLogMapper.toResponseDto(log))
                .toList();
    }

    public TourLogResponseDto getTourLogById(Long id) {
        String username = getCurrentUsername();

        TourLog tourLog = tourLogRepository.findByIdAndTourUserUsername(id, username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tour log not found with id: " + id));

        return TourLogMapper.toResponseDto(tourLog);
    }

    public TourLogResponseDto createTourLog(TourLogRequestDto dto) {
        String username = getCurrentUsername();

        Tour tour = tourRepository.findByIdAndUserUsername(dto.getTourId(), username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tour not found with id: " + dto.getTourId()));

        TourLog tourLog = TourLogMapper.toEntity(dto, tour);

        TourLog savedLog = tourLogRepository.save(tourLog);

        log.info("User '{}' added a log to tour {}.", username, dto.getTourId());

        return TourLogMapper.toResponseDto(savedLog);
    }

    public TourLogResponseDto updateTourLog(Long id, TourLogRequestDto dto) {
        String username = getCurrentUsername();

        TourLog existingLog = tourLogRepository.findByIdAndTourUserUsername(id, username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tour log not found with id: " + id));

        Tour tour = tourRepository.findByIdAndUserUsername(dto.getTourId(), username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tour not found with id: " + dto.getTourId()));

        TourLogMapper.updateEntity(existingLog, dto, tour);

        TourLog savedLog = tourLogRepository.save(existingLog);

        log.info("User '{}' updated tour log {}.", username, id);

        return TourLogMapper.toResponseDto(savedLog);
    }

    public void deleteTourLog(Long id) {
        String username = getCurrentUsername();

        TourLog tourLog = tourLogRepository.findByIdAndTourUserUsername(id, username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tour log not found with id: " + id));

        log.info("User '{}' deleted tour log {}.", username, id);

        tourLogRepository.delete(tourLog);
    }

    // HELPER

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}