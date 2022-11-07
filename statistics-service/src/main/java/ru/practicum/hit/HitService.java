package ru.practicum.hit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.hit.dto.EndpointHitDto;

@Service
@RequiredArgsConstructor
public class HitService {

    private final HitStorage hitStorage;

    public void postHit(EndpointHitDto endpointHitDto) {
        hitStorage.save(HitMapper.toEndpointHit(endpointHitDto));
    }
}
