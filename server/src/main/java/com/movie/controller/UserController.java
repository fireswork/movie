package com.movie.controller;

import com.movie.entity.User;
import com.movie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 获取用户列表（分页）
    @GetMapping
    public ResponseEntity<ApiResponse<Page<User>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone) {
        Page<User> users = userService.findUsers(username, phone, PageRequest.of(page, size));
        return ResponseEntity.ok(new ApiResponse<>(200, users, "获取成功"));
    }

    // 获取单个用户
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(new ApiResponse<>(200, user, "获取成功")))
                .orElse(ResponseEntity.ok(new ApiResponse<>(400, null, "用户不存在")));
    }

    // 创建用户
    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        User savedUser = userService.save(user);
        return ResponseEntity.ok(new ApiResponse<>(200, savedUser, "创建成功"));
    }

    // 更新用户
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
        if (!userService.findById(id).isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(400, null, "用户不存在"));
        }
        user.setId(id);
        User updatedUser = userService.save(user);
        return ResponseEntity.ok(new ApiResponse<>(200, updatedUser, "更新成功"));
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        if (!userService.findById(id).isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(400, null, "用户不存在"));
        }
        userService.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(200, null, "删除成功"));
    }
}

class ApiResponse<T> {
    private int code;
    private T data;
    private String message;

    public ApiResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 