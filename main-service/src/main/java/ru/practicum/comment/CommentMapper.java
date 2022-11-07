package ru.practicum.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.DateTimeUtils;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.entity.Comment;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventService;
import ru.practicum.event.entity.Event;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserService;
import ru.practicum.user.entity.User;

import java.time.LocalDateTime;

@Mapper(
            componentModel = "spring",
            uses = {UserMapper.class, EventMapper.class, EventService.class, UserService.class})
    public interface CommentMapper {

    @Mapping(target = "author", source = "author")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "createdOn", source = "createdOn", dateFormat = DateTimeUtils.DATE_TIME_FORMAT)
    @Mapping(target = "id", expression = "java(null)")
    Comment toCommentFromNewCommentDto(NewCommentDto newCommentDto, User author, Event event, LocalDateTime createdOn);

    Comment toComment(CommentDto commentDto);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "createdOn", source = "createdOn", dateFormat = DateTimeUtils.DATE_TIME_FORMAT)
    CommentDto toCommentDto(Comment comment);
}
