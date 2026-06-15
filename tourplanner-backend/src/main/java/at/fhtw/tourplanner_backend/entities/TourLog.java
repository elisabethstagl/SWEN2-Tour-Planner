package at.fhtw.tourplanner_backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "tour_logs")
public class TourLog {

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Column(name = "log_datetime", nullable = false)
    private Instant logDatetime;

    @Column(name = "comment", length = Integer.MAX_VALUE)
    private String comment;

    @Column(name = "difficulty")
    private Integer difficulty;

    @Column(name = "total_distance")
    private Double totalDistance;

    @Column(name = "total_time")
    private Integer totalTime;

    @Column(name = "rating")
    private Integer rating;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;


}