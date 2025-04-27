package com.movie.repository;

import com.movie.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Message> findAllByOrderByCreatedAtDesc();
} 