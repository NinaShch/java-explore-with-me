package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class NewCompilationDto {
    private List<Long> events;
    private Boolean pinned;
    @NotBlank
    private String title;
}
