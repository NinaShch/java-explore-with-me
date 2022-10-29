package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.HelperService;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.entity.Category;
import ru.practicum.event.EventRepository;
import ru.practicum.event.entity.Event;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.paging.OffsetLimitPageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;
    private final HelperService helperService;

    public List<CategoryDto> getCategorise(int from, int size) {
        Pageable pageable = OffsetLimitPageable.create(from, size, Sort.by(Sort.Direction.ASC, "id"));
        return categoryRepository.getAll(pageable).stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(Long catId) {
        return categoryMapper.toCategoryDto(helperService.getCategoryById(catId));
    }

    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = helperService.getCategoryById(categoryDto.getId());
        category.setName(categoryDto.getName());
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toNewCategory(newCategoryDto)));
    }

    public void deleteCategory(Long categoryId) {
        Category category = helperService.getCategoryById(categoryId);
        List<Event> events = eventRepository.findByCategory(category);
        if (!events.isEmpty()) throw new ForbiddenException("Category with events", "Category with events");
        categoryRepository.delete(category);
    }
}
