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
            // 1. 获取用户的观影历史和互动记录
            List<MovieInteraction> userInteractions = movieInteractionRepository.findByUserId(userId);
            
            // 2. 获取所有电影
            List<Movie> allMovies = movieRepository.findAll();
            
            // 如果用户没有任何互动记录，返回所有符合筛选条件的电影
            if (userInteractions.isEmpty()) {
                List<Movie> filteredMovies = filterMovies(allMovies, filters);
                if (filteredMovies.isEmpty()) {
                    return ApiResponse.success(new ArrayList<>(), "没有找到符合条件的电影");
                }
                return ApiResponse.success(filteredMovies, "获取推荐成功");
            }

            // 3. 获取用户喜欢的电影类型
            Set<String> userPreferredCategories = new HashSet<>();
            List<Long> interactedMovieIds = new ArrayList<>();
            
            for (MovieInteraction interaction : userInteractions) {
                // 只考虑用户点赞或有播放记录的电影
                Boolean liked = interaction.getLiked();
                Integer playCount = interaction.getPlayCount();
                
                if ((liked != null && liked) || (playCount != null && playCount > 0)) {
                    interactedMovieIds.add(interaction.getMovieId());
                }
            }
            
            // 如果用户没有点赞或播放任何电影，返回所有符合筛选条件的电影
            if (interactedMovieIds.isEmpty()) {
                List<Movie> filteredMovies = filterMovies(allMovies, filters);
                if (filteredMovies.isEmpty()) {
                    return ApiResponse.success(new ArrayList<>(), "没有找到符合条件的电影");
                }
                return ApiResponse.success(filteredMovies, "获取推荐成功");
            }

            // 获取这些电影的类型
            List<Movie> interactedMovies = movieRepository.findAllById(interactedMovieIds);
            for (Movie movie : interactedMovies) {
                if (movie.getCategories() != null && !movie.getCategories().isEmpty()) {
                    userPreferredCategories.addAll(Arrays.asList(movie.getCategories().split(",")));
                }
            }

            // 4. 筛选推荐电影
            List<Movie> recommendedMovies = allMovies.stream()
                .filter(movie -> {
                    // 排除用户已经互动过的电影
                    if (interactedMovieIds.contains(movie.getId())) {
                        return false;
                    }
                    
                    // 检查是否包含用户喜欢的类型
                    if (movie.getCategories() != null && !movie.getCategories().isEmpty()) {
                        List<String> movieCategories = Arrays.asList(movie.getCategories().split(","));
                        return movieCategories.stream().anyMatch(userPreferredCategories::contains);
                    }
                    return false;
                })
                .collect(Collectors.toList());

            // 5. 应用过滤条件
            recommendedMovies = filterMovies(recommendedMovies, filters);
            
            // 如果没有找到符合条件的推荐电影，返回空列表
            if (recommendedMovies.isEmpty()) {
                return ApiResponse.success(new ArrayList<>(), "没有找到符合条件的推荐电影");
            }

            // 6. 按评分排序
            recommendedMovies.sort((m1, m2) -> {
                double rating1 = m1.getRating() != null ? m1.getRating() : 0.0;
                double rating2 = m2.getRating() != null ? m2.getRating() : 0.0;
                return Double.compare(rating2, rating1);
            });
            
            return ApiResponse.success(recommendedMovies, "获取推荐成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "获取推荐失败：" + e.getMessage());
        }
    }

    // 应用过滤条件的辅助方法
    private List<Movie> filterMovies(List<Movie> movies, Map<String, Object> filters) {
        return movies.stream()
            .filter(movie -> {
                // 类型过滤
                if (filters.containsKey("categoryId") && filters.get("categoryId") != null) {
                    String movieCategories = movie.getCategories();
                    if (movieCategories == null || movieCategories.isEmpty()) {
                        return false;
                    }
                    String categoryId = String.valueOf(filters.get("categoryId"));
                    if (!movieCategories.contains(categoryId)) {
                        return false;
                    }
                }
                
                // 地区过滤
                if (filters.containsKey("regionId") && filters.get("regionId") != null) {
                    Long regionId = Long.valueOf(String.valueOf(filters.get("regionId")));
                    if (movie.getRegion() == null || !Objects.equals(movie.getRegion(), regionId)) {
                        return false;
                    }
                }
                
                // 年份过滤
                if (filters.containsKey("year") && filters.get("year") != null) {
                    Integer year = (Integer) filters.get("year");
                    if (movie.getYear() == null || !Objects.equals(movie.getYear(), year)) {
                        return false;
                    }
                }
                
                // 价格过滤
                if (filters.containsKey("maxPrice") && filters.get("maxPrice") != null) {
                    Integer maxPrice = (Integer) filters.get("maxPrice");
                    if (movie.getPrice() == null || movie.getPrice() > maxPrice) {
                        return false;
                    }
                }
                
                return true;
            })
            .collect(Collectors.toList());
    }
} 