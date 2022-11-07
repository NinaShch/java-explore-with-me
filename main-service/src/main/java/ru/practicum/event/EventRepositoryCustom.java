package ru.practicum.event;

import ru.practicum.category.entity.Category;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.State;
import ru.practicum.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepositoryCustom {
    List<Event> findByParamsCommon(String text, List<Category> categories, Boolean paid, LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd, boolean onlyAvailable, int from, int size);

    List<Event> findByParamsForAdmin(List<User> users, List<State> states, List<Category> categories,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);
}
