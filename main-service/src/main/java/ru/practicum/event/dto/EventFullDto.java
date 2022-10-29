package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.entity.Location;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class EventFullDto {
    @NotBlank
    @Length(max = 1024)
    private String annotation;
    @NotNull
    private CategoryDto category;
    private int confirmedRequests;
    private String createdOn;
    @Length(max = 2048)
    private String description;
    private String eventDate;
    private Long id;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private Location location;
    @NotNull
    private boolean paid;
    private int participantLimit;
    private String publishedOn;
    private boolean requestModeration;
    private String state;
    @NotNull
    private String title;
    private Long views;
}
