package ru.practicum.event.service;



import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventDto;

import java.util.List;

public interface EventPrivateService {

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    List<EventShortDto> getEventsByUserId(Long userId, Long from, Long size);

    EventFullDto updateEventByUserIdAndEventId(Long userId, Long eventId, UpdateEventDto newEventDto);

}
