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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TourLogService {

    private final TourLogRepository tourLogRepository;
    private final TourRepository tourRepository;

    public List<TourLogResponseDto> getAllTourLogs() {

        List<TourLog> logs = tourLogRepository.findAll();
        List<TourLogResponseDto> result = new ArrayList<>();

        for (TourLog log : logs) {
            result.add(TourLogMapper.toResponseDto(log));
        }

        return result;
    }

    public List<TourLogResponseDto> getTourLogsByTourId(Long tourId) {

        List<TourLog> logs = tourLogRepository.findByTourId(tourId);
        List<TourLogResponseDto> result = new ArrayList<>();

        for (TourLog log : logs) {
            result.add(TourLogMapper.toResponseDto(log));
        }
        return result;
    }

    public TourLogResponseDto getTourLogById(Long id) {

        TourLog log = tourLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour log not found with id: " + id));

        return TourLogMapper.toResponseDto(log);
    }

    public TourLogResponseDto createTourLog(TourLogRequestDto dto) {

        Tour tour = tourRepository.findById(dto.getTourId())
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found with id: " + dto.getTourId()));

        TourLog log = TourLogMapper.toEntity(dto, tour);

        TourLog savedLog = tourLogRepository.save(log);

        return TourLogMapper.toResponseDto(savedLog);
    }

    public TourLogResponseDto updateTourLog(Long id, TourLogRequestDto dto) {

        TourLog existingLog = tourLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour log not found with id: " + id));

        Tour tour = tourRepository.findById(dto.getTourId())
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found with id: " + dto.getTourId()));

        TourLogMapper.updateEntity(existingLog, dto, tour);
        TourLog savedLog = tourLogRepository.save(existingLog);
        return TourLogMapper.toResponseDto(savedLog);
    }

    public void deleteTourLog(Long id) {

        if (!tourLogRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tour log not found with id: " + id);
        }

        tourLogRepository.deleteById(id);
    }
}