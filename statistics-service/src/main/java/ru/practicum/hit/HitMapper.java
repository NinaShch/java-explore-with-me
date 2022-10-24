package ru.practicum.hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HitMapper {

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        return new EndpointHit(
                endpointHitDto.getId(),
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                LocalDateTime.parse(endpointHitDto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }
}
