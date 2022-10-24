package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.event.model.Location;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class NewEventDto {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Location location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private String title;
}
