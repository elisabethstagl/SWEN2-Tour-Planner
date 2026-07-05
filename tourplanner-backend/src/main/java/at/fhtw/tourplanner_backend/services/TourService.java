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

import java.util.HashSet;
import java.util.List;

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

        //gets username for comparison if user is allowed to update tour
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

        List<TourWithLogs> toursWithLogs = tourRepository.findAllByUserUsername(username)
                .stream()
                .map(this::withComputedFieldsAndLogs)
                .toList();

        if (query == null || query.isBlank()) {
            return toursWithLogs.stream()
                    .map(t -> TourMapper.toResponseDto(t.tour()))
                    .toList();
        }

        String searchTerm = query.trim().toLowerCase();

        return toursWithLogs.stream()
                .filter(t -> matchesOwnFields(t.tour(), searchTerm)
                        || matchesComputedValuesOrLogs(t.tour(), t.logs(), searchTerm))
                .map(t -> TourMapper.toResponseDto(t.tour()))
                .toList();
    }

    // HELPER

    // sets the computed fields (child friendliness and popularity) -> calculated in tourStatsService
    private Tour withComputedFields(Tour tour) {
        return withComputedFieldsAndLogs(tour).tour();
    }

    // Fetches a tour's logs once and reuses that same list for both
    // computed values (TourStatsService) and, when called from searchTours(),
    // for matching log comments.
    private TourWithLogs withComputedFieldsAndLogs(Tour tour) {
        List<TourLog> logs = tourLogRepository.findByTourId(tour.getId());
        tour.setPopularity(tourStatsService.computePopularity(logs));
        tour.setChildFriendliness(tourStatsService.computeChildFriendliness(logs));
        return new TourWithLogs(tour, logs);
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    private boolean matchesOwnFields(Tour tour, String searchTerm) {
        return containsIgnoreCase(tour.getName(), searchTerm)
                || containsIgnoreCase(tour.getDescription(), searchTerm)
                || containsIgnoreCase(tour.getFromLocation(), searchTerm)
                || containsIgnoreCase(tour.getToLocation(), searchTerm)
                || containsIgnoreCase(tour.getTransportType(), searchTerm);
    }

    private boolean matchesComputedValuesOrLogs(Tour tour, List<TourLog> logs, String searchTerm) {
        if (tour.getPopularity() != null && String.valueOf(tour.getPopularity()).contains(searchTerm)) {
            return true;
        }

        if (tour.getChildFriendliness() != null && String.valueOf(tour.getChildFriendliness()).contains(searchTerm)) {
            return true;
        }

        for (TourLog log : logs) {
            String comment = log.getComment();
            if (comment != null && comment.toLowerCase().contains(searchTerm)) {
                return true;
            }
        }

        return false;
    }

    private record TourWithLogs(Tour tour, List<TourLog> logs) {}
}