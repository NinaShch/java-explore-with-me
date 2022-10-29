package ru.practicum.request.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.event.entity.Status;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ParticipationRequestDto {

    private String created;
    @NotNull
    private Long event;
    @NotNull
    private Long requester;
    private Status status;
    private Long id;
}
