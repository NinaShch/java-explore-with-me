package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.ExceptionMessage;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.paging.OffsetLimitPageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationService {

    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toNewCompilation(newCompilationDto, eventRepository);
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    public List<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        Pageable pageable = OffsetLimitPageable.create(from, size, Sort.by(Sort.Direction.ASC, "id"));
        return compilationRepository.getAllByPinned(pinned, pageable)
                .stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation not found"));
        return CompilationMapper.toCompilationDto(compilation);
    }

    public void deleteCompilation(Long compId) {
        Compilation compilation = getCompById(compId);
        compilationRepository.delete(compilation);
    }

    private Compilation getCompById(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation not found"));
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public void deleteEventFromCompilation(Long compId, Long eventId) {
        Compilation compilation = getCompById(compId);
        Event eventToDelete = getEventById(eventId);

        if (!compilation.getEvents().contains(eventToDelete)) {
            throw new NotFoundException("No such event in compilation");
        }

        compilation.setEvents(
                compilation.getEvents()
                        .stream()
                        .filter(event -> !event.equals(eventToDelete))
                        .collect(Collectors.toList())
        );
        compilationRepository.save(compilation);
    }

    public void addEventToCompilation(Long compId, Long eventId) {
        Compilation compilation = getCompById(compId);
        Event event = getEventById(eventId);

        if (compilation.getEvents().contains(event)) {
            throw new ForbiddenException("Event already present in compilation", ExceptionMessage.CONDITIONS_NOT_MET);
        }

        compilation.getEvents().add(event);
        compilationRepository.save(compilation);
    }

    public void unpinCompilation(Long compId) {
        Compilation compilation = getCompById(compId);
        if (!compilation.isPinned()) {
            throw new ForbiddenException("Compilation is not pinned", ExceptionMessage.CONDITIONS_NOT_MET);
        }
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    public void pinCompilation(Long compId) {
        Compilation compilation = getCompById(compId);
        if (compilation.isPinned()) {
            throw new ForbiddenException("Compilation is already pinned", ExceptionMessage.CONDITIONS_NOT_MET);
        }
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }
}
