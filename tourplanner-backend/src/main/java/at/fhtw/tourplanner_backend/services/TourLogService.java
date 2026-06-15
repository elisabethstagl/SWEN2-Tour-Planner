package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.entities.Tour;
import at.fhtw.tourplanner_backend.entities.TourLog;
import at.fhtw.tourplanner_backend.repositories.TourLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourLogService {

    private final TourLogRepository tourLogRepository;

    public List<TourLog> getAllTourLogs() {
        return tourLogRepository.findAll();
    }

    public List<TourLog> getTourLogsByTourId(Long tourId) {
        return tourLogRepository.findByTourId(tourId);
    }

    public TourLog getTourLogById(Long id) {
        return tourLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour log not found with id: " + id));
    }

    public TourLog createTourLog(TourLog tourLog) {
        return tourLogRepository.save(tourLog);
    }

    public TourLog updateTourLog(Long id, TourLog updatedTourLog) {
        TourLog existing = getTourLogById(id);
        existing.setLogDatetime(updatedTourLog.getLogDatetime());
        existing.setComment(updatedTourLog.getComment());
        existing.setDifficulty(updatedTourLog.getDifficulty());
        existing.setTotalDistance(updatedTourLog.getTotalDistance());
        existing.setTotalTime(updatedTourLog.getTotalTime());
        existing.setRating(updatedTourLog.getRating());
        return tourLogRepository.save(existing);
    }

    public void deleteTourLog(Long id) {
        tourLogRepository.deleteById(id);
    }
}
