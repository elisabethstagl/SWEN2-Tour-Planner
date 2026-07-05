package at.fhtw.tourplanner_backend.repositories;

import at.fhtw.tourplanner_backend.entities.TourLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourLogRepository extends JpaRepository<TourLog, Long> {
    List<TourLog> findByTourId(Long tourId);

    List<TourLog> findByTourIdAndTourUserUsername(Long tourId, String username);

    List<TourLog> findAllByTourUserUsername(String username);

    Optional<TourLog> findByIdAndTourUserUsername(Long id, String username);
}
