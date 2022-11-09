package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("Exception", e);
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
        log.error("Exception", e);
        return new ErrorResponseWithErrorsList(
                e.getErrors(),
                e.getStatus(),
                e.getReason(),
                e.getMessage(),
                e.getTimestamp()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException e) {
        log.error("Exception", e);
        return new ErrorResponse(
                e.getStatus(),
                e.getReason(),
                e.getMessage(),
                e.getTimestamp()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerErrorException(final InternalServerErrorException e) {
        log.error("Exception", e);
        return new ErrorResponse(
                e.getStatus(),
                e.getReason(),
                e.getMessage(),
                e.getTimestamp()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseWithErrorsList handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("Exception", e);
        List<String> errors = new ArrayList<>();
        errors.add(Arrays.toString(e.getStackTrace()));
        return new ErrorResponseWithErrorsList(
                errors,
                HttpStatus.BAD_REQUEST.toString(),
                "Validation Exception",
                e.getMessage(),
                DateTimeUtils.getDateTime(LocalDateTime.now())
        );
    }
}
