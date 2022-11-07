package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentDto {
    private Long id;
    private Long authorId;
    private Long eventId;
    private String content;
    private String createdOn;
}
