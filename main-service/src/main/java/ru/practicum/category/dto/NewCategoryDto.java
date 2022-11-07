package ru.practicum.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
// класс создан так как есть в спецификации в сваггере
public class NewCategoryDto {

    @NotBlank
    private String name;
}
