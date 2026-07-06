package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.dto.tour.TourExportDto;
import at.fhtw.tourplanner_backend.dto.tour.TourExportResponseDto;
import at.fhtw.tourplanner_backend.dto.tour.TourImportDto;
import at.fhtw.tourplanner_backend.dto.tour.TourImportRequestDto;
import at.fhtw.tourplanner_backend.dto.tour.TourResponseDto;
import at.fhtw.tourplanner_backend.dto.tourlog.TourLogImportDto;
import at.fhtw.tourplanner_backend.dto.tourlog.TourLogResponseDto;
import at.fhtw.tourplanner_backend.entities.Tour;
import at.fhtw.tourplanner_backend.entities.TourLog;
import at.fhtw.tourplanner_backend.entities.User;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourImportExportServiceTest {

    private static final String USERNAME = "alice";

    @Mock
    private TourRepository tourRepository;
    @Mock
    private TourLogRepository tourLogRepository;
    @Mock
    private TourService tourService;
    @Mock
    private TourLogService tourLogService;

    @InjectMocks
    private TourImportExportService tourImportExportService;

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
    void exportTours_returnsEmptyList_whenUserHasNoTours() {
        when(tourRepository.findAllByUserUsername(USERNAME)).thenReturn(List.of());

        TourExportResponseDto result = tourImportExportService.exportTours();

        assertThat(result.getTours()).isEmpty();
    }

    @Test
    void exportTours_includesTourFieldsAndItsLogs() {
        Tour tour = tourWith(1L, "Vienna Woods");
        TourLog log = logWith("Beautiful sunrise");
        when(tourRepository.findAllByUserUsername(USERNAME)).thenReturn(List.of(tour));
        when(tourLogRepository.findByTourId(1L)).thenReturn(List.of(log));

        TourExportResponseDto result = tourImportExportService.exportTours();

        assertThat(result.getTours()).hasSize(1);
        TourExportDto exported = result.getTours().getFirst();
        assertThat(exported.getName()).isEqualTo("Vienna Woods");
        assertThat(exported.getLogs()).hasSize(1);
        assertThat(exported.getLogs().getFirst().getComment()).isEqualTo("Beautiful sunrise");
    }

    @Test
    void exportTours_onlyReadsToursOfCurrentUser() {
        when(tourRepository.findAllByUserUsername(USERNAME)).thenReturn(List.of());

        tourImportExportService.exportTours();

        verify(tourRepository).findAllByUserUsername(USERNAME);
        verify(tourRepository, never()).findAll();
    }

    @Test
    void importTours_createsTourAndItsLogs_assignedToCurrentUser() {
        TourImportDto tourImportDto = tourImportWith("Alps Trail", List.of(logImportWith("Great hike")));
        TourImportRequestDto request = new TourImportRequestDto(List.of(tourImportDto));

        TourResponseDto createdTour = new TourResponseDto();
        createdTour.setId(42L);
        createdTour.setName("Alps Trail");
        when(tourService.createTour(any())).thenReturn(createdTour);
        when(tourLogService.createTourLog(any())).thenReturn(new TourLogResponseDto());

        List<TourResponseDto> result = tourImportExportService.importTours(request);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(42L);
        verify(tourService).createTour(argThat(dto -> dto.getName().equals("Alps Trail")));
        verify(tourLogService).createTourLog(argThat(dto ->
                dto.getTourId().equals(42L) && dto.getComment().equals("Great hike")));
    }

    // HELPERS

    private Tour tourWith(Long id, String name) {
        Tour tour = new Tour();
        tour.setId(id);
        tour.setName(name);
        tour.setDescription("A nice tour");
        tour.setFromLocation("Vienna");
        tour.setToLocation("Salzburg");
        tour.setTransportType("bike");
        tour.setDistanceKm(100.0);
        tour.setEstimatedTime(120);
        User user = new User();
        user.setUsername(USERNAME);
        tour.setUser(user);
        return tour;
    }

    private TourLog logWith(String comment) {
        TourLog log = new TourLog();
        log.setLogDatetime(Instant.parse("2026-01-01T10:00:00Z"));
        log.setComment(comment);
        log.setDifficulty(2);
        log.setTotalDistance(12.5);
        log.setTotalTime(90);
        log.setRating(4);
        return log;
    }

    private TourImportDto tourImportWith(String name, List<TourLogImportDto> logs) {
        return new TourImportDto(
                name,
                "A nice tour",
                "Vienna",
                "Salzburg",
                "bike",
                100.0,
                120,
                null,
                logs
        );
    }

    private TourLogImportDto logImportWith(String comment) {
        return new TourLogImportDto(
                Instant.parse("2026-01-01T10:00:00Z"),
                comment,
                2,
                12.5,
                90,
                4
        );
    }
}
