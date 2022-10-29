package ru.practicum.event;

import ru.practicum.event.entity.Event;

import java.util.Comparator;
import java.util.Map;

class EventByViewsComparator implements Comparator<Event> {
    private final Map<Long, Long> viewsMap;

    EventByViewsComparator(Map<Long, Long> viewsMap) {
        this.viewsMap = viewsMap;
    }

    @Override
    public int compare(Event e1, Event e2) {
        Long v1 = viewsMap.get(e1.getId());
        Long v2 = viewsMap.get(e2.getId());
        return Long.compare(v1 != null ? v1 : 0L, v2 != null ? v2 : 0L);
    }
}
