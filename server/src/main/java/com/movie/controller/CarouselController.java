package com.movie.controller;

import com.movie.common.ApiResponse;
import com.movie.entity.Carousel;
import com.movie.repository.CarouselRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/carousels")
public class CarouselController {

    @Autowired
    private CarouselRepository carouselRepository;

    @GetMapping
    public ApiResponse<List<Carousel>> getAllCarousels() {
        List<Carousel> carousels = carouselRepository.findAll();
        return ApiResponse.success(carousels, "获取成功");
    }

    @PostMapping
    public ApiResponse<Carousel> createCarousel(@RequestBody Carousel carousel) {
        carousel.setId(null);
        Carousel savedCarousel = carouselRepository.save(carousel);
        return ApiResponse.success(savedCarousel, "创建成功");
    }

    @PutMapping("/{id}")
    public ApiResponse<Carousel> updateCarousel(@PathVariable Long id, @RequestBody Carousel carousel) {
        if (!carouselRepository.existsById(id)) {
            return ApiResponse.error(400, "轮播图不存在");
        }
        carousel.setId(id);
        Carousel updatedCarousel = carouselRepository.save(carousel);
        return ApiResponse.success(updatedCarousel, "更新成功");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCarousel(@PathVariable Long id) {
        if (!carouselRepository.existsById(id)) {
            return ApiResponse.error(400, "轮播图不存在");
        }
        carouselRepository.deleteById(id);
        return ApiResponse.success(null, "删除成功");
    }
} 