package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.Status;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.paging.OffsetLimitPageable;
import ru.practicum.request.ParticipationRequest;
import ru.practicum.request.ParticipationRequestDto;
import ru.practicum.request.ParticipationRequestMapper;
import ru.practicum.request.ParticipationRequestRepository;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.exception.ExceptionMessage.CONDITIONS_NOT_MET;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    public List<EventShortDto> getEventsByUser(Long userId, int from, int size) {
        User user = getUserById(userId);
        Pageable pageable = OffsetLimitPageable.create(from, size, Sort.by(Sort.Direction.ASC, "eventDate"));
        return eventRepository.findByInitiator(user, pageable).stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public EventFullDto updateEventByUser(Long userId, UpdateEventDto updateEventDto) {
        User user = getUserById(userId);
        Event event = getEventById(updateEventDto.getEventId());

        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenException("Only the initiator can update event", CONDITIONS_NOT_MET);
        }
        if (event.getState().equals(State.PUBLISHED))
            throw new ForbiddenException("Only pending or canceled events can be changed", CONDITIONS_NOT_MET);

        LocalDateTime eventDate = parseDate(updateEventDto.getEventDate());
        validateEventDate(eventDate);

        event.setAnnotation(updateEventDto.getAnnotation());
        event.setCategory(categoryRepository.findById(updateEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found")));
        event.setDescription(updateEventDto.getDescription());
        event.setEventDate(eventDate);
        event.setPaid(updateEventDto.isPaid());
        event.setParticipantLimit(updateEventDto.getParticipantLimit());
        event.setTitle(updateEventDto.getTitle());
        if (event.getState().equals(State.CANCELED)) event.setState(State.PENDING);
        return EventMapper.toEventFullDto(eventRepository.save(event));

    }

    public EventFullDto createEventByUser(Long userId, NewEventDto newEventDto) {
        User user = getUserById(userId);
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
                () -> new NotFoundException("Category not found"));

        LocalDateTime eventDate = parseDate(newEventDto.getEventDate());
        validateEventDate(eventDate);

        Event event = EventMapper.toEvent(newEventDto, category, eventDate, user);
        Event savedEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(savedEvent);
    }

    public EventFullDto getEventByUserAndId(Long userId, Long eventId) {
        User user = getUserById(userId);
        Event event = getEventById(eventId);
        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenException("Only the initiator can get event details", CONDITIONS_NOT_MET);
        }

        return EventMapper.toEventFullDto(event);
    }

    public EventFullDto cancelEvent(Long userId, Long eventId) {
        User user = getUserById(userId);
        Event event = getEventById(eventId);
        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenException("Only the initiator can cancel events", CONDITIONS_NOT_MET);
        }
        if (event.getState() != State.PENDING) {
            throw new ForbiddenException("Only pending events can be cancelled", CONDITIONS_NOT_MET);
        }

        event.setState(State.CANCELED);
        Event cancelledEvent = eventRepository.save(event);

        return EventMapper.toEventFullDto(cancelledEvent);
    }

    public List<ParticipationRequestDto> getEventRequestsByUserAndId(Long userId, Long eventId) {
        User user = getUserById(userId);
        Event event = getEventById(eventId);
        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenException("Only the initiator can get event requests", CONDITIONS_NOT_MET);
        }

        return participationRequestRepository.findByEvent(event)
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId) {
        User user = getUserById(userId);
        Event event = getEventById(eventId);
        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenException("Only the initiator can confirm requests", CONDITIONS_NOT_MET);
        }

        // нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ForbiddenException("Participant limit reached", CONDITIONS_NOT_MET);
        }

        ParticipationRequest participationRequest = getParticipationRequestById(reqId);
        participationRequest.setStatus(Status.CONFIRMED);

        ParticipationRequest savedOne = participationRequestRepository.save(participationRequest);

        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventRepository.save(event);

        // если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            participationRequestRepository.cancelAllPendingRequests();
        }

        return ParticipationRequestMapper.toParticipationRequestDto(savedOne);
    }

    public ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long reqId) {
        User user = getUserById(userId);
        Event event = getEventById(eventId);
        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenException("Only the initiator can reject requests", CONDITIONS_NOT_MET);
        }

        ParticipationRequest participationRequest = getParticipationRequestById(reqId);
        participationRequest.setStatus(Status.REJECTED);

        ParticipationRequest cancelledOne = participationRequestRepository.save(participationRequest);
        return ParticipationRequestMapper.toParticipationRequestDto(cancelledOne);
    }

    private LocalDateTime parseDate(String dateString) {
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Wrong datetime $dateString");
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
    }

    private ParticipationRequest getParticipationRequestById(Long reqId) {
        return participationRequestRepository.findById(reqId).orElseThrow(
                () -> new NotFoundException("Event not found"));
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate
                .minusHours(2)
                .isBefore(LocalDateTime.now()))
            throw new ForbiddenException("Event time should be at least two hours after now", CONDITIONS_NOT_MET);
    }

    public UserDto addNewUser(NewUserDto newUserDto) {
        User newUser = UserMapper.toNewUser(newUserDto);
        User savedUser = userRepository.save(newUser);
        return UserMapper.toUserDto(savedUser);
    }

    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }

    public List<UserDto> getUsersByAdmin(Long[] ids, int from, int size) {
        if (ids == null) {
            Pageable pageable = OffsetLimitPageable.create(from, size, Sort.by(Sort.Direction.ASC, "id"));
            return userRepository.findAll(pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            List<UserDto> users = new ArrayList<>();
            for (Long id : ids) {
                userRepository.findById(id).ifPresent(user -> users.add(UserMapper.toUserDto(user)));
            }
            return users;
        }
    }

    public List<ParticipationRequestDto> getEventRequestsByUser(Long userId) {
        User requester = getUserById(userId);
        return participationRequestRepository.findByRequester(requester)
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto createEventRequestByUser(Long userId, Long eventId) {
        User user = getUserById(userId);
        Event event = getEventById(eventId);
        if (event.getInitiator().equals(user)) {
            throw new ForbiddenException("Can't request own events", CONDITIONS_NOT_MET);
        }
        if (!participationRequestRepository.findByEventAndRequester(event, user).isEmpty()) {
            throw new ForbiddenException("Request for this event already exists", CONDITIONS_NOT_MET);
        }
        if (event.getState() != State.PUBLISHED) {
            throw new ForbiddenException("Only published events can be requested", CONDITIONS_NOT_MET);
        }
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ForbiddenException("Participant limit reached", CONDITIONS_NOT_MET);
        }

        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(event.isRequestModeration() ? Status.PENDING : Status.CONFIRMED)
                .build();

        ParticipationRequest savedParticipationRequest = participationRequestRepository.save(participationRequest);

        if (participationRequest.getStatus() == Status.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        return ParticipationRequestMapper.toParticipationRequestDto(savedParticipationRequest);
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        User user = getUserById(userId);
        ParticipationRequest request = getParticipationRequestById(requestId);

        if (!request.getRequester().equals(user)) {
            throw new ForbiddenException("Only the requesor can cancel requests", CONDITIONS_NOT_MET);
        }

        request.setStatus(Status.CANCELED);

        ParticipationRequest cancelledOne = participationRequestRepository.save(request);
        return ParticipationRequestMapper.toParticipationRequestDto(cancelledOne);
    }
}
