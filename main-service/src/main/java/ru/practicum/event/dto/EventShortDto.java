package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class EventShortDto {
    String annotation;
    CategoryDto category;
    int confirmedRequests;
    String eventDate;
    Long id;
    UserShortDto initiator;
    boolean paid;
    String title;
    Long views;
}
