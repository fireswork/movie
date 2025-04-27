package com.movie.controller;

import com.movie.entity.Collection;
import com.movie.entity.Movie;
import com.movie.repository.CollectionRepository;
import com.movie.repository.MovieRepository;
import com.movie.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.ArrayList;

@RestController
@RequestMapping("/collections")
public class CollectionController {

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private MovieRepository movieRepository;

    @GetMapping
    public ApiResponse<List<Collection>> getUserCollections(@RequestParam Long userId) {
        List<Collection> collections = collectionRepository.findByUserId(userId);
        return ApiResponse.success(collections, "获取成功");
    }

    @GetMapping("/{id}/movies")
    public ApiResponse<List<Movie>> getCollectionMovies(@PathVariable Long id) {
        return collectionRepository.findById(id)
                .map(collection -> {
                    if (collection.getMovieIds() == null || collection.getMovieIds().isEmpty()) {
                        return ApiResponse.success(new ArrayList<>(), "获取成功");
                    }
                    List<Long> movieIds = Arrays.stream(collection.getMovieIds().split(","))
                            .filter(s -> !s.isEmpty())
                            .map(Long::parseLong)
                            .collect(Collectors.toList());
                    List<Movie> movies = movieRepository.findAllById(movieIds);
                    return ApiResponse.success(movies, "获取成功");
                })
                .orElse(ApiResponse.error(400, "收藏夹不存在"));
    }

    @PostMapping
    public ApiResponse<Collection> createCollection(@RequestBody Collection collection) {
        Collection savedCollection = collectionRepository.save(collection);
        return ApiResponse.success(savedCollection, "创建成功");
    }

    @PutMapping("/{id}")
    public ApiResponse<Collection> updateCollection(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String movieIds) {
        return collectionRepository.findById(id)
                .map(collection -> {
                    if (name != null) {
                        collection.setName(name);
                    }
                    if (movieIds != null) {
                        collection.setMovieIds(movieIds);
                    }
                    Collection updatedCollection = collectionRepository.save(collection);
                    return ApiResponse.success(updatedCollection, "更新成功");
                })
                .orElse(ApiResponse.error(400, "收藏夹不存在"));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCollection(@PathVariable Long id) {
        if (!collectionRepository.existsById(id)) {
            return ApiResponse.error(400, "收藏夹不存在");
        }
        collectionRepository.deleteById(id);
        return ApiResponse.success(null, "删除成功");
    }

    @PostMapping("/{id}/movies")
    public ApiResponse<Collection> addMovieToCollection(@PathVariable Long id, @RequestParam Long movieId) {
        return collectionRepository.findById(id)
                .map(collection -> {
                    String currentIds = collection.getMovieIds();
                    List<String> movieIds = currentIds.isEmpty() ? 
                        new java.util.ArrayList<>() : 
                        new java.util.ArrayList<>(Arrays.asList(currentIds.split(",")));
                    
                    String movieIdStr = String.valueOf(movieId);
                    if (!movieIds.contains(movieIdStr)) {
                        movieIds.add(movieIdStr);
                        collection.setMovieIds(String.join(",", movieIds));
                        Collection updatedCollection = collectionRepository.save(collection);
                        return ApiResponse.success(updatedCollection, "添加成功");
                    }
                    return ApiResponse.<Collection>error(400, "电影已在收藏夹中");
                })
                .orElse(ApiResponse.<Collection>error(400, "收藏夹不存在"));
    }

    @DeleteMapping("/{id}/movies/{movieId}")
    public ApiResponse<Collection> removeMovieFromCollection(@PathVariable Long id, @PathVariable Long movieId) {
        return collectionRepository.findById(id)
                .map(collection -> {
                    String currentIds = collection.getMovieIds();
                    if (currentIds.isEmpty()) {
                        return ApiResponse.<Collection>error(400, "电影不在收藏夹中");
                    }
                    
                    List<String> movieIds = new java.util.ArrayList<>(Arrays.asList(currentIds.split(",")));
                    String movieIdStr = String.valueOf(movieId);
                    if (movieIds.remove(movieIdStr)) {
                        collection.setMovieIds(String.join(",", movieIds));
                        Collection updatedCollection = collectionRepository.save(collection);
                        return ApiResponse.success(updatedCollection, "移除成功");
                    }
                    return ApiResponse.<Collection>error(400, "电影不在收藏夹中");
                })
                .orElse(ApiResponse.<Collection>error(400, "收藏夹不存在"));
    }

    @GetMapping("/check")
    public ApiResponse<Boolean> checkMovieInCollections(@RequestParam Long userId, @RequestParam Long movieId) {
        List<Collection> collections = collectionRepository.findByUserId(userId);
        boolean isCollected = collections.stream()
                .anyMatch(collection -> {
                    String[] movieIds = collection.getMovieIds().split(",");
                    return Arrays.asList(movieIds).contains(String.valueOf(movieId));
                });
        return ApiResponse.success(isCollected, "查询成功");
    }
} 