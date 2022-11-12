package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.HelperService;
import ru.practicum.comment.entity.Comment;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final HelperService helperService;
    private final CommentRepository commentRepository;

    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = helperService.getCommentById(commentId);
        commentRepository.delete(comment);
    }
}
