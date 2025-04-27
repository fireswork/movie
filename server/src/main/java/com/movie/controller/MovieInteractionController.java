package com.movie.controller;

import com.movie.entity.MovieInteraction;
import com.movie.repository.MovieInteractionRepository;
import com.movie.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movie-interactions")
public class MovieInteractionController {

    @Autowired
    private MovieInteractionRepository movieInteractionRepository;

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
} 