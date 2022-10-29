package ru.practicum.hit;

import ru.practicum.stats.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository {
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, boolean unique);

    List<ViewStatsDto> getStatsByUri(LocalDateTime start, LocalDateTime end, String uri, boolean unique);
}
