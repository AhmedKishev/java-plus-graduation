package ru.practicum.event.service;



import jakarta.validation.constraints.Positive;
import ru.practicum.dto.EventAdminParams;
import ru.practicum.dto.EventCommentDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.UpdateEventDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EventAdminService {
    List<EventFullDto> getAllEventsByParams(EventAdminParams eventAdminParams);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventDto updateEventDto);

    EventFullDto getAdminEventById(@Positive Long id);

    EventCommentDto getEventByComment(@Positive Long id);

    Map<Long, EventCommentDto> getEventsByIds(Set<Long> eventIds);

    void incrementConfirmedCountByEventId(Long eventId);

    void decrementConfirmedCountByEventId(Long eventId);

    void incrementConfirmedRequestsByCount(Long eventId, Long count);

    void decrementConfirmedRequestsByCount(Long eventId, Long count);

    EventFullDto getEventById(@Positive Long id);
}
