package ru.practicum.event;

import ru.practicum.category.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.user.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {
    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(event.getEventDate()),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.isPaid(),
                event.getTitle(),
                event.getViews()
        );
    }

    public static EventFullDto toEventFullDto(Event event) {
        EventFullDto.EventFullDtoBuilder builder = EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .description(event.getDescription())
                .id(event.getId())
                .location(event.getLocation())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .title(event.getTitle())
                .views(event.getViews());

        if (event.getCreatedOn() != null) {
            builder.createdOn(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(event.getCreatedOn()));
        }
        if (event.getEventDate() != null) {
            builder.eventDate(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(event.getEventDate()));
        }
        if (event.getPublishedOn() != null) {
            builder.publishedOn(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(event.getPublishedOn()));
        }
        if (event.getState() != null) {
            builder.state(event.getState().toString());
        }

        return builder.build();
    }

    public static Event toEvent(NewEventDto newEventDto, Category category, LocalDateTime eventDate, User initiator) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(eventDate)
                .initiator(initiator)
                .location(newEventDto.getLocation())
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .title(newEventDto.getTitle())
                .state(State.PENDING)
                .views(0L)
                .confirmedRequests(0)
                .build();
    }
}
