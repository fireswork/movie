package com.movie.config;

import com.movie.entity.User;
import com.movie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 检查admin用户是否存在
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User(
                "admin",
                passwordEncoder.encode("123456"),
                "男",
                0,
                "管理员",
                "00000000000",
                true
            );
            userRepository.save(admin);
        }
    }
} 