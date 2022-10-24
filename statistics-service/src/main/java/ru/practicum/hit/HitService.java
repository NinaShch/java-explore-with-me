package ru.practicum.hit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HitService {

    private final HitStorage hitStorage;

    public void postHit(EndpointHitDto endpointHitDto) {
        hitStorage.save(HitMapper.toEndpointHit(endpointHitDto));
    }
}
