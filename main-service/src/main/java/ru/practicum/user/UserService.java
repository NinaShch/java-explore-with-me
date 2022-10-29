package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HelperService;
import ru.practicum.category.CategoryRepository;
import ru.practicum.client.StatHitClient;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.State;
import ru.practicum.event.entity.Status;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.paging.OffsetLimitPageable;
import ru.practicum.request.entity.ParticipationRequest;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.ParticipationRequestMapper;
import ru.practicum.request.ParticipationRequestRepository;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.entity.User;

import java.time.LocalDateTime;
import java.util.Arrays;
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
    private final ParticipationRequestMapper participationRequestMapper;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;
    private final StatHitClient statHitClient;
    private final HelperService helperService;

    public List<EventShortDto> getEventsByUser(Long userId, int from, int size) {
        User user = helperService.getUserById(userId);
        Pageable pageable = OffsetLimitPageable.create(from, size, Sort.by(Sort.Direction.ASC, "eventDate"));
        return eventRepository.findByInitiator(user, pageable).stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public EventFullDto updateEventByUser(Long userId, UpdateEventDto updateEventDto) {
        User user = helperService.getUserById(userId);
        Event event = helperService.getEventById(updateEventDto.getEventId());

        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenException("Only the initiator can update event", CONDITIONS_NOT_MET);
        }
        if (event.getState().equals(State.PUBLISHED))
            throw new ForbiddenException("Only pending or canceled events can be changed", CONDITIONS_NOT_MET);

        LocalDateTime eventDate = helperService.parseDate(updateEventDto.getEventDate());
        validateEventDate(eventDate);

        event.setAnnotation(updateEventDto.getAnnotation());
        event.setCategory(helperService.getCategoryById(updateEventDto.getCategory()));
        event.setDescription(updateEventDto.getDescription());
        event.setEventDate(eventDate);
        event.setPaid(updateEventDto.isPaid());
        event.setParticipantLimit(updateEventDto.getParticipantLimit());
        event.setTitle(updateEventDto.getTitle());
        if (event.getState().equals(State.CANCELED)) event.setState(State.PENDING);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toEventFullDto(savedEvent, statHitClient.statsRequest(event.getId()));

    }

    public EventFullDto createEventByUser(Long userId, NewEventDto newEventDto) {
        User user = helperService.getUserById(userId);
        categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
                () -> new NotFoundException("Category not found"));

        LocalDateTime eventDate = helperService.parseDate(newEventDto.getEventDate());
        validateEventDate(eventDate);

        Event event = eventMapper.toEvent(newEventDto, user, LocalDateTime.now());
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toEventFullDto(savedEvent, statHitClient.statsRequest(savedEvent.getId()));
    }

    public EventFullDto getEventByUserAndId(Long userId, Long eventId) {
        User user = helperService.getUserById(userId);
        Event event = helperService.getEventById(eventId);
        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenException("Only the initiator can get event details", CONDITIONS_NOT_MET);
        }

        return eventMapper.toEventFullDto(event, statHitClient.statsRequest(event.getId()));
    }

    public EventFullDto cancelEvent(Long userId, Long eventId) {
        User user = helperService.getUserById(userId);
        Event event = helperService.getEventById(eventId);
        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenException("Only the initiator can cancel events", CONDITIONS_NOT_MET);
        }
        if (event.getState() != State.PENDING) {
            throw new ForbiddenException("Only pending events can be cancelled", CONDITIONS_NOT_MET);
        }

        event.setState(State.CANCELED);
        Event cancelledEvent = eventRepository.save(event);

        return eventMapper.toEventFullDto(cancelledEvent, statHitClient.statsRequest(cancelledEvent.getId()));
    }

    public List<ParticipationRequestDto> getEventRequestsByUserAndId(Long userId, Long eventId) {
        User user = helperService.getUserById(userId);
        Event event = helperService.getEventById(eventId);
        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenException("Only the initiator can get event requests", CONDITIONS_NOT_MET);
        }

        return participationRequestRepository.findByEvent(event)
                .stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId) {
        User user = helperService.getUserById(userId);
        Event event = helperService.getEventById(eventId);
        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenException("Only the initiator can confirm requests", CONDITIONS_NOT_MET);
        }

        // нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие
        // из сваггера - Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
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
            participationRequestRepository.cancelAllPendingRequestsForEvent(event);
        }

        return participationRequestMapper.toParticipationRequestDto(savedOne);
    }

    public ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long reqId) {
        User user = helperService.getUserById(userId);
        Event event = helperService.getEventById(eventId);
        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenException("Only the initiator can reject requests", CONDITIONS_NOT_MET);
        }

        ParticipationRequest participationRequest = getParticipationRequestById(reqId);
        participationRequest.setStatus(Status.REJECTED);

        ParticipationRequest cancelledOne = participationRequestRepository.save(participationRequest);
        return participationRequestMapper.toParticipationRequestDto(cancelledOne);
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
        User newUser = userMapper.toNewUser(newUserDto);
        User savedUser = userRepository.save(newUser);
        return userMapper.toUserDto(savedUser);
    }

    public void deleteUser(Long userId) {
        User user = helperService.getUserById(userId);
        userRepository.delete(user);
    }

    public List<UserDto> getUsersByAdmin(Long[] ids, int from, int size) {
        if (ids == null) {
            Pageable pageable = OffsetLimitPageable.create(from, size, Sort.by(Sort.Direction.ASC, "id"));
            return userRepository.findAll(pageable).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findByIds(Arrays.stream(ids).collect(Collectors.toList())).stream()
                    .map(userMapper::toUserDto).collect(Collectors.toList());
        }
    }

    public List<ParticipationRequestDto> getListParticipationRequestByUserId(Long userId) {
        User requester = helperService.getUserById(userId);
        return participationRequestRepository.findByRequester(requester)
                .stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParticipationRequestDto createEventRequestByUser(Long userId, Long eventId) {
        User user = helperService.getUserById(userId);
        Event event = helperService.getEventById(eventId);
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

        if (participationRequest.getStatus() == Status.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        return participationRequestMapper
                .toParticipationRequestDto(participationRequestRepository.save(participationRequest));
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        User user = helperService.getUserById(userId);
        ParticipationRequest request = getParticipationRequestById(requestId);

        if (!request.getRequester().equals(user)) {
            throw new ForbiddenException("Only the requesor can cancel requests", CONDITIONS_NOT_MET);
        }

        request.setStatus(Status.CANCELED);

        ParticipationRequest cancelledOne = participationRequestRepository.save(request);
        return participationRequestMapper.toParticipationRequestDto(cancelledOne);
    }
}
