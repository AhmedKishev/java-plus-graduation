package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.RequestClient;
import ru.practicum.client.UserClient;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventParams;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.UserDto;
import ru.practicum.enums.EventSort;
import ru.practicum.enums.State;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.View;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.JpaSpecifications;
import ru.practicum.event.repository.ViewRepository;
import ru.practicum.ewm.RecommendationsClient;
import ru.practicum.ewm.UserActionClient;
import ru.practicum.ewm.grpc.stats.event.ActionTypeProto;
import ru.practicum.ewm.grpc.stats.event.RecommendedEventProto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class EventPublicServiceImpl implements EventPublicService {

    EventRepository eventRepository;
    ViewRepository viewRepository;
    RequestClient requestClient;
    UserClient userClient;
    UserActionClient userActionClient;
    RecommendationsClient recommendationsClient;

    // Получение событий с возможностью фильтрации
    @Override
    public List<EventShortDto> getAllEventsByParams(EventParams params, HttpServletRequest request) {

        if (params.getRangeStart() != null && params.getRangeEnd() != null && params.getRangeEnd().isBefore(params.getRangeStart())) {
            throw new BadRequestException("rangeStart should be before rangeEnd");
        }

        // если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
        if (params.getRangeStart() == null) params.setRangeStart(LocalDateTime.now());

        // сортировочка и пагинация
        Sort sort = Sort.by(Sort.Direction.ASC, "eventDate");
        if (EventSort.VIEWS.equals(params.getEventSort())) sort = Sort.by(Sort.Direction.DESC, "views");
        PageRequest pageRequest = PageRequest.of(params.getFrom().intValue() / params.getSize().intValue(),
                params.getSize().intValue(), sort);

        Page<Event> events = eventRepository.findAll(JpaSpecifications.publicFilters(params), pageRequest);
        List<Long> eventIds = events.stream().map(Event::getId).toList();

        // информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
        Map<Long, Long> confirmedRequestsMap = requestClient.getConfirmedRequestsByEventIds(eventIds);
        Map<Long, Long> viewsMap = viewRepository.countsByEventIds(eventIds)
                .stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1]
                ));

        // информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики


        return events.stream()
                .map(e -> EventMapper.toEventShortDto(e, 0d, userClient.findById(e.getInitiatorId())))
                .toList();
    }

    // Получение подробной информации об опубликованном событии по его идентификатору
    @Override
    @Transactional(readOnly = false)
    public EventFullDto getEventById(Long eventId, long userId) {
        // событие должно быть опубликовано
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        // информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
        Long confirmedRequests = requestClient.countByEventIdAndStatus(eventId);
        Long views = viewRepository.countByEventId(eventId);

        // делаем новый уникальный просмотр
        if (!viewRepository.existsByEventIdAndUserId(eventId, userId)) {
            View view = View.builder()
                    .event(event)
                    .userId(userId)
                    .build();
            viewRepository.save(view);
        }

        userActionClient.collectUserAction(eventId, userId, ActionTypeProto.ACTION_VIEW, Instant.now());

        return EventMapper.toEventFullDto(event, userClient.findByIdShort(event.getInitiatorId()), confirmedRequests, 0d);
    }

    @Override
    public List<EventShortDto> getEventsRecommendations(long userId, int maxResults) {
        Map<Long, Double> recommendations = recommendationsClient
                .getRecommendationsForUser(userId, maxResults)
                .collect(Collectors.toMap(RecommendedEventProto::getEventId, RecommendedEventProto::getScore));

        List<Event> events = eventRepository.findAllById(recommendations.keySet());
        List<Long> initiatorIds = events.stream()
                .map(Event::getInitiatorId)
                .toList();

        Map<Long, UserDto> initiators = userClient.getAllUsers(initiatorIds, 0, initiatorIds.size()).stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));
        return events.stream()
                .map(event -> EventMapper.toEventShortDto(event, recommendations.get(event.getId()),
                        initiators.get(event.getInitiatorId())))
                .toList();
    }
}