package ru.practicum.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ForbiddenException extends RuntimeException {
    private final List<String> errors;
    private final String status;
    private final String reason;
    private final String timestamp;

    public ForbiddenException(String message, String reason) {
        super(message);
        errors = new ArrayList<>();
        status = "FORBIDDEN";
        timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
        this.reason = reason;
    }
}
