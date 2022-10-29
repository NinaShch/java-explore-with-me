package ru.practicum.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private final String status;
    private final String reason;
    private final String timestamp;

    public BadRequestException(String message, String reason) {
        super(message);
        status = "BAD_REQUEST";
        timestamp = DateTimeConverter.getDateTimeNow();
        this.reason = reason;
    }
}
