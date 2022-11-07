package ru.practicum.hit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.hit.dto.EndpointHitDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/hit")
public class HitController {

    private final HitService hitService;

    @PostMapping
    public void postHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("post hit");
        hitService.postHit(endpointHitDto);
    }
}
