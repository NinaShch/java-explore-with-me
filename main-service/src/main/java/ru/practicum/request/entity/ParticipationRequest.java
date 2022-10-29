package ru.practicum.request.entity;

import lombok.*;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.Status;
import ru.practicum.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "participation_requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "events_id")
    private Event event;
    @ManyToOne
    @JoinColumn(name = "users_id")
    private User requester;
    @Enumerated(EnumType.STRING)
    private Status status;
}
