package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdateEventDto {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Long eventId;
    private boolean paid;
    private int participantLimit;
    private String title;
}
