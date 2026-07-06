package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.entities.TourLog;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * popularity (log count) and child-friendliness (0-100 score based on
 * recorded difficulty, distance and time across all logs of a tour).
 *
 * Pure computation over an already-loaded list of logs - the caller is
 * responsible for fetching a tour's logs.
 */
@Service
public class TourStatsService {

    public int computePopularity(List<TourLog> logs) {
        return logs.size();
    }

    public Double computeChildFriendliness(List<TourLog> logs) {
        // Only logs with all three recorded values can contribute to the average
        List<TourLog> completeLogs = logs.stream()
                .filter(log -> log.getDifficulty() != null
                        && log.getTotalDistance() != null
                        && log.getTotalTime() != null)
                .toList();

        if (completeLogs.isEmpty()) {
            return null;
        }

        double totalDifficulty = 0;
        double totalDistance = 0;
        double totalTime = 0;

        for (TourLog log : completeLogs) {
            totalDifficulty += log.getDifficulty();
            totalDistance += log.getTotalDistance();
            totalTime += log.getTotalTime();
        }

        double avgDifficulty = totalDifficulty / completeLogs.size();
        double avgDistance = totalDistance / completeLogs.size();
        double avgTimeHours = (totalTime / completeLogs.size()) / 60.0;

        double difficultyPenalty = (avgDifficulty - 1) * 12.5;

        // stops at 30km, every km brings the same penalty points, so 5km = 5 penalty points
        double distancePenalty = Math.min(avgDistance, 30);

        // * 2,5 points per hour, stops at 20 so the penalty won't be too big. 8 hours * 2,5 = 20 => maximum.
        double timePenalty = Math.min(avgTimeHours * 2.5, 20);

        double score = 100.0 - difficultyPenalty - distancePenalty - timePenalty;

        if (score < 0) {
            score = 0;
        }

        if (score > 100) {
            score = 100;
        }

        // *10 and /10 for score = 85.69 for example instead of normal Math.round(score) = 86
        return Math.round(score * 10.0) / 10.0;
    }
}