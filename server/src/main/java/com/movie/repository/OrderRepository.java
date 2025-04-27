package com.movie.repository;

import com.movie.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByCreatedAtAfter(LocalDateTime date);
    Optional<Order> findByUserIdAndMovieIdAndStatus(Long userId, Long movieId, String status);
} 