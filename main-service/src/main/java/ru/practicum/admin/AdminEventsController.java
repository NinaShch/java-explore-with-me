package ru.practicum.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;

import java.util.List;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventsController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEventsByParams(
            @RequestParam(required = false) long[] userIds,
            @RequestParam(required = false) String[] states,
            @RequestParam(required = false) long[] categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        log.info("admin requests events");
        return eventService.getEventByParamsForAdmin(userIds, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("/{eventId}")
    public EventFullDto putEvent(
            @PathVariable long eventId,
            @RequestBody NewEventDto newEventDto
    ) {
        log.info("admin puts event with id = {}", eventId);
        return eventService.putEvent(eventId, newEventDto);
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable Long eventId) {
        log.info("publish event with id = {}", eventId);
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable Long eventId) {
        log.info("reject event with id = {}", eventId);
        return eventService.rejectEvent(eventId);
    }
}
