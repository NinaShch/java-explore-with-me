package ru.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.practicum.DateTimeUtils;

import java.time.LocalDateTime;

@Getter
public class NotFoundException extends RuntimeException {
    private final String status;
    private final String reason;
    private final String timestamp;

    public NotFoundException(String message) {
        super(message);
        status = HttpStatus.NOT_FOUND.toString();
        reason = "The required object was not found.";
        timestamp = DateTimeUtils.getDateTime(LocalDateTime.now());
    }

    public NotFoundException(String message, Throwable e) {
        super(message, e);
        status = HttpStatus.NOT_FOUND.toString();
        reason = "The required object was not found.";
        timestamp = DateTimeUtils.getDateTime(LocalDateTime.now());
    }
}
