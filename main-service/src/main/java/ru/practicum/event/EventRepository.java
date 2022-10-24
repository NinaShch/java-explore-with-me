package ru.practicum.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {

    List<Event> findByInitiator(User user, Pageable pageable);

    List<Event> findByCategory(Category category);
}
