package ru.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseWithErrorsList {
    private List<String> errors;
    private String status;
    private String reason;
    private String message;
    private String timestamp;
}
