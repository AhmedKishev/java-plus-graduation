package ru.practicum.event.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.Category;
import ru.practicum.enums.State;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    Long id;

    @Column(name = "initiator_id", nullable = false)
    Long initiatorId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Column(name = "title", length = 120, nullable = false)
    String title;

    @Column(name = "annotation", length = 2000, nullable = false)
    String annotation;

    @Column(name = "description", length = 7000, nullable = false)
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 20, nullable = false)
    State state;

    @Embedded
    Location location;

    @Column(name = "participant_limit", nullable = false)
    Long participantLimit;

    @Column(name = "request_moderation", nullable = false)
    Boolean requestModeration;

    @Column(name = "confirmed_requests", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    @Builder.Default
    Long confirmedRequests = 0L;

    @Column(name = "paid", nullable = false)
    Boolean paid;

    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(name = "created_on", nullable = false)
    LocalDateTime createdOn;

}