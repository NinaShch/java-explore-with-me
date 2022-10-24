package ru.practicum.request;

import java.time.format.DateTimeFormatter;

public class ParticipationRequestMapper {

    private ParticipationRequestMapper() {
    }

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto
                .builder()
                .created(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(participationRequest.getCreated()))
                .event(participationRequest.getEvent().getId())
                .requester(participationRequest.getRequester().getId())
                .status(participationRequest.getStatus())
                .id(participationRequest.getId())
                .build();
    }
}
