package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.paging.OffsetLimitPageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public List<CategoryDto> getCategorise(int from, int size) {
        Pageable pageable = OffsetLimitPageable.create(from, size, Sort.by(Sort.Direction.ASC, "id"));
        return categoryRepository.getAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(Long catId) {
        return CategoryMapper.toCategoryDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category not found")));
    }

    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new NotFoundException("Category not found"));
        category.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toNewCategory(newCategoryDto)));
    }

    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        List<Event> events = eventRepository.findByCategory(category);
        if (!events.isEmpty()) throw new ForbiddenException("Category with events", "Category with events");
        categoryRepository.delete(category);
    }
}
