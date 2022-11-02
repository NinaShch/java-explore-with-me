package ru.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.practicum.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class ForbiddenException extends RuntimeException {
    private final List<String> errors;
    private final String status;
    private final String reason;
    private final String timestamp;

    public ForbiddenException(String message) {
        super(message);
        errors = new ArrayList<>();
        errors.add(Arrays.toString(this.getStackTrace()));
        status = HttpStatus.FORBIDDEN.toString();
        timestamp = DateTimeUtils.getDateTime(LocalDateTime.now());
        reason = "For the requested operation the conditions are not met.";
    }

    public ForbiddenException(String message, Throwable e) {
        super(message, e);
        errors = new ArrayList<>();
        errors.add(Arrays.toString(this.getStackTrace()));
        status = HttpStatus.FORBIDDEN.toString();
        timestamp = DateTimeUtils.getDateTime(LocalDateTime.now());
        reason = "For the requested operation the conditions are not met.";
    }
}
