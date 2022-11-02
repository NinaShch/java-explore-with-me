package ru.practicum.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.DateTimeUtils;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.entity.ParticipationRequest;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {
    @Mapping(target = "created", source = "participationRequest.created", dateFormat = DateTimeUtils.DATE_TIME_FORMAT)
    @Mapping(target = "event", source = "participationRequest.event.id")
    @Mapping(target = "requester", source = "participationRequest.requester.id")
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest);
}
