package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.HelperService;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.entity.Compilation;
import ru.practicum.event.entity.Event;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.paging.OffsetLimitPageable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final HelperService helperService;

    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toNewCompilation(newCompilationDto);
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    public List<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        Pageable pageable = OffsetLimitPageable.create(from, size, Sort.by(Sort.Direction.ASC, "id"));
        return compilationRepository.getAllByPinned(pinned, pageable)
                .stream()
                .map(compilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    public CompilationDto getCompilationDtoById(Long compId) {
        Compilation compilation = helperService.getCompilationById(compId);
        return compilationMapper.toCompilationDto(compilation);
    }

    public void deleteCompilation(Long compId) {
        Compilation compilation = helperService.getCompilationById(compId);
        compilationRepository.delete(compilation);
    }

    public void deleteEventFromCompilation(Long compId, Long eventId) {
        Compilation compilation = helperService.getCompilationById(compId);
        Event eventToDelete = helperService.getEventById(eventId);

        if (!containsEvent(compilation, eventToDelete)) {
            throw new NotFoundException("No such event in compilation");
        }

        compilation.setEvents(
                compilation.getEvents()
                        .stream()
                        .filter(event -> !Objects.equals(event.getId(), eventToDelete.getId()))
                        .collect(Collectors.toList())
        );
        compilationRepository.save(compilation);
    }

    public void addEventToCompilation(Long compId, Long eventId) {
        Compilation compilation = helperService.getCompilationById(compId);
        Event event = helperService.getEventById(eventId);

        if (containsEvent(compilation, event)) {
            throw new ForbiddenException("Event already present in compilation");
        }

        compilation.getEvents().add(event);
        compilationRepository.save(compilation);
    }

    public void unpinCompilation(Long compId) {
        Compilation compilation = helperService.getCompilationById(compId);
        if (!compilation.isPinned()) {
            throw new ForbiddenException("Compilation is not pinned");
        }
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    public void pinCompilation(Long compId) {
        Compilation compilation = helperService.getCompilationById(compId);
        if (compilation.isPinned()) {
            throw new ForbiddenException("Compilation is already pinned");
        }
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    private boolean containsEvent(Compilation compilation, Event eventToFind) {
        return compilation.getEvents().stream().anyMatch((event) -> Objects.equals(event.getId(), eventToFind.getId()));
    }
}
