package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.ParticipationRequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "requester_id")
    Long requesterId;

    @Column(name = "event_id")
    Long eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    ParticipationRequestStatus status;

    @Column(name = "created_at")
    LocalDateTime created;

}
