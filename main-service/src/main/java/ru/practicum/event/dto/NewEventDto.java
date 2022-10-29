package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.event.entity.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class NewEventDto {
    @NotBlank
    private String annotation;
    @NotNull
    private Long category;
    @NotBlank
    private String description;
    @NotBlank
    private String eventDate;
    @NotNull
    private Location location;
    @NotNull
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    @NotBlank
    private String title;
}
