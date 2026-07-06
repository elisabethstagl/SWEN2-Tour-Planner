package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.dto.tour.TourRequestDto;
import at.fhtw.tourplanner_backend.dto.tour.TourResponseDto;
import at.fhtw.tourplanner_backend.entities.Tour;
import at.fhtw.tourplanner_backend.entities.TourLog;
import at.fhtw.tourplanner_backend.entities.User;
import at.fhtw.tourplanner_backend.exceptions.ResourceNotFoundException;
import at.fhtw.tourplanner_backend.repositories.TourLogRepository;
import at.fhtw.tourplanner_backend.repositories.TourRepository;
import at.fhtw.tourplanner_backend.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    private static final String USERNAME = "alice";

    @Mock
    private TourRepository tourRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TourStatsService tourStatsService;
    @Mock
    private TourLogRepository tourLogRepository;

    @InjectMocks
    private TourService tourService;

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
    void getAllTours_returnsToursOfCurrentUserOnly() {
        Tour tour = tourWith(1L, "Vienna Woods");
        when(tourRepository.findAllByUserUsername(USERNAME)).thenReturn(List.of(tour));

        List<TourResponseDto> result = tourService.getAllTours();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Vienna Woods");
        verify(tourLogRepository, times(1)).findByTourId(1L);
        verify(tourStatsService).computePopularity(anyList());
        verify(tourStatsService).computeChildFriendliness(anyList());
    }

    @Test
    void getTourById_returnsTour_whenOwnedByCurrentUser() {
        Tour tour = tourWith(1L, "Vienna Woods");
        when(tourRepository.findByIdAndUserUsername(1L, USERNAME)).thenReturn(Optional.of(tour));

        TourResponseDto result = tourService.getTourById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getTourById_throwsResourceNotFound_whenTourNotOwnedOrMissing() {
        when(tourRepository.findByIdAndUserUsername(99L, USERNAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tourService.getTourById(99L));
    }

    @Test
    void createTour_savesTourAssignedToCurrentUser() {
        User user = userWith(USERNAME);
        TourRequestDto dto = tourRequestWith("New Tour");
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(tourRepository.save(any(Tour.class))).thenAnswer(invocation -> {
            Tour saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        TourResponseDto result = tourService.createTour(dto);

        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getName()).isEqualTo("New Tour");
        verify(tourRepository).save(argThat(t -> t.getUser() == user));
    }

    @Test
    void updateTour_updatesOnlyWhenTourBelongsToCurrentUser() {
        Tour existing = tourWith(1L, "Old Name");
        User user = userWith(USERNAME);
        TourRequestDto dto = tourRequestWith("Updated Name");

        when(tourRepository.findByIdAndUserUsername(1L, USERNAME)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(tourRepository.save(existing)).thenReturn(existing);

        TourResponseDto result = tourService.updateTour(1L, dto);

        assertThat(result.getName()).isEqualTo("Updated Name");
    }

    @Test
    void updateTour_throwsResourceNotFound_whenTourNotOwnedByCurrentUser() {
        when(tourRepository.findByIdAndUserUsername(1L, USERNAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tourService.updateTour(1L, tourRequestWith("Updated Name")));

        verify(tourRepository, never()).save(any());
    }

    @Test
    void deleteTour_deletesTour_whenOwnedByCurrentUser() {
        Tour tour = tourWith(1L, "Vienna Woods");
        when(tourRepository.findByIdAndUserUsername(1L, USERNAME)).thenReturn(Optional.of(tour));

        tourService.deleteTour(1L);

        verify(tourRepository).delete(tour);
    }

    @Test
    void deleteTour_throwsResourceNotFound_whenNotOwnedByCurrentUser() {
        when(tourRepository.findByIdAndUserUsername(1L, USERNAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tourService.deleteTour(1L));
        verify(tourRepository, never()).delete(any());
    }

    @Test
    void searchTours_returnsAllTours_whenQueryIsBlank() {
        Tour tour = tourWith(1L, "Vienna Woods");
        when(tourRepository.findAllByUserUsername(USERNAME)).thenReturn(List.of(tour));

        List<TourResponseDto> result = tourService.searchTours("   ");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchTours_matchesTourByOwnFields() {
        Tour matching = tourWith(1L, "Vienna Woods");
        Tour nonMatching = tourWith(2L, "Alps Trail");
        when(tourRepository.findAllByUserUsername(USERNAME)).thenReturn(List.of(matching, nonMatching));
        when(tourLogRepository.findByTourId(anyLong())).thenReturn(List.of());

        List<TourResponseDto> result = tourService.searchTours("Vienna");

        assertThat(result).extracting(TourResponseDto::getId).containsExactly(1L);
    }

    @Test
    void searchTours_matchesTourByComputedPopularityValue() {
        Tour tour = tourWith(1L, "Alps Trail");
        when(tourRepository.findAllByUserUsername(USERNAME)).thenReturn(List.of(tour));
        when(tourStatsService.computePopularity(anyList())).thenReturn(7);

        List<TourResponseDto> result = tourService.searchTours("7");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchTours_matchesTourByLogComment() {
        Tour tour = tourWith(1L, "Alps Trail");
        TourLog log = new TourLog();
        log.setComment("Beautiful sunrise on this hike");

        when(tourRepository.findAllByUserUsername(USERNAME)).thenReturn(List.of(tour));
        when(tourLogRepository.findByTourId(1L)).thenReturn(List.of(log));

        List<TourResponseDto> result = tourService.searchTours("sunrise");

        assertThat(result).hasSize(1);
        verify(tourLogRepository, times(1)).findByTourId(1L);
    }

    @Test
    void searchTours_excludesToursThatMatchNothing() {
        Tour tour = tourWith(1L, "Alps Trail");
        when(tourRepository.findAllByUserUsername(USERNAME)).thenReturn(List.of(tour));
        when(tourLogRepository.findByTourId(1L)).thenReturn(List.of());

        List<TourResponseDto> result = tourService.searchTours("kayak");

        assertThat(result).isEmpty();
    }

    // HELPERS

    private Tour tourWith(Long id, String name) {
        Tour tour = new Tour();
        tour.setId(id);
        tour.setName(name);
        tour.setUser(userWith(USERNAME));
        return tour;
    }

    private User userWith(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }

    private TourRequestDto tourRequestWith(String name) {
        TourRequestDto dto = new TourRequestDto();
        dto.setName(name);
        dto.setDescription("A nice tour");
        dto.setFrom("Vienna");
        dto.setTo("Salzburg");
        dto.setTransportType("bike");
        dto.setDistance(100.0);
        dto.setEstimatedTime(120);
        return dto;
    }
}
