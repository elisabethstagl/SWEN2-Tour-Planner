package at.fhtw.tourplanner_backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "tours")
public class Tour {
    // for createdAt - sets createdAt right before Hibernate saves the entity
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "from_location", nullable = false, length = 200)
    private String fromLocation;

    @Column(name = "to_location", nullable = false, length = 200)
    private String toLocation;

    @Column(name = "transport_type", nullable = false, length = 50)
    private String transportType;

    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "estimated_time")
    private Integer estimatedTime;

    @Column(name = "map_image_path", length = 500)
    private String mapImagePath;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

    //field is not stored in the DB, only calculated and returned in JSON responses
    @Transient
    private Integer popularity;

    @Transient
    private Double childFriendliness;
}