package ru.practicum.user.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@RequiredArgsConstructor
public class NewUserDto {
    @NotBlank
    @Length(max = 128)
    private String name;
    @Email
    private String email;
}
