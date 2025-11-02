package ru.practicum.ewm.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.EventHitDto;
import ru.practicum.EventStatsResponseDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@FeignClient(name = "stats-server", path = "/")
public interface StatClient {

    @PostMapping("/hit")
    void hit(@RequestBody EventHitDto eventHitDto);

    @GetMapping("/stats")
    Collection<EventStatsResponseDto> stats(@RequestParam("start") LocalDateTime start,
                                            @RequestParam("end") LocalDateTime end,
                                            @RequestParam(value = "uris", required = false) List<String> uris,
                                            @RequestParam(value = "unique", required = false) Boolean unique);
}