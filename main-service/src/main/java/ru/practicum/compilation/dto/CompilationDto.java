package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.event.dto.EventShortDto;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class CompilationDto {
    private List<EventShortDto> events;
    @NotNull
    private Long id;
    @NotNull
    private boolean pinned;
    @NotNull
    private String title;
}
