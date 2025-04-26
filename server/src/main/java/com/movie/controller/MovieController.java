package com.movie.controller;

import com.movie.common.ApiResponse;
import com.movie.entity.Movie;
import com.movie.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    @GetMapping
    public ApiResponse<List<Movie>> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        return ApiResponse.success(movies, "获取成功");
    }

    @GetMapping("/{id}")
    public ApiResponse<Movie> getMovie(@PathVariable Long id) {
        return movieRepository.findById(id)
                .map(movie -> ApiResponse.success(movie, "获取成功"))
                .orElse(ApiResponse.error(400, "电影不存在"));
    }

    @PostMapping
    public ApiResponse<Movie> createMovie(@RequestBody Movie movie) {
        // 如果是免费电影，价格设为0
        if (movie.getIsFree()) {
            movie.setPrice(0.0);
        }
        // 如果是付费电影，价格不能为空或0
        else if (movie.getPrice() == null || movie.getPrice() <= 0) {
            return ApiResponse.error(400, "付费电影必须设置价格");
        }
        
        movie.setId(null);
        Movie savedMovie = movieRepository.save(movie);
        return ApiResponse.success(savedMovie, "创建成功");
    }

    @PutMapping("/{id}")
    public ApiResponse<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        if (!movieRepository.existsById(id)) {
            return ApiResponse.error(400, "电影不存在");
        }

        // 如果是免费电影，价格设为0
        if (movie.getIsFree()) {
            movie.setPrice(0.0);
        }
        // 如果是付费电影，价格不能为空或0
        else if (movie.getPrice() == null || movie.getPrice() <= 0) {
            return ApiResponse.error(400, "付费电影必须设置价格");
        }

        movie.setId(id);
        Movie updatedMovie = movieRepository.save(movie);
        return ApiResponse.success(updatedMovie, "更新成功");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMovie(@PathVariable Long id) {
        if (!movieRepository.existsById(id)) {
            return ApiResponse.error(400, "电影不存在");
        }
        movieRepository.deleteById(id);
        return ApiResponse.success(null, "删除成功");
    }
} 