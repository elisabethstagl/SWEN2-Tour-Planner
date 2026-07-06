package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.dto.tourlog.TourLogRequestDto;
import at.fhtw.tourplanner_backend.dto.tourlog.TourLogResponseDto;
import at.fhtw.tourplanner_backend.entities.Tour;
import at.fhtw.tourplanner_backend.entities.TourLog;
import at.fhtw.tourplanner_backend.exceptions.ResourceNotFoundException;
import at.fhtw.tourplanner_backend.repositories.TourLogRepository;
import at.fhtw.tourplanner_backend.repositories.TourRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourLogServiceTest {

    private static final String USERNAME = "alice";

    @Mock
    private TourLogRepository tourLogRepository;
    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private TourLogService tourLogService;

    @BeforeEach
    void setUpSecurityContext() {
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken(USERNAME, null));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllTourLogs_returnsLogsOfCurrentUser() {
        TourLog log = logWith(1L);
        when(tourLogRepository.findAllByTourUserUsername(USERNAME)).thenReturn(List.of(log));

        List<TourLogResponseDto> result = tourLogService.getAllTourLogs();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void getTourLogsByTourId_returnsLogsForThatTourAndUser() {
        TourLog log = logWith(1L);
        when(tourLogRepository.findByTourIdAndTourUserUsername(10L, USERNAME)).thenReturn(List.of(log));

        List<TourLogResponseDto> result = tourLogService.getTourLogsByTourId(10L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getTourLogById_returnsLog_whenOwnedByCurrentUser() {
        TourLog log = logWith(1L);
        when(tourLogRepository.findByIdAndTourUserUsername(1L, USERNAME)).thenReturn(Optional.of(log));

        TourLogResponseDto result = tourLogService.getTourLogById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getTourLogById_throwsResourceNotFound_whenMissing() {
        when(tourLogRepository.findByIdAndTourUserUsername(1L, USERNAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tourLogService.getTourLogById(1L));
    }

    @Test
    void createTourLog_savesLog_whenParentTourOwnedByCurrentUser() {
        Tour tour = tourWith(10L);
        TourLogRequestDto dto = logRequestFor(10L);
        when(tourRepository.findByIdAndUserUsername(10L, USERNAME)).thenReturn(Optional.of(tour));
        when(tourLogRepository.save(any(TourLog.class))).thenAnswer(invocation -> {
            TourLog saved = invocation.getArgument(0);
            saved.setId(5L);
            return saved;
        });

        TourLogResponseDto result = tourLogService.createTourLog(dto);

        assertThat(result.getId()).isEqualTo(5L);
        verify(tourLogRepository).save(argThat(l -> l.getTour() == tour));
    }

    @Test
    void createTourLog_throwsResourceNotFound_whenParentTourNotOwnedByCurrentUser() {
        when(tourRepository.findByIdAndUserUsername(10L, USERNAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tourLogService.createTourLog(logRequestFor(10L)));
        verify(tourLogRepository, never()).save(any());
    }

    @Test
    void updateTourLog_updatesLog_whenOwnedByCurrentUser() {
        TourLog existing = logWith(1L);
        Tour tour = tourWith(10L);
        TourLogRequestDto dto = logRequestFor(10L);

        when(tourLogRepository.findByIdAndTourUserUsername(1L, USERNAME)).thenReturn(Optional.of(existing));
        when(tourRepository.findByIdAndUserUsername(10L, USERNAME)).thenReturn(Optional.of(tour));
        when(tourLogRepository.save(existing)).thenReturn(existing);

        TourLogResponseDto result = tourLogService.updateTourLog(1L, dto);

        assertThat(result.getComment()).isEqualTo(dto.getComment());
    }

    @Test
    void deleteTourLog_deletesLog_whenOwnedByCurrentUser() {
        TourLog log = logWith(1L);
        when(tourLogRepository.findByIdAndTourUserUsername(1L, USERNAME)).thenReturn(Optional.of(log));

        tourLogService.deleteTourLog(1L);

        verify(tourLogRepository).delete(log);
    }

    @Test
    void deleteTourLog_throwsResourceNotFound_whenNotOwnedByCurrentUser() {
        when(tourLogRepository.findByIdAndTourUserUsername(1L, USERNAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tourLogService.deleteTourLog(1L));
        verify(tourLogRepository, never()).delete(any());
    }

    // HELPERS

    private TourLog logWith(Long id) {
        TourLog log = new TourLog();
        log.setId(id);
        log.setTour(tourWith(10L));
        log.setLogDatetime(Instant.parse("2026-01-01T10:00:00Z"));
        log.setComment("Great hike");
        log.setDifficulty(2);
        log.setTotalDistance(12.5);
        log.setTotalTime(90);
        log.setRating(4);
        return log;
    }

    private Tour tourWith(Long id) {
        Tour tour = new Tour();
        tour.setId(id);
        return tour;
    }

    private TourLogRequestDto logRequestFor(Long tourId) {
        return new TourLogRequestDto(
                tourId,
                Instant.parse("2026-01-01T10:00:00Z"),
                "Great hike",
                2,
                12.5,
                90,
                4
        );
    }
}
