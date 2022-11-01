package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.DateTimeUtils;
import ru.practicum.HelperService;
import ru.practicum.category.CategoryService;
import ru.practicum.category.entity.Category;
import ru.practicum.client.HitDto;
import ru.practicum.client.StatHitClient;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.State;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.user.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final StatHitClient statHitClient;
    private final EventMapper eventMapper;
    private final HelperService helperService;
    private final CategoryService categoryService;

    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        statHitClient.hitRequest(
                new HitDto(
                        "mainService",
                        request.getRequestURI(),
                        request.getRemoteAddr(),
                        DateTimeUtils.getDateTimeNow()));

        Event event = helperService.getEventById(eventId);
        Long hits = statHitClient.statsRequest(eventId).orElse(0L);
        return eventMapper.toEventFullDto(event, hits);
    }

    public List<EventFullDto> getEventByParamsForAdmin(long[] userIds, String[] stateStrings, long[] categoryIds,
                                                       String rangeStart, String rangeEnd, int from, int size) {
        List<User> users = userIds != null ?
                Arrays.stream(userIds).boxed().map(helperService::getUserById).collect(Collectors.toList())
                : null;
        List<State> states = stateStrings != null ?
                Arrays.stream(stateStrings).map(this::getStateByString).collect(Collectors.toList())
                : null;
        List<Category> categories = categoryIds != null ?
                Arrays.stream(categoryIds).boxed().map(helperService::getCategoryById).collect(Collectors.toList())
                : null;

        List<Event> gotEvents = eventRepository.findByParamsForAdmin(
                users,
                states,
                categories,
                DateTimeUtils.parseDate(rangeStart),
                DateTimeUtils.parseDate(rangeEnd),
                from,
                size
        );

        return gotEvents
                .stream()
                .map((event) -> eventMapper
                        .toEventFullDto(event, statHitClient.statsRequest(event.getId()).orElse(0L)))
                .collect(Collectors.toList());
    }

    public List<EventShortDto> getEventsByParamsCommon(String text, long[] categoryIds, Boolean paid,
                                                       String rangeStart, String rangeEnd, boolean onlyAvailable,
                                                       String sort, int from, int size) {

        List<Category> categories = null;

        if (categoryIds != null) {
            categories = Arrays.stream(categoryIds).boxed().map(helperService::getCategoryById).collect(Collectors.toList());
        }

        if ("EVENT_DATE".equals(sort))
            return getEventsByParamsSortedByDate(text, categories, paid, rangeStart, rangeEnd,
                    onlyAvailable, from, size);
        else if ("VIEWS".equals(sort))
            return getEventsByParamsSortedByViews(text, categories, paid, rangeStart, rangeEnd,
                    onlyAvailable, from, size);
        else throw new BadRequestException("Wrong sort", "nonexistent type of sorting");
    }

    private List<EventShortDto> getEventsByParamsSortedByDate(String text, List<Category> categories, Boolean paid,
                                                               String rangeStart, String rangeEnd,
                                                               boolean onlyAvailable, int from, int size) {
        List<Event> gotEvents = eventRepository.findByParamsCommon(
                text,
                categories,
                paid,
                DateTimeUtils.parseDate(rangeStart),
                DateTimeUtils.parseDate(rangeEnd),
                onlyAvailable,
                from,
                size
        );

        return gotEvents.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList());
    }

    private List<EventShortDto> getEventsByParamsSortedByViews(String text, List<Category> categories, Boolean paid,
                                                               String rangeStart, String rangeEnd,
                                                               boolean onlyAvailable, int from, int size) {
        List<Event> gotEvents = eventRepository.findByParamsCommon(
                text,
                categories,
                paid,
                DateTimeUtils.parseDate(rangeStart),
                DateTimeUtils.parseDate(rangeEnd),
                onlyAvailable,
                from,
                size
        );

        Map<Long, Long> viewsMap = statHitClient.viewsMapRequest(
                gotEvents.stream().map(Event::getId).collect(Collectors.toList())
        );
        gotEvents.sort(new EventByViewsComparator(viewsMap));
        return gotEvents
                .stream()
                .skip(from)
                .limit(size)
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    private State getStateByString(String stateString) {
        for (State state : State.values()) {
            if (state.name().equals(stateString))
                return state;
        }
        throw new BadRequestException("Wrong state " + stateString, "Nonexistent state");
    }

    public EventFullDto putEvent(long eventId, NewEventDto newEventDto) {
        Event event = helperService.getEventById(eventId);
        if (newEventDto.getAnnotation() != null) {
            event.setAnnotation(newEventDto.getAnnotation());
        }
        if (newEventDto.getCategory() != null) {
            event.setCategory(helperService.getCategoryById(newEventDto.getCategory()));
        }
        if (newEventDto.getDescription() != null) {
            event.setDescription(newEventDto.getDescription());
        }
        if (newEventDto.getEventDate() != null) {
            event.setEventDate(DateTimeUtils.parseDate(newEventDto.getEventDate()));
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
        return eventMapper.toEventFullDto(savedEvent, statHitClient.statsRequest(event.getId()).orElse(0L));
    }

    public EventFullDto publishEvent(Long eventId) {
        Event event = helperService.getEventById(eventId);

        if (event.getEventDate()
                .minusHours(1)
                .isBefore(LocalDateTime.now()))
            throw new ForbiddenException("Event time should be at least two hours after now");

        if (event.getState() != State.PENDING) {
            throw new ForbiddenException("Only pending events can be published");
        }

        event.setState(State.PUBLISHED);
        Event savedEvent = eventRepository.save(event);

        return eventMapper.toEventFullDto(savedEvent, statHitClient.statsRequest(savedEvent.getId()).orElse(0L));
    }

    public EventFullDto rejectEvent(Long eventId) {
        Event event = helperService.getEventById(eventId);
        if (event.getState() == State.PUBLISHED) {
            throw new ForbiddenException("Already published events can't be rejected");
        }

        event.setState(State.CANCELED);
        Event savedEvent = eventRepository.save(event);

        return eventMapper.toEventFullDto(savedEvent, statHitClient.statsRequest(savedEvent.getId()).orElse(0L));
    }
}