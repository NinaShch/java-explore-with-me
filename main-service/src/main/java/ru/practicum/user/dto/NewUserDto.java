package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class NewUserDto {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
