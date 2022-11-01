package ru.practicum.exception;

import lombok.Getter;
import ru.practicum.DateTimeUtils;

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
        status = "FORBIDDEN";
        timestamp = DateTimeUtils.getDateTimeNow();
        reason = "For the requested operation the conditions are not met.";
    }
}
