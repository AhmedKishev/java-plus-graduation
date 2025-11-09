package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.EventClient;
import ru.practicum.client.UserClient;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.dto.UserDto;
import ru.practicum.enums.ParticipationRequestStatus;
import ru.practicum.enums.State;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.repository.RequestRepository;
import ru.practicum.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.request.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestService {

    RequestRepository requestRepository;

    UserClient userClient;
    EventClient eventClient;
    // ЗАЯВКИ ТЕКУЩЕГО ПОЛЬЗОВАТЕЛЯ

    // Добавление запроса от текущего пользователя на участие в событии
    @Transactional(readOnly = false)
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        UserDto requester = userClient.findById(userId);
        EventFullDto event = eventClient.getEventByIdRequest(eventId);

        // нельзя добавить повторный запрос (Ожидается код ошибки 409)
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("User tries to make duplicate request", "Forbidden action");
        }

        // инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
        if (Objects.equals(requester.getId(), event.getInitiator().getId())) {
            throw new ConflictException("User tries to request for his own event", "Forbidden action");
        }

        // нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("User tries to request for non-published event", "Forbidden action");
        }

        // если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
        long confirmedRequestCount = requestRepository.countByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED);
        if (event.getParticipantLimit() > 0 && confirmedRequestCount >= event.getParticipantLimit()) {
            throw new ConflictException("Participants limit is already reached", "Forbidden action");
        }

        // если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
        ParticipationRequestStatus newRequestStatus = ParticipationRequestStatus.PENDING;
        if (!event.getRequestModeration()) newRequestStatus = ParticipationRequestStatus.CONFIRMED;
        if (Objects.equals(event.getParticipantLimit(), 0L)) newRequestStatus = ParticipationRequestStatus.CONFIRMED;

        Request newRequest = Request.builder()
                .requesterId(requester.getId())
                .eventId(event.getId())
                .status(newRequestStatus)
                .created(LocalDateTime.now())
                .build();

        if (newRequestStatus == ParticipationRequestStatus.CONFIRMED) {
            eventClient.incrementConfirmedCountByEventId(eventId);
        }

        requestRepository.save(newRequest);
        return RequestMapper.toDto(newRequest);
    }

    // Отмена своего запроса на участие в событии
    @Transactional(readOnly = false)
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userClient.findById(userId);
        Request existingRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));


        existingRequest.setStatus(ParticipationRequestStatus.CANCELED);
        requestRepository.save(existingRequest);
        return RequestMapper.toDto(existingRequest);
    }

    // Получение информации о заявках текущего пользователя на участие в чужих событиях
    public Collection<ParticipationRequestDto> findRequesterRequests(Long userId) {
        return requestRepository.findByRequesterId(userId).stream()
                .filter(Objects::nonNull)
                .map(RequestMapper::toDto)
                .toList();
    }

    // ЗАЯВКИ НА КОНКРЕТНОЕ СОБЫТИЕ

    // Получение информации о запросах на участие в событии текущего пользователя
    public Collection<ParticipationRequestDto> findEventRequests(Long userId, Long eventId) {
        UserDto initiator = userClient.findById(userId);
        EventFullDto event = eventClient.findById(eventId);

        // проверка что юзер - инициатор события
        if (!Objects.equals(initiator.getId(), event.getInitiator().getId())) {
            throw new ConflictException("User " + userId + " is not an initiator of event " + eventId, "Forbidden action");
        }

        return requestRepository.findByEventId(eventId).stream()
                .filter(Objects::nonNull)
                .map(RequestMapper::toDto)
                .toList();
    }

    // Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
    @Transactional(readOnly = false)
    public EventRequestStatusUpdateResultDto moderateRequest(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequestDto updateRequestDto
    ) {
        UserDto initiator = userClient.findById(userId);
        EventFullDto event = eventClient.findById(eventId);

        // проверка что юзер - инициатор события
        if (!Objects.equals(initiator.getId(), event.getInitiator().getId())) {
            throw new ConflictException("User " + userId + " is not an initiator of event " + eventId, "Forbidden action");
        }

        // если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        if (event.getParticipantLimit() < 1 || !event.getRequestModeration()) {
            return new EventRequestStatusUpdateResultDto();
        }

        // статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
        List<Request> requests = requestRepository.findAllById(updateRequestDto.getRequestIds());
        for (Request request : requests) {
            if (request.getStatus() != ParticipationRequestStatus.PENDING) {
                throw new ConflictException("Request " + request.getId() + " must have status PENDING", "Incorrectly made request");
            }
        }

        List<Long> requestsToConfirm = new ArrayList<>();
        List<Long> requestsToReject = new ArrayList<>();

        if (updateRequestDto.getStatus() == ParticipationRequestStatus.CONFIRMED) {

            long confirmedRequestCount = requestRepository.countByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED);

            if (confirmedRequestCount >= event.getParticipantLimit()) {
                // нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
                throw new ConflictException("The participant limit has been reached for event " + eventId, "Forbidden action");
            } else if (updateRequestDto.getRequestIds().size() < event.getParticipantLimit() - confirmedRequestCount) {
                requestsToConfirm = updateRequestDto.getRequestIds();
                requestRepository.updateStatusByIds(requestsToConfirm, ParticipationRequestStatus.CONFIRMED);
            } else {
                long freeSeats = event.getParticipantLimit() - confirmedRequestCount;
                requestsToConfirm = updateRequestDto.getRequestIds().stream()
                        .limit(freeSeats)
                        .toList();
                requestsToReject = updateRequestDto.getRequestIds().stream()
                        .skip(freeSeats)
                        .toList();
                requestRepository.updateStatusByIds(requestsToConfirm, ParticipationRequestStatus.CONFIRMED);

                eventClient.incrementConfirmedRequestsByCount(eventId, (long) requestsToConfirm.size());
                // если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
                requestRepository.setStatusToRejectForAllPending(eventId);
            }

        } else if (updateRequestDto.getStatus() == ParticipationRequestStatus.REJECTED) {
            requestsToReject = updateRequestDto.getRequestIds();
            long confirmedRejected = requests.stream()
                    .filter(r -> r.getStatus() == ParticipationRequestStatus.CONFIRMED)
                    .count();
            if (confirmedRejected > 0) {
                eventClient.decrementConfirmedRequestsByCount(eventId, confirmedRejected);
            }
            requestRepository.updateStatusByIds(requestsToReject, ParticipationRequestStatus.REJECTED);
        } else {
            throw new ConflictException("Only CONFIRMED and REJECTED statuses are allowed", "Forbidden action");
        }

        EventRequestStatusUpdateResultDto resultDto = new EventRequestStatusUpdateResultDto();
        List<ParticipationRequestDto> confirmedRequests = requestRepository.findAllById(requestsToConfirm).stream()
                .map(RequestMapper::toDto)
                .toList();
        resultDto.setConfirmedRequests(confirmedRequests);
        List<ParticipationRequestDto> rejectedRequests = requestRepository.findAllById(requestsToReject).stream()
                .map(RequestMapper::toDto)
                .toList();
        resultDto.setRejectedRequests(rejectedRequests);
        return resultDto;
    }

    public Long countByEventIdAndStatus(Long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED);
    }

    public Map<Long, Long> getConfrimedRequestsByEventIds(List<Long> ids) {
        return requestRepository.getConfirmedRequestsByEventIds(ids)
                .stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1]
                ));
    }
}
