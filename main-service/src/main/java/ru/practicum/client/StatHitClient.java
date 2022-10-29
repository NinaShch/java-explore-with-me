package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.dto.ViewStatsDto;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatHitClient extends BaseClient {

    private static final String API_PREFIX = "/";

    public StatHitClient(@Value("${statistics-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public void hitRequest(HitDto hitDto) {
        post("/hit", hitDto);
    }

/*
    Можно было бы создать пул потоков и сделать эндпоинт на отдельном потоке, но в этом случае
    количество просмотров будет зависеть от того какой запрос отработает первым - запрос клиентом события
    или запрос к сервису статистики на увеличение количества просмотров

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    public void hitRequest(HitDto hitDto) {
       executorService.execute(() -> {
            post("/hit", hitDto);
        });
    }
*/

    public Long statsRequest(Long eventId) {
        String[] uris = new String[] { "/event/" + eventId };

        String start = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now().minusMonths(6));
        String end = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());

        Map<String, Object> parameters = Map.of(
                "start", URLEncoder.encode(start, Charset.defaultCharset()),
                "end", URLEncoder.encode(end, Charset.defaultCharset()),
                "uris", uris
        );

        ViewStatsDto[] viewStatsArray = get("/stats?start={start}&end={end}&uris={uris}", parameters, ViewStatsDto[].class).getBody();
        if (viewStatsArray != null) {
            if (viewStatsArray.length == 0) {
                return 0L;
            }
            ViewStatsDto viewStats = viewStatsArray[0];
            if (viewStats != null) {
                return viewStats.getHits();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public Map<Long, Long> viewsMapRequest(List<Long> eventIds) {
        List<String> uris = eventIds.stream().map(eventId -> "/event/" + eventId).collect(Collectors.toList());
        String start = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now().minusMonths(6));
        String end = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());

        Map<String, Object> parameters = Map.of(
                "start", URLEncoder.encode(start, Charset.defaultCharset()),
                "end", URLEncoder.encode(end, Charset.defaultCharset()),
                "uris", uris
        );

        ViewStatsDto[] viewStatsArray = get("/stats?start={start}&end={end}&uris={uris}", parameters, ViewStatsDto[].class).getBody();
        if (viewStatsArray != null) {
            Map<Long, Long> viewsMap = new HashMap<>();
            Arrays.stream(viewStatsArray).forEach(
                    viewStat -> {
                        String eventId = viewStat.getUri().split("/")[2];
                        viewsMap.put(Long.parseLong(eventId), viewStat.getHits());
                    }
            );
            return viewsMap;
        } else {
            return new HashMap<>();
        }
    }
}
