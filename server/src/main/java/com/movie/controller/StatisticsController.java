package com.movie.controller;

import com.movie.common.ApiResponse;
import com.movie.entity.Movie;
import com.movie.entity.MovieInteraction;
import com.movie.entity.Order;
import com.movie.entity.User;
import com.movie.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MovieInteractionRepository movieInteractionRepository;

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> getOverviewStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 获取用户总数
        long userCount = userRepository.count();
        statistics.put("userCount", userCount);
        
        // 获取电影总数
        long movieCount = movieRepository.count();
        statistics.put("movieCount", movieCount);
        
        return ApiResponse.success(statistics, "获取成功");
    }

    @GetMapping("/sales")
    public ApiResponse<List<Map<String, Object>>> getSalesStatistics() {
        // 获取最近30天的销售数据
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Order> orders = orderRepository.findByCreatedAtAfter(thirtyDaysAgo);
        
        // 按日期分组统计
        Map<String, Double> dailySales = orders.stream()
            .collect(Collectors.groupingBy(
                order -> order.getCreatedAt().toLocalDate().toString(),
                Collectors.summingDouble(Order::getAmount)
            ));
        
        // 构建返回数据
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Double> entry : dailySales.entrySet()) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", entry.getKey());
            dayData.put("amount", entry.getValue());
            result.add(dayData);
        }
        
        return ApiResponse.success(result, "获取成功");
    }

    @GetMapping("/movie-interactions")
    public ApiResponse<List<Map<String, Object>>> getMovieInteractionStatistics() {
        // 获取所有电影的互动数据
        List<Movie> movies = movieRepository.findAll();
        List<MovieInteraction> interactions = movieInteractionRepository.findAll();
        
        // 统计每部电影的点赞数和播放次数
        Map<Long, Long> likesCount = interactions.stream()
            .filter(i -> i.getLiked() != null && i.getLiked())
            .collect(Collectors.groupingBy(MovieInteraction::getMovieId, Collectors.counting()));
        
        // 构建返回数据
        List<Map<String, Object>> result = movies.stream()
            .map(movie -> {
                Map<String, Object> movieData = new HashMap<>();
                movieData.put("movieId", movie.getId());
                movieData.put("title", movie.getTitle());
                movieData.put("likes", likesCount.getOrDefault(movie.getId(), 0L));
                return movieData;
            })
            .sorted((m1, m2) -> Long.compare((Long)m2.get("likes"), (Long)m1.get("likes")))
            .limit(10)
            .collect(Collectors.toList());
        
        return ApiResponse.success(result, "获取成功");
    }

    @GetMapping("/user-preferences")
    public ApiResponse<List<Map<String, Object>>> getUserPreferenceStatistics() {
        // 获取所有用户的观影记录和评分
        List<MovieInteraction> interactions = movieInteractionRepository.findAll();
        List<Movie> movies = movieRepository.findAll();
        List<User> users = userRepository.findAll();
        
        // 按职业和电影类型统计偏好
        Map<String, Map<String, Integer>> jobPreferences = new HashMap<>();
        
        for (MovieInteraction interaction : interactions) {
            Optional<User> userOpt = users.stream()
                .filter(u -> u.getId().equals(interaction.getUserId()))
                .findFirst();
            Optional<Movie> movieOpt = movies.stream()
                .filter(m -> m.getId().equals(interaction.getMovieId()))
                .findFirst();
                
            if (userOpt.isPresent() && movieOpt.isPresent()) {
                User user = userOpt.get();
                Movie movie = movieOpt.get();
                String profession = user.getProfession();
                
                if (profession != null && movie.getCategories() != null) {
                    String[] categories = movie.getCategories().split(",");
                    
                    jobPreferences.computeIfAbsent(profession, k -> new HashMap<>());
                    for (String category : categories) {
                        jobPreferences.get(profession).merge(category.trim(), 1, Integer::sum);
                    }
                }
            }
        }
        
        // 构建返回数据
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, Integer>> entry : jobPreferences.entrySet()) {
            Map<String, Object> jobData = new HashMap<>();
            jobData.put("job", entry.getKey());
            jobData.put("preferences", entry.getValue());
            result.add(jobData);
        }
        
        return ApiResponse.success(result, "获取成功");
    }
} 