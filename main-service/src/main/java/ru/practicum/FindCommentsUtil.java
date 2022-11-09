package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.comment.CommentMapper;
import ru.practicum.comment.CommentRepository;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.entity.Comment;
import ru.practicum.event.entity.Event;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FindCommentsUtil {

    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    public List<CommentDto> getCommentsByEvent(Event event) {
        return commentRepository.findByEvent(event).stream()
                .sorted(Comparator.comparing(Comment::getCreatedOn))
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
