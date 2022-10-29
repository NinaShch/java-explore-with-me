package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class EventShortDto {
    @NotBlank
    String annotation;
    @NotNull
    CategoryDto category;
    int confirmedRequests;
    @NotBlank
    String eventDate;
    Long id;
    @NotNull
    UserShortDto initiator;
    @NotNull
    boolean paid;
    @NotBlank
    String title;
    Long views;
}
