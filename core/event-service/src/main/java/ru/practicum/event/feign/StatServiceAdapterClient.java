package ru.practicum.event.feign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.EventHitDto;
import ru.practicum.EventStatsResponseDto;
import ru.practicum.ewm.client.StatClient;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class StatServiceAdapterClient {

    private final StatClient statClient;

    public void saveHit(EventHitDto dto) {
        statClient.hit(dto);
    }

    public Collection<EventStatsResponseDto> getStats(LocalDateTime start,
                                                      LocalDateTime end,
                                                      List<String> uris,
                                                      Boolean unique) {
        try {
            return statClient.stats(start, end, uris, unique);
        } catch (Exception e) {
            log.warn("Failed to get stats: {}", e.getMessage());
        }
        return Collections.emptyList();
    }
}