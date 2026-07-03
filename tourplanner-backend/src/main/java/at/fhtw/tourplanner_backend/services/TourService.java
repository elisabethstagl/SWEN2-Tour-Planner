package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.dto.tour.TourRequestDto;
import at.fhtw.tourplanner_backend.dto.tour.TourResponseDto;
import at.fhtw.tourplanner_backend.entities.Tour;
import at.fhtw.tourplanner_backend.entities.TourLog;
import at.fhtw.tourplanner_backend.entities.User;
import at.fhtw.tourplanner_backend.exceptions.ResourceNotFoundException;
import at.fhtw.tourplanner_backend.mapper.TourMapper;
import at.fhtw.tourplanner_backend.repositories.TourLogRepository;
import at.fhtw.tourplanner_backend.repositories.TourRepository;
import at.fhtw.tourplanner_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.netty.util.AsciiString.containsIgnoreCase;

@Service
@RequiredArgsConstructor
@Log4j2
public class TourService {

    private final TourRepository tourRepository;
    private final UserRepository userRepository;
    private final TourStatsService tourStatsService;
    private final TourLogRepository tourLogRepository;

    public List<TourResponseDto> getAllTours() {
        String username = getCurrentUsername();

        return tourRepository.findAllByUserUsername(username)
                .stream()
                .map(tour -> TourMapper.toResponseDto(withComputedFields(tour)))
                .toList();
    }

    public TourResponseDto getTourById(Long id) {
        String username = getCurrentUsername();

        Tour tour = tourRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tour not found with id: " + id));

        return TourMapper.toResponseDto(withComputedFields(tour));
    }

    public TourResponseDto createTour(TourRequestDto dto) {
        String username = getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + username));

        Tour tour = TourMapper.toEntity(dto, user);

        Tour savedTour = tourRepository.save(tour);

        log.info("User '{}' created tour '{}'.", username, tour.getName());

        return TourMapper.toResponseDto(withComputedFields(savedTour));
    }

    public TourResponseDto updateTour(Long id, TourRequestDto dto) {
        String username = getCurrentUsername();

        Tour existingTour = tourRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tour not found with id: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + username));

        TourMapper.updateEntity(existingTour, dto, user);

        Tour savedTour = tourRepository.save(existingTour);

        log.info("User '{}' updated tour {}.", username, id);

        return TourMapper.toResponseDto(withComputedFields(savedTour));
    }

    public void deleteTour(Long id) {
        String username = getCurrentUsername();

        Tour tour = tourRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tour not found with id: " + id));

        log.info("User '{}' deleted tour {}.", username, id);

        tourRepository.delete(tour);
    }

    public List<TourResponseDto> searchTours(String query) {
        String username = getCurrentUsername();

        List<Tour> tours = tourRepository.findAllByUserUsername(username)
                .stream()
                .map(this::withComputedFields)
                .toList();

        if (query == null || query.isBlank()) {
            return tours.stream()
                    .map(TourMapper::toResponseDto)
                    .toList();
        }

        String searchTerm = query.trim();

        // let the database check the tour's own fields (name, description, from, to, transport type)
        Set<Long> fieldMatchIds = new HashSet<>(tourRepository.findMatchingTourIds(username, searchTerm));

        String searchTermLower = searchTerm.toLowerCase();

        return tours.stream()
                .filter(tour -> fieldMatchIds.contains(tour.getId())
                        || matchesComputedValuesOrLogs(tour, searchTermLower))
                .map(TourMapper::toResponseDto)
                .toList();
    }

    // HELPER

    private Tour withComputedFields(Tour tour) {
        tour.setPopularity(tourStatsService.computePopularity(tour.getId()));
        tour.setChildFriendliness(tourStatsService.computeChildFriendliness(tour.getId()));
        return tour;
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    private boolean matchesComputedValuesOrLogs(Tour tour, String searchTerm) {
        if (tour.getPopularity() != null && String.valueOf(tour.getPopularity()).contains(searchTerm)) {
            return true;
        }
        if (tour.getChildFriendliness() != null && String.valueOf(tour.getChildFriendliness()).contains(searchTerm)) {
            return true;
        }

        List<TourLog> logs = tourLogRepository.findByTourId(tour.getId());

        for (TourLog log : logs) {
            if (containsIgnoreCase(log.getComment(), searchTerm)) {
                return true;
            }
        }

        return false;
    }

}