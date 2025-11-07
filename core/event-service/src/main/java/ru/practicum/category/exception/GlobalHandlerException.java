package ru.practicum.category.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.NotFoundException;
import ru.practicum.handler.ErrorResponse;

import java.time.Instant;


@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse notFound(final NotFoundException e) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND)
                .error("Not Found")
                .message(e.getMessage())
                .build();
    }

}
