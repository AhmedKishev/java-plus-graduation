package ru.practicum.client;

import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventCommentDto;
import ru.practicum.dto.EventFullDto;

import java.util.Map;
import java.util.Set;

@FeignClient(name = "event-service")
public interface EventClient {


    @GetMapping("/admin/events/{id}")
    public EventFullDto findById(@PathVariable("id") @Positive Long id);

    @GetMapping("/admin/events/comment/{id}")
    public EventCommentDto getEventByComment(@PathVariable("id") @Positive Long id);

    @GetMapping("/admin/events/comments")
    public Map<Long, EventCommentDto> getEventCommentsByIds(@RequestParam Set<Long> eventIds);


    @GetMapping("/admin/events/request/{eventId}")
    EventFullDto getEventByIdRequest(@PathVariable("eventId") @Positive Long id);

    @PutMapping("/admin/events/inc/{eventId}")
    public void incrementConfirmedCountByEventId(@PathVariable Long eventId);

    @PutMapping("/admin/events/inc/count/{eventId}")
    public void incrementConfirmedRequestsByCount(@PathVariable Long eventId,
                                                  @RequestParam Long count);
    @PutMapping("/admin/events/dec/count/{eventId}")
    public void decrementConfirmedRequestsByCount(@PathVariable Long eventId,
                                                  @RequestParam Long count);
}
