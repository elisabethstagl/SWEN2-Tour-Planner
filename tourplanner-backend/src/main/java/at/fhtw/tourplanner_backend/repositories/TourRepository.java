package at.fhtw.tourplanner_backend.repositories;

import at.fhtw.tourplanner_backend.entities.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {

    List<Tour> findAllByUserUsername(String username);

    Optional<Tour> findByIdAndUserUsername(Long id, String username);
}