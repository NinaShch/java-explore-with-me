package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.entity.Event;
import ru.practicum.request.entity.ParticipationRequest;
import ru.practicum.user.entity.User;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findByEventAndRequester(Event event, User requester);

    List<ParticipationRequest> findByEvent(Event event);

    List<ParticipationRequest> findByRequester(User requester);

    @Modifying
    @Query("update ParticipationRequest pr set pr.status = 'REJECTED' where pr.status = 'PENDING' and pr.event = :event")
    void cancelAllPendingRequestsForEvent(Event event);
}
