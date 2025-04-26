package com.movie.controller;

import com.movie.common.ApiResponse;
import com.movie.entity.Category;
import com.movie.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public ApiResponse<List<Category>> getCategories() {
        List<Category> categories = categoryRepository.findAll();
        return ApiResponse.success(categories, "获取成功");
    }

    @GetMapping("/{id}")
    public ApiResponse<Category> getCategory(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(category -> ApiResponse.success(category, "获取成功"))
                .orElse(ApiResponse.error(400, "分类不存在"));
    }

    @PostMapping
    public ApiResponse<Category> createCategory(@RequestBody Category category) {
        Category savedCategory = categoryRepository.save(category);
        return ApiResponse.success(savedCategory, "创建成功");
    }

    @PutMapping("/{id}")
    public ApiResponse<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        if (!categoryRepository.existsById(id)) {
            return ApiResponse.error(400, "分类不存在");
        }
        category.setId(id);
        Category updatedCategory = categoryRepository.save(category);
        return ApiResponse.success(updatedCategory, "更新成功");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            return ApiResponse.error(400, "分类不存在");
        }
        categoryRepository.deleteById(id);
        return ApiResponse.success(null, "删除成功");
    }
} 