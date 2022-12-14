package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping(produces = "application/json")
    public List<CompilationDto> getCompilations(
            @RequestParam(defaultValue = "false", required = false) boolean pinned,
            @RequestParam(defaultValue = "0", required = false) int from,
            @RequestParam(defaultValue = "10", required = false) int size
    ) {
        log.info("Request compilations, pinned={}", pinned);
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping(value = "/{compId}", produces = "application/json")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        log.info("request compilation id = {}", compId);
        return compilationService.getCompilationDtoById(compId);
    }
}

