package ru.practicum.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.category.entity.Category;
import ru.practicum.event.entity.Event;
import ru.practicum.user.entity.User;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {

    List<Event> findByInitiator(User user, Pageable pageable);

    List<Event> findByCategory(Category category);
}
