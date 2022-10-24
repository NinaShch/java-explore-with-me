package ru.practicum.request;

import lombok.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Status;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
