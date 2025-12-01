package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Table(name = "event_similarities")
public class EventSimilarity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "event_a", nullable = false)
    Long eventA;

    @Column(name = "event_b", nullable = false)
    Long eventB;

    @Column(name = "score", nullable = false)
    Double score;

    @Column(name = "timestamp", nullable = false)
    Instant timestamp;

}
