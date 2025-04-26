package com.movie.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApiResponse<T> {
    @JsonProperty("code")
    private int code;
    
    @JsonProperty("data")
    private T data;
    
    @JsonProperty("message")
    private String message;

    private ApiResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, data, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, data, message);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, null, message);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, null, message);
    }
} 