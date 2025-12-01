package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.mapper.EventSimilarityMapper;
import ru.practicum.model.EventSimilarity;
import ru.practicum.repository.EventSimilarityRepository;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional
public class EventSimilarityServiceImpl implements EventSimilarityService {

    EventSimilarityRepository eventSimilarityRepository;

    @Override
    public void handle(EventSimilarityAvro eventSimilarityAvro) {

        if (!eventSimilarityRepository.existsByEventAAndEventB(eventSimilarityAvro.getEventA(), eventSimilarityAvro.getEventB())) {
            eventSimilarityRepository.save(EventSimilarityMapper.toEntity(eventSimilarityAvro));
        } else {
            EventSimilarity oldEventSimilarity = eventSimilarityRepository.findByEventAAndEventB(eventSimilarityAvro.getEventA(), eventSimilarityAvro.getEventB());
            oldEventSimilarity.setScore(eventSimilarityAvro.getScore());
            oldEventSimilarity.setTimestamp(eventSimilarityAvro.getTimestamp());
            eventSimilarityRepository.save(oldEventSimilarity);
        }
    }
}

