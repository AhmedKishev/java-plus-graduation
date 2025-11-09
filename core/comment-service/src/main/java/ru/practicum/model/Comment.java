package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "textual_content", length = 1000, nullable = false)
    String text;

    @Column(name = "author_id", nullable = false)
    Long authorId;

   @Column(name = "event_id", nullable = false)
    Long eventId;

    @Column(name = "create_time", nullable = false)
    LocalDateTime createTime;

    @Column(name = "patch_time")
    LocalDateTime patchTime;

    @Column(name = "approved", nullable = false)
    Boolean approved;

    public boolean isApproved() {
        return approved;
    }
}