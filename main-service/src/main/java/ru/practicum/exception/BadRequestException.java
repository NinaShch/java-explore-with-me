package ru.practicum.exception;

import lombok.Getter;
import ru.practicum.DateTimeUtils;

@Getter
public class BadRequestException extends RuntimeException {
    private final String status;
    private final String reason;
    private final String timestamp;

    public BadRequestException(String message, String reason) {
        super(message);
        status = "BAD_REQUEST";
        timestamp = DateTimeUtils.getDateTimeNow();
        this.reason = reason;
    }

    public BadRequestException(String message, String reason, Throwable e) {
        super(message, e);
        status = "BAD_REQUEST";
        timestamp = DateTimeUtils.getDateTimeNow();
        this.reason = reason;
    }
}
