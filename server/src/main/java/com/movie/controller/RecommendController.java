package com.movie.controller;

import com.movie.common.ApiResponse;
import com.movie.entity.Movie;
import com.movie.entity.MovieInteraction;
import com.movie.repository.MovieRepository;
import com.movie.repository.MovieInteractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieInteractionRepository movieInteractionRepository;

    @PostMapping
    public ApiResponse<List<Movie>> getRecommendations(
            @RequestParam Long userId,
            @RequestBody Map<String, Object> filters) {
        
        try {
            // 1. 获取用户的观影历史和评分
            List<MovieInteraction> userInteractions = movieInteractionRepository.findByUserId(userId);
            
            // 2. 获取所有电影
            List<Movie> allMovies = movieRepository.findAll();
            
            // 3. 应用过滤条件
            List<Movie> filteredMovies = allMovies.stream()
                .filter(movie -> {
                    // 类型过滤
                    if (filters.containsKey("categoryId") && filters.get("categoryId") != null) {
                        String movieCategories = movie.getCategories();
                        String categoryId = String.valueOf(filters.get("categoryId"));
                        if (!movieCategories.contains(categoryId)) {
                            return false;
                        }
                    }
                    
                    // 地区过滤
                    if (filters.containsKey("regionId") && filters.get("regionId") != null) {
                        Long regionId = Long.valueOf(String.valueOf(filters.get("regionId")));
                        if (!Objects.equals(movie.getRegion(), regionId)) {
                            return false;
                        }
                    }
                    
                    // 年份过滤
                    if (filters.containsKey("year") && filters.get("year") != null) {
                        Integer year = (Integer) filters.get("year");
                        if (!Objects.equals(movie.getYear(), year)) {
                            return false;
                        }
                    }
                    
                    // 价格过滤
                    if (filters.containsKey("maxPrice") && filters.get("maxPrice") != null) {
                        Integer maxPrice = (Integer) filters.get("maxPrice");
                        if (movie.getPrice() > maxPrice) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
            
            // 4. 计算推荐分数并排序
            List<Movie> recommendedMovies = filteredMovies.stream()
                .sorted((m1, m2) -> Double.compare(m2.getRating(), m1.getRating()))
                .collect(Collectors.toList());
            
            return ApiResponse.success(recommendedMovies, "获取推荐成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "获取推荐失败：" + e.getMessage());
        }
    }
} 