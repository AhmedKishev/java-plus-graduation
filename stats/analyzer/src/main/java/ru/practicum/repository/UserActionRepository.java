package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.UserAction;

import java.util.List;
import java.util.Set;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    List<UserAction> findAllByUserId(Long userId, PageRequest pageRequest);

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    List<UserAction> findAllByEventIdInAndUserId(Set<Long> viewedEvents, Long userId);

    UserAction findByEventIdAndUserId(Long eventId, Long userId);

    @Query("select COALESCE(SUM(ua.mark), 0) from UserAction as ua where ua.eventId = :eventId")
    Float getSumWeightByEventId(@Param("eventId") Long eventId);
}
