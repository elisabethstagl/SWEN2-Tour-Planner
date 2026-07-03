package at.fhtw.tourplanner_backend.repositories;

import at.fhtw.tourplanner_backend.entities.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {

    List<Tour> findAllByUserUsername(String username);

    Optional<Tour> findByIdAndUserUsername(Long id, String username);

    boolean existsByIdAndUserUsername(Long id, String username);

    @Query("""
            SELECT t.id FROM Tour t
            WHERE t.user.username = :username
            AND (
                LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                OR LOWER(t.fromLocation) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                OR LOWER(t.toLocation) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                OR LOWER(t.transportType) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            )
            """)
    List<Long> findMatchingTourIds(@Param("username") String username, @Param("searchTerm") String searchTerm);
}