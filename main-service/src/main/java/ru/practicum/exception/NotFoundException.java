package ru.practicum.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String status;
    private final String reason;
    private final String timestamp;

    public NotFoundException(String message) {
        super(message);
        status = "NOT_FOUND";
        reason = "The required object was not found.";
        timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }
}
