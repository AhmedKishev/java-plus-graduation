package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.UserClient;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)

@RequiredArgsConstructor
@Slf4j
public class CompilationPublicServiceImpl implements CompilationPublicService {

    private final CompilationRepository compilationRepository;

    private final UserClient userClient;

    @Override
    public CompilationDto readCompilationById(Long compId) {
        log.info("readCompilationById - invoked");
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation not found"));
        log.info("Result:  {}", compilation);

        List<Long> ids = compilation.getEvents()
                .stream()
                .map(Event::getInitiatorId)
                .toList();

        Map<Long, UserShortDto> users = userClient.findAllByIdsShort(ids);

        return CompilationMapper.toCompilationDto(compilation, users);
    }

    @Override
    public List<CompilationDto> readAllCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from, size, Sort.Direction.ASC, "id");
        List<Compilation> compilations;
        compilations = (pinned == null) ? compilationRepository.findAll(pageable).getContent() :
                compilationRepository.findAllByPinned(pinned, pageable);
        log.info("Result: {}", compilations);


        return compilations.stream()
                .map(compilation -> {

                    List<Long> ids = compilation.getEvents()
                            .stream()
                            .map(Event::getInitiatorId)
                            .toList();

                    Map<Long, UserShortDto> users = userClient.findAllByIdsShort(ids);

                    return CompilationMapper.toCompilationDto(compilation, users);
                })
                .collect(Collectors.toList());
    }


}