package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(
                e.getStatus(),
                e.getReason(),
                e.getMessage(),
                e.getTimestamp()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseWithErrorsList handleForbiddenException(final ForbiddenException e) {
        log.warn(e.getMessage());
        return new ErrorResponseWithErrorsList(
                e.getErrors(),
                e.getStatus(),
                e.getReason(),
                e.getMessage(),
                e.getTimestamp()
        );
    }
}
