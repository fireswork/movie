package com.movie.controller;

import com.movie.common.ApiResponse;
import com.movie.entity.Region;
import com.movie.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/regions")
public class RegionController {

    @Autowired
    private RegionRepository regionRepository;

    @GetMapping
    public ApiResponse<List<Region>> getRegions() {
        List<Region> regions = regionRepository.findAll();
        return ApiResponse.success(regions, "获取成功");
    }

    @GetMapping("/{id}")
    public ApiResponse<Region> getRegion(@PathVariable Long id) {
        return regionRepository.findById(id)
                .map(region -> ApiResponse.success(region, "获取成功"))
                .orElse(ApiResponse.error(400, "地区不存在"));
    }

    @PostMapping
    public ApiResponse<Region> createRegion(@RequestBody Region region) {
        Region savedRegion = regionRepository.save(region);
        return ApiResponse.success(savedRegion, "创建成功");
    }

    @PutMapping("/{id}")
    public ApiResponse<Region> updateRegion(@PathVariable Long id, @RequestBody Region region) {
        if (!regionRepository.existsById(id)) {
            return ApiResponse.error(400, "地区不存在");
        }
        region.setId(id);
        Region updatedRegion = regionRepository.save(region);
        return ApiResponse.success(updatedRegion, "更新成功");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRegion(@PathVariable Long id) {
        if (!regionRepository.existsById(id)) {
            return ApiResponse.error(400, "地区不存在");
        }
        regionRepository.deleteById(id);
        return ApiResponse.success(null, "删除成功");
    }
} 