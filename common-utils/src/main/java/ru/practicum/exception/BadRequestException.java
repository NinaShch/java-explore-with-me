package ru.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.practicum.DateTimeUtils;

import java.time.LocalDateTime;


@Getter
public class BadRequestException extends RuntimeException {
    private final String status;
    private final String reason;
    private final String timestamp;

    public BadRequestException(String message, String reason) {
        super(message);
        status = HttpStatus.BAD_REQUEST.toString();
        timestamp = DateTimeUtils.getDateTime(LocalDateTime.now());
        this.reason = reason;
    }

    public BadRequestException(String message, String reason, Throwable e) {
        super(message, e);
        status = HttpStatus.BAD_REQUEST.toString();
        timestamp = DateTimeUtils.getDateTime(LocalDateTime.now());
        this.reason = reason;
    }
}
