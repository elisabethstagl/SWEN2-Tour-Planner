package at.fhtw.tourplanner_backend.services;

import at.fhtw.tourplanner_backend.entities.TourLog;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TourStatsServiceTest {

    private final TourStatsService tourStatsService = new TourStatsService();

    @Test
    void computePopularity_returnsZero_whenTourHasNoLogs() {
        int popularity = tourStatsService.computePopularity(List.of());

        assertThat(popularity).isZero();
    }

    @Test
    void computePopularity_returnsNumberOfLogs() {
        int popularity = tourStatsService.computePopularity(List.of(
                logWith(1, 5.0, 60),
                logWith(2, 8.0, 90),
                logWith(3, 3.0, 30)
        ));

        assertThat(popularity).isEqualTo(3);
    }

    @Test
    void computeChildFriendliness_returnsNull_whenTourHasNoLogs() {
        Double result = tourStatsService.computeChildFriendliness(List.of());

        assertThat(result).isNull();
    }

    @Test
    void computeChildFriendliness_returnsHighScore_forEasyShortTour() {
        // difficulty 1 (easiest), short distance, short time -> should stay close to 100
        Double score = tourStatsService.computeChildFriendliness(List.of(
                logWith(1, 2.0, 20)
        ));

        assertThat(score).isGreaterThan(90.0);
    }

    @Test
    void computeChildFriendliness_returnsLowerScore_forDifficultLongTour() {
        // difficulty 5, long distance and time -> score should drop significantly
        Double score = tourStatsService.computeChildFriendliness(List.of(
                logWith(5, 50.0, 400)
        ));

        assertThat(score).isLessThan(50.0);
    }

    @Test
    void computeChildFriendliness_returnsZero_whenValuesAreExtreme() {
        Double score = tourStatsService.computeChildFriendliness(List.of(
                logWith(5, 500.0, 5000)
        ));

        assertThat(score).isEqualTo(0.0);
    }

    @Test
    void computeChildFriendliness_returnsNull_whenAllLogsAreMissingValues() {
        // A log missing difficulty/distance/time must not crash the computation;
        // with no usable log it degrades to null
        TourLog logWithoutDifficulty = new TourLog();
        logWithoutDifficulty.setDifficulty(null);
        logWithoutDifficulty.setTotalDistance(5.0);
        logWithoutDifficulty.setTotalTime(60);

        Double result = tourStatsService.computeChildFriendliness(List.of(logWithoutDifficulty));

        assertThat(result).isNull();
    }

    @Test
    void computeChildFriendliness_ignoresIncompleteLogs_whenComputingAverage() {
        TourLog incompleteLog = new TourLog();
        incompleteLog.setDifficulty(null);
        incompleteLog.setTotalDistance(999.0);
        incompleteLog.setTotalTime(9999);

        TourLog completeLog = logWith(1, 2.0, 20);

        Double result = tourStatsService.computeChildFriendliness(List.of(incompleteLog, completeLog));

        assertThat(result).isGreaterThan(90.0);
    }

    private TourLog logWith(int difficulty, double totalDistance, int totalTime) {
        TourLog log = new TourLog();
        log.setDifficulty(difficulty);
        log.setTotalDistance(totalDistance);
        log.setTotalTime(totalTime);
        return log;
    }
}
