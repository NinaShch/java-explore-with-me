package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.entity.Category;
import ru.practicum.compilation.CompilationRepository;
import ru.practicum.compilation.entity.Compilation;
import ru.practicum.event.EventRepository;
import ru.practicum.event.entity.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.UserRepository;
import ru.practicum.user.entity.User;

@Service
@RequiredArgsConstructor
// если методы из этого класса перенести в контекстные классы,
// то так как они используются не только в своих сервисах,
// получается циклическая зависимость у UserService и EventService
// циклические зависимости это антипаттерн проектирования и так категорически не соведуют делать,
// если только вообще нет другой возможности. Поэтому считаю правильным оставить свой вариант.

public class HelperService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final CompilationRepository compilationRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id= %d was not found.", id)));
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id= %d was not found.", id)));
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id= %d was not found.", id)));
    }

    public Compilation getCompilationById(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id= %d was not found.", id)));
    }

}
