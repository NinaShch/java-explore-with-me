package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/{userId}/events", produces = "application/json")
    public List<EventShortDto> getEventsByUser(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        log.info("request events by user id = {}", userId);
        return userService.getEventsByUser(userId, from, size);
    }

    @PatchMapping(value = "/{userId}/events", produces = "application/json", consumes = "application/json")
    public EventFullDto updateEventByUser(
            @PathVariable Long userId,
            @RequestBody @Valid UpdateEventDto updateEventDto) {
        log.info("request to update event id = {}, by user id = {}", updateEventDto.getEventId(), userId);
        return userService.updateEventByUser(userId, updateEventDto);
    }

    @PostMapping(value = "/{userId}/events", produces = "application/json", consumes = "application/json")
    public EventFullDto createEventByUser(
            @PathVariable Long userId,
            @RequestBody @Valid NewEventDto newEventDto) {
        log.info("request to create event = {}, by user id = {}", newEventDto.getTitle(), userId);
        return userService.createEventByUser(userId, newEventDto);
    }

    @GetMapping(value = "/{userId}/events/{eventId}", produces = "application/json")
    public EventFullDto getEventByUserAndId(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        log.info("request event by user id = {} and event id = {}", userId, eventId);
        return userService.getEventByUserAndId(userId, eventId);
    }

    @PatchMapping(value = "/{userId}/events/{eventId}", produces = "application/json")
    public EventFullDto cancelEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        log.info("cancel event by user id = {} and event id = {}", userId, eventId);
        return userService.cancelEvent(userId, eventId);
    }

    @GetMapping(value = "/{userId}/events/{eventId}/requests", produces = "application/json")
    public List<ParticipationRequestDto> getEventRequestsByUserAndId(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        log.info("request event requests by user id = {} and event id = {}", userId, eventId);
        return userService.getEventRequestsByUserAndId(userId, eventId);
    }

    @PatchMapping(value = "/{userId}/events/{eventId}/requests/{reqId}/confirm", produces = "application/json")
    //формат ответа прописан в ТЗ, изменение формата может привести к тому что при отправке задания не
    //будут пройдены тесты и нельзя будет влить PR
    public ParticipationRequestDto confirmRequest(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long reqId
    ) {
        log.info("confirm event request with id = {} by user id = {} and event id = {}", reqId, userId, eventId);
        return userService.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping(value = "/{userId}/events/{eventId}/requests/{reqId}/reject", produces = "application/json")
    public ParticipationRequestDto rejectRequest(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long reqId
    ) {
        log.info("reject event request with id = {} by user id = {} and event id = {}", reqId, userId, eventId);
        return userService.rejectRequest(userId, eventId, reqId);
    }

    @GetMapping(value = "/{userId}/requests", produces = "application/json")
    public List<ParticipationRequestDto> getEventRequestsByUser(@PathVariable Long userId) {
        log.info("request event requests by user id = {}", userId);
        return userService.getListParticipationRequestByUserId(userId);
    }

    @PostMapping(value = "/{userId}/requests", produces = "application/json")
    public ParticipationRequestDto createEventRequestByUser(
            @PathVariable Long userId,
            @RequestParam Long eventId
    ) {
        log.info("request to create a request for event = {}, by user id = {}", eventId, userId);
        return userService.createEventRequestByUser(userId, eventId);
    }

    @PatchMapping(value = "/{userId}/requests/{requestId}/cancel", produces = "application/json")
    public ParticipationRequestDto cancelRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId
    ) {
        log.info("cancel event request with id = {} by user id = {}", requestId, userId);
        return userService.cancelRequest(userId, requestId);
    }
}
