package ru.practicum.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.event.mapper.EventMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation, Map<Long, UserShortDto> ids) {
        List<EventShortDto> eventShortDtoList = compilation.getEvents().stream()
                .map(event ->
                        EventMapper.toEventShortDto(event, 0d, toUserDto(ids.get(event.getInitiatorId())))
                ).collect(Collectors.toList());
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(eventShortDtoList)
                .build();
    }

    private UserDto toUserDto(UserShortDto userShortDto) {
        return UserDto.builder()
                .id(userShortDto.getId())
                .name(userShortDto.getName())
                .build();
    }

}