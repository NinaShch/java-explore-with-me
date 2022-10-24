package ru.practicum.event.model;

import lombok.*;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "annotation", length = 1024)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "categories_id")
    private Category category;
    @Column(name = "confirmed_requests")
    private int confirmedRequests;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "description", length = 1024)
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "users_id")
    private User initiator;
    @Embedded
    private Location location;
    private boolean paid;
    @Column(name = "participant_limit")
    private int participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private State state;
    private String title;
    private Long views;
}
