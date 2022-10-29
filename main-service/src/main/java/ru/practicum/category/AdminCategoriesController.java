package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.CategoryService;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class AdminCategoriesController {

    private final CategoryService categoryService;

    @PatchMapping (produces = "application/json", consumes = "application/json")
    public CategoryDto updateCategory(@RequestBody CategoryDto categoryDto) {
        log.info("request update category id = {}", categoryDto.getId());
        return categoryService.updateCategory(categoryDto);
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    public CategoryDto createCategory(@RequestBody NewCategoryDto newCategoryDto) {
        log.info("request create new category {}", newCategoryDto.getName());
        return categoryService.createCategory(newCategoryDto);
    }

    @DeleteMapping("/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId) {
        log.info("request to delete category id = {}", categoryId);
        categoryService.deleteCategory(categoryId);
    }
}
