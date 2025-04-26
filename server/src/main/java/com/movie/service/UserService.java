package com.movie.service;

import com.movie.dto.AuthRequest;
import com.movie.dto.AuthResponse;
import com.movie.entity.User;
import com.movie.repository.UserRepository;
import com.movie.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        return new CustomUserDetails(user);
    }

    public AuthResponse register(User user) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 对密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 默认为普通用户
        user.setAdmin(false);
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        // 返回认证响应
        return new AuthResponse(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.isAdmin()
        );
    }
    
    public AuthResponse authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 返回认证响应
        return new AuthResponse(
            user.getId(),
            user.getUsername(),
            user.isAdmin()
        );
    }
} 