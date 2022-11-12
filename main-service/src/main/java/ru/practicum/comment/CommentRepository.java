package ru.practicum.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.entity.Comment;
import ru.practicum.event.entity.Event;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByEvent(Event event);
}
