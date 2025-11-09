package ru.practicum.service;

import ru.practicum.dto.CommentCreateDto;
import ru.practicum.dto.CommentDto;

public interface CommentPrivateService {

    CommentDto createComment(Long userId, Long eventId, CommentCreateDto commentDto);

    void deleteComment(Long userId, Long comId);

    CommentDto patchComment(Long userId, Long comId, CommentCreateDto commentCreateDto);
}