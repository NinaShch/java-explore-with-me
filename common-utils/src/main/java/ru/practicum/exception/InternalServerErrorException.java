package ru.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.practicum.DateTimeUtils;

import java.time.LocalDateTime;

@Getter
public class InternalServerErrorException extends RuntimeException {

    private final String status;
    private final String reason;
    private final String timestamp;

    public InternalServerErrorException(String message) {
        super(message);
        status = HttpStatus.INTERNAL_SERVER_ERROR.toString();
        reason = "Error occurred";
        timestamp = DateTimeUtils.getDateTime(LocalDateTime.now());
    }

    public InternalServerErrorException(String message, Throwable e) {
        super(message, e);
        status = HttpStatus.INTERNAL_SERVER_ERROR.toString();
        reason = "Error occurred";
        timestamp = DateTimeUtils.getDateTime(LocalDateTime.now());
    }
}
