package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.EventClient;
import ru.practicum.client.UserClient;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CommentShortDto;
import ru.practicum.dto.EventCommentDto;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;
import ru.practicum.util.Util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentPublicServiceImpl implements CommentPublicService {

    CommentRepository repository;
    EventClient eventClient;
    UserClient userClient;

    @Override
    public CommentDto getComment(Long comId) {
        log.info("getComment - invoked");
        Comment comment = repository.findById(comId)
                .orElseThrow(() -> {
                    log.error("Comment with id = {} - not exist", comId);
                    return new NotFoundException(String.format("Comment with id %d not found", comId));
                });
        if (!comment.isApproved()) {
            log.warn("Comment with id = {} is not approved", comId);
            throw new ForbiddenException(String.format("Comment with id %d is not approved", comment.getId()));
        }
        log.info("Result: comment with id= {}", comId);

        UserDto userDto = userClient.findById(comment.getAuthorId());

        EventCommentDto eventCommentDto = eventClient.getEventByComment(comment.getEventId());

        return CommentMapper.toCommentDto(comment, userDto, eventCommentDto);
    }

    @Override
    public List<CommentShortDto> getCommentsByEvent(Long eventId, int from, int size) {
        log.info("getCommentsByEvent - invoked");
        eventClient.findById(eventId);
        Pageable pageable = Util.createPageRequestAsc("createTime", from, size);
        Page<Comment> commentsPage = repository.findAllByEventId(eventId, pageable);
        List<Comment> comments = commentsPage.getContent();
        List<Comment> approvedComments = comments.stream()
                .filter(Comment::isApproved)
                .collect(Collectors.toList());
        log.info("Result : list of approved comments size = {}", approvedComments.size());

        Set<Long> userIds = comments.stream()
                .map(Comment::getAuthorId)
                .collect(Collectors.toSet());

        Map<Long, UserDto> users = userClient.findAllByIds(userIds);

        return CommentMapper.toListCommentShortDto(approvedComments, users);
    }

    @Override
    public CommentDto getCommentByEventAndCommentId(Long eventId, Long commentId) {
        log.info("getCommentByEventAndCommentId - invoked");
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("Comment with id = {} does not exist", commentId);
                    return new NotFoundException("Comment not found");
                });
        if (!comment.getEventId().equals(eventId)) {
            log.error("Comment with id = {} does not belong to event with id = {}", commentId, eventId);
            throw new NotFoundException(String.format("Comment with id %d not found for the specified event", comment.getId()));
        }
        if (!comment.isApproved()) {
            log.warn("Comment with id = {} is not approved", commentId);
            throw new ForbiddenException(String.format("Comment with id %d is not approved", comment.getId()));
        }
        log.info("Result: comment with eventId= {} and commentId= {}", eventId, commentId);

        UserDto userDto = userClient.findById(comment.getAuthorId());

        EventCommentDto eventCommentDto = eventClient.getEventByComment(comment.getEventId());

        return CommentMapper.toCommentDto(comment, userDto, eventCommentDto);
    }
}