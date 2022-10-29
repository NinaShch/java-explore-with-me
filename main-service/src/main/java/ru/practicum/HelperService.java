package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.entity.Category;
import ru.practicum.compilation.CompilationRepository;
import ru.practicum.compilation.entity.Compilation;
import ru.practicum.event.EventRepository;
import ru.practicum.event.entity.Event;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.UserRepository;
import ru.practicum.user.entity.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
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

    public LocalDateTime parseDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Wrong datetime " + dateString, "Wrong dateTime format");
        }
    }
}
