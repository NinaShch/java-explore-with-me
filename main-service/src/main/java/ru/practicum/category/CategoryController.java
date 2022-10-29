package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;

import java.util.List;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping(produces = "application/json")
    public List<CategoryDto> getCategories(
            @RequestParam(defaultValue = "0", required = false) int from,
            @RequestParam(defaultValue = "10", required = false) int size
    ) {
        log.info("request categories");
        return categoryService.getCategorise(from, size);
    }

    @GetMapping(value = "/{catId}", produces = "application/json")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("request category id = {}", catId);
        return categoryService.getCategoryById(catId);
    }

}
