package ru.practicum.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.DateTimeUtils;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.CategoryService;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.entity.Event;
import ru.practicum.user.UserMapper;
import ru.practicum.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class, CategoryMapper.class, CategoryService.class, EventService.class})
public interface EventMapper {

    @Mapping(target = "initiator", source = "event.initiator")
    @Mapping(target = "category", source = "event.category")
    @Mapping(target = "eventDate", source = "event.eventDate", dateFormat = DateTimeUtils.DATE_TIME_FORMAT)
    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "initiator", source = "event.initiator")
    @Mapping(target = "category", source = "event.category")
    @Mapping(target = "eventDate", source = "event.eventDate", dateFormat = DateTimeUtils.DATE_TIME_FORMAT)
    @Mapping(target = "createdOn", source = "event.createdOn", dateFormat = DateTimeUtils.DATE_TIME_FORMAT)
    @Mapping(target = "publishedOn", source = "event.publishedOn", dateFormat = DateTimeUtils.DATE_TIME_FORMAT)
    @Mapping(target = "views", source = "hits")
    EventFullDto toEventFullDto(Event event, Long hits, List<CommentDto> comments);

    @Mapping(target = "category", source = "newEventDto.category")
    @Mapping(target = "initiator", source = "user")
    @Mapping(target = "eventDate", source = "newEventDto.eventDate", dateFormat = DateTimeUtils.DATE_TIME_FORMAT)
    @Mapping(target = "createdOn", source = "createdOn")
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "confirmedRequests", constant = "0")
    Event toEvent(NewEventDto newEventDto, User user, LocalDateTime createdOn);

}
