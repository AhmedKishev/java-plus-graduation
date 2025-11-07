package ru.practicum.event.mapper;


import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.dto.*;
import ru.practicum.enums.State;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;

public class EventMapper {


    public static Event toEvent(
            NewEventDto newEventDto,
            Long initiatorId,
            Category category
    ) {
        return Event.builder()
                .initiatorId(initiatorId)
                .category(category)
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .state(State.PENDING)
                .location(LocationMapper.toEntity(newEventDto.getLocation()))
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .paid(newEventDto.getPaid())
                .eventDate(newEventDto.getEventDate())
                .createdOn(LocalDateTime.now())
                .build();
    }


    public static EventFullDto toEventFullDto(
            Event event,
            UserShortDto byId, Long confirmedRequests,
            Long views
    ) {
        if (confirmedRequests == null) confirmedRequests = 0L;
        return EventFullDto.builder()
                .id(event.getId())
                .initiator(byId)
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .state(event.getState())
                .location(LocationMapper.toDto(event.getLocation()))
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .publishedOn(event.getPublishedOn())
                .createdOn(event.getCreatedOn())
                .confirmedRequests(confirmedRequests)
                .views(views)
                .build();
    }

    public static EventShortDto toEventShortDto(
            Event event,
            UserShortDto byIdShort, Long confirmedRequests,
            Long views
    ) {
        if (confirmedRequests == null) confirmedRequests = 0L;
        return EventShortDto.builder()
                .id(event.getId())
                .initiator(byIdShort)
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .confirmedRequests(confirmedRequests)
                .views(views)
                .build();
    }

    public static EventCommentDto toEventComment(Event event) {
        return EventCommentDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .build();
    }
}
