package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.*;
import ru.practicum.model.Comment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapper {

    public Comment toComment(CommentCreateDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .build();
    }

    public CommentDto toCommentDto(Comment comment, UserDto author, EventCommentDto commentDto) {
        return CommentDto.builder()
                .id(comment.getId())
                .author(author)
                .event(commentDto)
                .createTime(comment.getCreateTime())
                .text(comment.getText())
                .approved(comment.getApproved())
                .build();
    }

    public List<CommentDto> toListCommentDto(List<Comment> list,
                                             Map<Long, UserDto> usersMap,
                                             Map<Long, EventCommentDto> eventsMap) {
        return list.stream()
                .map(comment -> toCommentDto(
                        comment,
                        usersMap.get(comment.getAuthorId()),
                        eventsMap.get(comment.getEventId())
                ))
                .collect(Collectors.toList());
    }

    public CommentShortDto toCommentShortDto(Comment comment, Map<Long, UserDto> users) {
        UserDto authorDto = users.get(comment.getAuthorId());

        return CommentShortDto.builder()
                .author(authorDto)
                .createTime(comment.getText())
                .id(comment.getId())
                .text(comment.getText())
                .build();
    }

    public List<CommentShortDto> toListCommentShortDto(List<Comment> list,
                                                       Map<Long, UserDto> users) {
        return list.stream()
                .map(comment -> toCommentShortDto(comment, users))
                .collect(Collectors.toList());
    }
}