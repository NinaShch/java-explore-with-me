package ru.practicum.hit;

import ru.practicum.DateTimeUtils;
import ru.practicum.hit.dto.EndpointHitDto;
import ru.practicum.hit.entity.EndpointHit;

public class HitMapper {

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        return new EndpointHit(
                endpointHitDto.getId(),
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                DateTimeUtils.parseDate(endpointHitDto.getTimestamp())
        );
    }
}
