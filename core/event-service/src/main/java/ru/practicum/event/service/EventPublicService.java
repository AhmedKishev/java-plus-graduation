package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventParams;
import ru.practicum.dto.EventShortDto;

import java.util.List;

public interface EventPublicService {

    List<EventShortDto> getAllEventsByParams(EventParams eventParams, HttpServletRequest request);

    EventFullDto getEventById(Long id, long request);

    List<EventShortDto> getEventsRecommendations(long userId, int maxResults);

    void putLikeForEvent(long userId, @Positive Long eventId);
}
