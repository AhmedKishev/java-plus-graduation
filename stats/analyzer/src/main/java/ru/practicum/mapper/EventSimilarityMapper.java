package ru.practicum.mapper;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.model.EventSimilarity;

public class EventSimilarityMapper {
    public static EventSimilarity toEntity(EventSimilarityAvro eventSimilarityAvro) {
        return EventSimilarity.builder()
                .eventA(eventSimilarityAvro.getEventA())
                .eventB(eventSimilarityAvro.getEventB())
                .score(eventSimilarityAvro.getScore())
                .timestamp(eventSimilarityAvro.getTimestamp())
                .build();
    }
}
