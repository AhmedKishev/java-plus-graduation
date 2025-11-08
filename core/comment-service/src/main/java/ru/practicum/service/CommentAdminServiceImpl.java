package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.EventClient;
import ru.practicum.client.UserClient;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.EventCommentDto;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class CommentAdminServiceImpl implements CommentAdminService {

    CommentRepository repository;
    UserClient userClient;
    EventClient eventClient;

    @Override
    public void delete(Long comId) {
        log.info("admin delete - invoked");
        if (!repository.existsById(comId)) {
            log.error("User with id = {} not exist", comId);
            throw new NotFoundException(String.format("Comment with id %d not found", comId));
        }
        log.info("Result: comment with id = {} deleted", comId);
        repository.deleteById(comId);
    }

    @Override
    public List<CommentDto> search(String text, int from, int size) {
        log.info("admin search - invoked");
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Comment> page = repository.findAllByText(text, pageable);
        List<Comment> list = page.getContent();
        log.info("Result: list of comments size = {} ", list.size());

        Set<Long> userIds = list.stream()
                .map(Comment::getAuthorId)
                .collect(Collectors.toSet());
        Set<Long> eventIds = list.stream()
                .map(Comment::getEventId)
                .collect(Collectors.toSet());


        Map<Long, UserDto> usersMap = userClient.findAllByIds(userIds);
        Map<Long, EventCommentDto> eventsMap = eventClient.getEventCommentsByIds(eventIds);

        return CommentMapper.toListCommentDto(list, usersMap, eventsMap);
    }

    @Override
    public List<CommentDto> findAllByUserId(Long userId, int from, int size) {
        log.info("admin findAllByUserId - invoked");
        userClient.findById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Comment> page = repository.findAllByAuthorId(userId, pageable);
        List<Comment> list = page.getContent();
        log.info("Result: list of comments size = {} ", list.size());

        Set<Long> userIds = list.stream()
                .map(Comment::getAuthorId)
                .collect(Collectors.toSet());
        Set<Long> eventIds = list.stream()
                .map(Comment::getEventId)
                .collect(Collectors.toSet());

        Map<Long, UserDto> usersMap = userClient.findAllByIds(userIds);
        Map<Long, EventCommentDto> eventsMap = eventClient.getEventCommentsByIds(eventIds);


        return CommentMapper.toListCommentDto(list, usersMap, eventsMap);
    }

    @Override
    public CommentDto approveComment(Long comId) {
        log.info("approveComment - invoked");
        Comment comment = repository.findById(comId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id %d not found", comId)));
        comment.setApproved(true);
        repository.save(comment);
        log.info("Result: comment with id = {} approved", comId);
        UserDto userDto = userClient.findById(comment.getAuthorId());

        EventCommentDto eventCommentDto = eventClient.getEventByComment(comment.getEventId());

        return CommentMapper.toCommentDto(comment, userDto, eventCommentDto);
    }

    @Override
    public CommentDto rejectComment(Long comId) {
        log.info("rejectComment - invoked");
        Comment comment = repository.findById(comId).orElseThrow(() -> new NotFoundException(String.format("Comment with id %d not found", comId)));
        comment.setApproved(false);
        repository.save(comment);
        log.info("Result: comment with id = {} rejected", comId);

        UserDto userDto = userClient.findById(comment.getAuthorId());

        EventCommentDto eventCommentDto = eventClient.getEventByComment(comment.getEventId());

        return CommentMapper.toCommentDto(comment, userDto, eventCommentDto);
    }
}