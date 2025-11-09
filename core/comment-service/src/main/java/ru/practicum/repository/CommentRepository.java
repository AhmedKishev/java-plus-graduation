package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.CommentCountDto;
import ru.practicum.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByEventId(Long eventId, Pageable pageable);


    @Query("SELECT new ru.practicum.dto.CommentCountDto(c.eventId, COUNT(c.id)) " +
            "FROM Comment c " +
            "WHERE c.eventId IN :eventIds " +
            "GROUP BY c.eventId")
    List<CommentCountDto> findAllCommentCount(@Param("eventIds") List<Long> eventIds);

    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE c.text ILIKE CONCAT('%', :text, '%')")
    Page<Comment> findAllByText(@Param("text") String text, Pageable pageable);

    Page<Comment> findAllByAuthorId(Long userId, Pageable pageable);

    Optional<Comment> findByEventIdAndId(Long eventId, Long commentId);
}