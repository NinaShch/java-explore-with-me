package ru.practicum.compilation;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation toNewCompilation(NewCompilationDto newCompilationDto, EventRepository eventRepository) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        if (newCompilationDto.getPinned() == null) compilation.setPinned(false);
        else compilation.setPinned(newCompilationDto.getPinned());
        if (newCompilationDto.getEvents() != null) {
            List<Event> events = new ArrayList<>();
            for (Long id : newCompilationDto.getEvents()) {
                events.add(eventRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Event not found")));
            }
            compilation.setEvents(events);
        }
        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto.CompilationDtoBuilder builder = CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.isPinned())
                .title(compilation.getTitle());

        if (compilation.getEvents() != null) {
            builder.events(
                    compilation.getEvents().stream().map(EventMapper::toEventShortDto).collect(Collectors.toList())
            );
        }

        return builder.build();
    }
}
