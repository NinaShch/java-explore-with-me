package ru.practicum.exception;

import lombok.Getter;

@Getter
public class InternalServerErrorException extends RuntimeException {

    private final String status;
    private final String reason;
    private final String timestamp;

    public InternalServerErrorException(String message) {
        super(message);
        status = "INTERNAL_SERVER_ERROR";
        reason = "Error occurred";
        timestamp = DateTimeConverter.getDateTimeNow();
    }
}
