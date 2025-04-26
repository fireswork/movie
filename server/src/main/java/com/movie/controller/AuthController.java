package com.movie.controller;

import com.movie.dto.AuthRequest;
import com.movie.dto.AuthResponse;
import com.movie.dto.RegisterRequest;
import com.movie.entity.User;
import com.movie.service.UserService;
import com.movie.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {
    
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            User user = new User(
                request.getUsername(),
                request.getPassword(),
                request.getGender(),
                request.getAge(),
                request.getProfession(),
                request.getPhone(),
                false
            );
            AuthResponse response = userService.register(user);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = userService.authenticate(request.getUsername(), request.getPassword());
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
} 