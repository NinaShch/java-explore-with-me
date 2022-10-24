package ru.practicum.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.CompilationService;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;

import javax.validation.Valid;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationsController {

    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("request add compilation");
        return compilationService.addCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("request to delete Compilation id = {}", compId);
        compilationService.deleteCompilation(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(
            @PathVariable Long compId,
            @PathVariable Long eventId
    ) {
        log.info("request to delete event with id = {} from Compilation id = {}", eventId, compId);
        compilationService.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addEventToCompilation(
            @PathVariable Long compId,
            @PathVariable Long eventId
    ) {
        log.info("request to add event with id = {} to Compilation with id = {}", eventId, compId);
        compilationService.addEventToCompilation(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    public void unpinCompilation(@PathVariable Long compId) {
        log.info("request to unpin Compilation id = {}", compId);
        compilationService.unpinCompilation(compId);
    }

    @PatchMapping("/{compId}/pin")
    public void pinCompilation(@PathVariable Long compId) {
        log.info("request to pin Compilation id = {}", compId);
        compilationService.pinCompilation(compId);
    }
}
