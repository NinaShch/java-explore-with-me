package ru.practicum.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.event.model.Status;

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
