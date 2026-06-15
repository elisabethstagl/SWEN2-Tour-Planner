package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.entities.Tour;
import at.fhtw.tourplanner_backend.repositories.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourService {

    private final TourRepository tourRepository;

    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }

    public Tour getTourById(Long id) {
        return tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found with id: " + id));
    }

    public Tour createTour(Tour tour) {
        return tourRepository.save(tour);
    }

    public Tour updateTour(Long id, Tour updatedTour) {
        Tour existing = getTourById(id);
        existing.setName(updatedTour.getName());
        existing.setDescription(updatedTour.getDescription());
        existing.setFromLocation(updatedTour.getFromLocation());
        existing.setToLocation(updatedTour.getToLocation());
        existing.setTransportType(updatedTour.getTransportType());
        existing.setDistanceKm(updatedTour.getDistanceKm());
        existing.setEstimatedTime(updatedTour.getEstimatedTime());
        existing.setMapImagePath(updatedTour.getMapImagePath());
        return tourRepository.save(existing);
    }

    public void deleteTour(Long id) {
        tourRepository.deleteById(id);
    }
}
