package ru.practicum.comment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.event.entity.Event;
import ru.practicum.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "users_id")
    private User author;
    @ManyToOne
    @JoinColumn(name = "events_id")
    private Event event;
    @Column(length = 2048)
    private String content;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
}
