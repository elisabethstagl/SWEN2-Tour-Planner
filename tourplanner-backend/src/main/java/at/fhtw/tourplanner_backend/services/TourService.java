package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.dto.tour.TourRequestDto;
import at.fhtw.tourplanner_backend.dto.tour.TourResponseDto;
import at.fhtw.tourplanner_backend.entities.Tour;
import at.fhtw.tourplanner_backend.entities.User;
import at.fhtw.tourplanner_backend.exceptions.ResourceNotFoundException;
import at.fhtw.tourplanner_backend.mapper.TourMapper;
import at.fhtw.tourplanner_backend.repositories.TourRepository;
import at.fhtw.tourplanner_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TourService {

    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    public List<TourResponseDto> getAllTours() {

        List<Tour> tours = tourRepository.findAll();
        List<TourResponseDto> result = new ArrayList<>();

        for (Tour tour : tours) {
            result.add(TourMapper.toResponseDto(tour));
        }

        return result;
    }

    public TourResponseDto getTourById(Long id) {

        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found with id: " + id));

        return TourMapper.toResponseDto(tour);
    }

    public TourResponseDto createTour(TourRequestDto dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        Tour tour = TourMapper.toEntity(dto, user);
        Tour savedTour = tourRepository.save(tour);
        return TourMapper.toResponseDto(savedTour);
    }

    public TourResponseDto updateTour(Long id, TourRequestDto dto) {

        Tour existingTour = tourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found with id: " + id));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        TourMapper.updateEntity(existingTour, dto, user);
        Tour savedTour = tourRepository.save(existingTour);
        return TourMapper.toResponseDto(savedTour);
    }

    public void deleteTour(Long id) {

        if (!tourRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tour not found with id: " + id);
        }

        tourRepository.deleteById(id);
    }
}