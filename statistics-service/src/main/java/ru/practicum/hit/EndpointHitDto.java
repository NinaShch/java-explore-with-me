package ru.practicum.hit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class EndpointHitDto {
    Long id;
    String app;
    String uri;
    String ip;
    String timestamp;
}
