package ru.practicum.mapper;


import ru.practicum.model.Request;
import ru.practicum.request.ParticipationRequestDto;

public class RequestMapper {

    public static ParticipationRequestDto toDto(Request request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setRequester(request.getRequesterId());
        dto.setEvent(request.getEventId());
        dto.setStatus(request.getStatus());
        dto.setCreated(request.getCreated());
        return dto;
    }

    public static Request toEntity(ParticipationRequestDto dto, Long requesterId, Long eventId) {
        Request request = new Request();
        request.setId(dto.getId());
        request.setRequesterId(requesterId);
        request.setEventId(eventId);
        request.setStatus(dto.getStatus());
        request.setCreated(dto.getCreated());
        return request;
    }

}
