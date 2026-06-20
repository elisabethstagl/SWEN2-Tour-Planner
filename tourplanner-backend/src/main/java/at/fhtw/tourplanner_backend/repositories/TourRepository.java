package at.fhtw.tourplanner_backend.repositories;

import at.fhtw.tourplanner_backend.entities.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
}