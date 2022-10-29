package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.hit.HitStorage;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final HitStorage hitStorage;

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(URLDecoder.decode(start, Charset.defaultCharset()),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(URLDecoder.decode(end, Charset.defaultCharset()),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (uris == null) {
            return hitStorage.getStats(startTime, endTime, unique);
        } else {
            return uris
                    .stream()
                    .flatMap((uri) -> hitStorage.getStatsByUri(startTime, endTime, uri, unique).stream())
                    .collect(Collectors.toList());
        }
    }
}
