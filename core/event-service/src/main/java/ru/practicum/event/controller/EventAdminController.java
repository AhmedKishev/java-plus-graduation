package ru.practicum.event.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventAdminParams;
import ru.practicum.dto.EventCommentDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.UpdateEventDto;
import ru.practicum.enums.State;
import ru.practicum.event.service.EventAdminService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventAdminController {

    EventAdminService eventAdminService;

    // Поиск событий
    @GetMapping
    Collection<EventFullDto> getAllEventsByParams(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<State> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(defaultValue = "10") @Positive Long size
    ) {
        EventAdminParams params = EventAdminParams.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();

        log.info("Calling to endpoint /admin/events GetMapping for params: " + params.toString());
        return eventAdminService.getAllEventsByParams(params);
    }


    // Редактирование данных события и его статуса (отклонение/публикация).
    @PatchMapping("/{eventId}")
    EventFullDto updateEventByAdmin(
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventDto updateEventDto
    ) {
        log.info("Calling to endpoint /admin/events/{eventId} PatchMapping for eventId: " + eventId + "."
                + " UpdateEvent: " + updateEventDto.toString());
        return eventAdminService.updateEventByAdmin(eventId, updateEventDto);
    }

    @GetMapping("/{id}")
    public EventFullDto findById(@PathVariable("id") @Positive Long id) {
        return eventAdminService.getAdminEventById(id);
    }


    @GetMapping("/request/{eventId}")
    EventFullDto getEventByIdRequest(@PathVariable("eventId") @Positive Long id) {
        return eventAdminService.getEventById(id);
    }

    @GetMapping("/comment/{id}")
    public EventCommentDto getEventByComment(@PathVariable("id") @Positive Long id) {
        return eventAdminService.getEventByComment(id);
    }

    @GetMapping("/comments")
    public Map<Long, EventCommentDto> getEventCommentsByIds(@RequestParam Set<Long> eventIds) {
        return eventAdminService.getEventsByIds(eventIds);
    }

    @PutMapping("/inc/{eventId}")
    public void incrementConfirmedCountByEventId(@PathVariable Long eventId) {
        eventAdminService.incrementConfirmedCountByEventId(eventId);
    }


    @PutMapping("/dec/{eventId}")
    public void decrementConfirmedCountByEventId(@PathVariable Long eventId) {
        eventAdminService.decrementConfirmedCountByEventId(eventId);
    }

    @PutMapping("/inc/count/{eventId}")
    public void incrementConfirmedRequestsByCount(@PathVariable Long eventId,
                                                  @RequestParam Long count) {
        eventAdminService.incrementConfirmedRequestsByCount(eventId, count);
    }

    @PutMapping("/dec/count/{eventId}")
    public void decrementConfirmedRequestsByCount(@PathVariable Long eventId,
                                                  @RequestParam Long count) {
        eventAdminService.decrementConfirmedRequestsByCount(eventId, count);
    }

}
