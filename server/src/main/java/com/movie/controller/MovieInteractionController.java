package com.movie.controller;

import com.movie.entity.MovieInteraction;
import com.movie.repository.MovieInteractionRepository;
import com.movie.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movie-interactions")
public class MovieInteractionController {

    @Autowired
    private MovieInteractionRepository movieInteractionRepository;

    @GetMapping
    public ApiResponse<Page<MovieInteraction>> getAllInteractions(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // 构建动态查询条件
        Specification<MovieInteraction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (userId != null) {
                predicates.add(cb.equal(root.get("userId"), userId));
            }
            if (movieId != null) {
                predicates.add(cb.equal(root.get("movieId"), movieId));
            }
            
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };

        // 创建分页和排序
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        // 执行查询
        Page<MovieInteraction> interactions = movieInteractionRepository.findAll(spec, pageable);
        return ApiResponse.success(interactions, "获取成功");
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<MovieInteraction>> getUserInteractions(@PathVariable Long userId) {
        List<MovieInteraction> interactions = movieInteractionRepository.findByUserId(userId);
        return ApiResponse.success(interactions, "获取成功");
    }

    @GetMapping("/user/{userId}/movie/{movieId}")
    public ApiResponse<MovieInteraction> getInteraction(
            @PathVariable Long userId,
            @PathVariable Long movieId) {
        return movieInteractionRepository.findByUserIdAndMovieId(userId, movieId)
                .map(interaction -> ApiResponse.success(interaction, "获取成功"))
                .orElse(ApiResponse.success(null, "未找到互动记录"));
    }

    @PutMapping("/user/{userId}/movie/{movieId}")
    public ApiResponse<MovieInteraction> updateInteraction(
            @PathVariable Long userId,
            @PathVariable Long movieId,
            @RequestBody MovieInteraction interaction) {
        
        if (interaction == null) {
            return ApiResponse.error(400, "请求数据不能为空");
        }

        MovieInteraction existingInteraction = movieInteractionRepository
                .findByUserIdAndMovieId(userId, movieId)
                .orElseGet(() -> {
                    MovieInteraction newInteraction = new MovieInteraction();
                    newInteraction.setUserId(userId);
                    newInteraction.setMovieId(movieId);
                    return newInteraction;
                });

        // 更新互动信息
        if (interaction.getLiked() != null) {
            existingInteraction.setLiked(interaction.getLiked());
        }
        if (interaction.getRating() != null) {
            if (interaction.getRating() < 1 || interaction.getRating() > 5) {
                return ApiResponse.error(400, "评分必须在1-5之间");
            }
            existingInteraction.setRating(interaction.getRating());
        }
        if (interaction.getComment() != null) {
            if (interaction.getComment().trim().isEmpty()) {
                return ApiResponse.error(400, "评论内容不能为空");
            }
            existingInteraction.setComment(interaction.getComment().trim());
        }

        MovieInteraction savedInteraction = movieInteractionRepository.save(existingInteraction);
        return ApiResponse.success(savedInteraction, "更新成功");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteInteraction(@PathVariable Long id) {
        Optional<MovieInteraction> interaction = movieInteractionRepository.findById(id);
        if (interaction.isEmpty()) {
            return ApiResponse.error(400, "互动记录不存在");
        }
        
        movieInteractionRepository.deleteById(id);
        return ApiResponse.success(null, "删除成功");
    }
} 