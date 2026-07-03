package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.entities.TourLog;
import at.fhtw.tourplanner_backend.repositories.TourLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * popularity (log count) and child-friendliness (0-100 score based on
 * recorded difficulty, distance and time across all logs of a tour).
 */
@Service
@RequiredArgsConstructor
public class TourStatsService {

    private final TourLogRepository tourLogRepository;

    public int computePopularity(Long tourId) {
        List<TourLog> logs = tourLogRepository.findByTourId(tourId);
        return logs.size();
    }


    public Double computeChildFriendliness(Long tourId) {
        List<TourLog> logs = tourLogRepository.findByTourId(tourId);

        if (logs.isEmpty()) {
            return null;
        }

        double totalDifficulty = 0;
        double totalDistance = 0;
        double totalTime = 0;

        for (TourLog log : logs) {
            totalDifficulty += log.getDifficulty();
            totalDistance += log.getTotalDistance();
            totalTime += log.getTotalTime();
        }

        double avgDifficulty = totalDifficulty / logs.size();
        double avgDistance = totalDistance / logs.size();
        double avgTime = totalTime / logs.size();

        double score = 100.0;

        // Higher difficulty reduces child-friendliness
        score -= (avgDifficulty - 1) * 15;

        // Longer distance reduces child-friendliness
        score -= avgDistance * 1.5;

        // Longer duration reduces child-friendliness
        score -= avgTime * 0.1;

        if (score < 0) {
            score = 0;
        }

        if (score > 100) {
            score = 100;
        }

        return Math.round(score * 10.0) / 10.0;
    }
}