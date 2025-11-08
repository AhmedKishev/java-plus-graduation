package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.EventClient;
import ru.practicum.client.UserClient;
import ru.practicum.dto.*;
import ru.practicum.enums.State;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class CommentPrivateServiceImpl implements CommentPrivateService {

    CommentRepository repository;
    UserClient userClient;
    EventClient eventClient;

    @Override
    public CommentDto createComment(Long userId, Long eventId, CommentCreateDto commentDto) {
        log.info("createComment - invoked");
        Comment comment = CommentMapper.toComment(commentDto);
        UserDto author = userClient.findById(userId);

        EventFullDto event = eventClient.findById(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            log.error("Event state = {} - should be PUBLISHED", event.getState());
            throw new ConflictException(String.format("Event with id %d not published you cant comment it", eventId));
        }
        comment.setAuthorId(author.getId());
        comment.setEventId(event.getId());
        comment.setApproved(true);   // по умолчанию комменты видны, но админ может удалить/вернуть
        comment.setCreateTime(LocalDateTime.now().withNano(0));
        log.info("Result: new comment created");


        EventCommentDto eventCommentDto = eventClient.getEventByComment(comment.getEventId());

        return CommentMapper.toCommentDto(repository.save(comment), author, eventCommentDto);
    }

    @Override
    public void deleteComment(Long userId, Long comId) {
        log.info("deleteComment - invoked");
        Comment comment = repository.findById(comId)
                .orElseThrow(() -> {
                    log.error("Comment with id = {} - not exist", comId);
                    return new NotFoundException(String.format("Comment with id %d not found", comId));
                });
        if (!comment.getAuthorId().equals(userId)) {
            log.error("Unauthorized access by user");
            throw new ConflictException(String.format("you didn't write comment with id %d and can't delete it", comId));
        }
        log.info("Result: comment with id = {} - deleted", comId);
        repository.deleteById(comId);
    }

    @Override
    public CommentDto patchComment(Long userId, Long comId, CommentCreateDto commentCreateDto) {
        log.info("patchComment - invoked");
        Comment comment = repository.findById(comId)
                .orElseThrow(() -> {
                    log.error("Comment with id = {} - not exist", comId);
                    return new NotFoundException(String.format("Comment with id %d not found", comId));
                });
        if (!comment.getAuthorId().equals(userId)) {
            log.error("Unauthorized access by user");
            throw new ConflictException(String.format("you didn't write comment with id %d and can't patch it", comId));
        }
        comment.setText(commentCreateDto.getText());
        comment.setPatchTime(LocalDateTime.now().withNano(0));
        log.info("Result: comment with id = {} - updated", comId);

        UserDto userDto = userClient.findById(comment.getAuthorId());

        EventCommentDto eventCommentDto = eventClient.getEventByComment(comment.getEventId());

        return CommentMapper.toCommentDto(comment, userDto, eventCommentDto);
    }
}