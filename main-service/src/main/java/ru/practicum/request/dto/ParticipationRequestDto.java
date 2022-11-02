package ru.practicum.request.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.event.entity.Status;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ParticipationRequestDto {

    private String created;
    private Long event;
    private Long requester;
    private Status status;
    private Long id;
}
