package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.client.HitDto;
import ru.practicum.client.StatHitClient;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.exception.ExceptionMessage.CONDITIONS_NOT_MET;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StatHitClient statHitClient;

    Event getEventById(Long eventId, HttpServletRequest request) {
        statHitClient.hitRequest(
                new HitDto(
                        "mainService",
                        request.getRequestURI(),
                        request.getRemoteAddr(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id= %d was not found.", eventId)));
        event.setViews(event.getViews() + 1);
        return eventRepository.save(event);
    }

    public List<EventFullDto> getEventByParamsForAdmin(long[] userIds, String[] stateStrings, long[] categoryIds,
                                                       String rangeStart, String rangeEnd, int from, int size) {
        List<User> users = userIds != null ?
                Arrays.stream(userIds).boxed().map(this::getUserById).collect(Collectors.toList())
                : null;
        List<State> states = stateStrings != null ?
                Arrays.stream(stateStrings).map(this::getStateByString).collect(Collectors.toList())
                : null;
        List<Category> categories = categoryIds != null ?
                Arrays.stream(categoryIds).boxed().map(this::getCategoryById).collect(Collectors.toList())
                : null;

        List<Event> gotEvents = eventRepository.findByParamsForAdmin(
                users,
                states,
                categories,
                parseDate(rangeStart),
                parseDate(rangeEnd),
                from,
                size
        );

        return gotEvents.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    public List<EventShortDto> getEventsByParamsCommon(String text, long[] categoryIds, Boolean paid,
                                                       String rangeStart, String rangeEnd, boolean onlyAvailable,
                                                       String sort, int from, int size) {

        List<Category> categories =
                Arrays.stream(categoryIds).boxed().map(this::getCategoryById).collect(Collectors.toList());

        List<Event> gotEvents = eventRepository.findByParamsCommon(
                text,
                categories,
                paid,
                parseDate(rangeStart),
                parseDate(rangeEnd),
                onlyAvailable,
                sort,
                from,
                size
        );

        return gotEvents.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private State getStateByString(String stateString) {
        for (State state : State.values()) {
            if (state.name().equals(stateString))
                return state;
        }
        throw new BadRequestException("Wrong state $stateString");
    }

    private LocalDateTime parseDate(String dateString) {
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Wrong datetime $dateString");
        }
    }

    public EventFullDto putEvent(long eventId, NewEventDto newEventDto) {
        Event event = getEventById(eventId);
        if (newEventDto.getAnnotation() != null) {
            event.setAnnotation(newEventDto.getAnnotation());
        }
        if (newEventDto.getCategory() != null) {
            event.setCategory(getCategoryById(newEventDto.getCategory()));
        }
        if (newEventDto.getDescription() != null) {
            event.setDescription(newEventDto.getDescription());
        }
        if (newEventDto.getEventDate() != null) {
            event.setEventDate(parseDate(newEventDto.getEventDate()));
        }
        if (newEventDto.getLocation() != null) {
            event.setLocation(newEventDto.getLocation());
        }
        if (newEventDto.getTitle() != null) {
            event.setTitle(newEventDto.getTitle());
        }
        event.setPaid(newEventDto.isPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.isRequestModeration());
        Event savedEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(savedEvent);
    }

    public EventFullDto publishEvent(Long eventId) {
        Event event = getEventById(eventId);

        //дата начала события должна быть не ранее чем за час от даты публикации.
        if (event.getEventDate()
                .minusHours(1)
                .isBefore(LocalDateTime.now()))
            throw new ForbiddenException("Event time should be at least two hours after now", CONDITIONS_NOT_MET);

        //событие должно быть в состоянии ожидания публикации
        if (event.getState() != State.PENDING) {
            throw new ForbiddenException("Only pending events can be published", CONDITIONS_NOT_MET);
        }

        event.setState(State.PUBLISHED);
        Event savedEvent = eventRepository.save(event);

        return EventMapper.toEventFullDto(savedEvent);
    }

    public EventFullDto rejectEvent(Long eventId) {
        Event event = getEventById(eventId);
        //Обратите внимание: событие не должно быть опубликовано.
        if (event.getState() == State.PUBLISHED) {
            throw new ForbiddenException("Already published events can't be rejected", CONDITIONS_NOT_MET);
        }

        event.setState(State.CANCELED);
        Event savedEvent = eventRepository.save(event);

        return EventMapper.toEventFullDto(savedEvent);
    }
}