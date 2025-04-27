package com.movie.repository;

import com.movie.entity.UserMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserMovieRepository extends JpaRepository<UserMovie, Long> {
    Optional<UserMovie> findByUserIdAndMovieIdAndExpiredAtGreaterThan(Long userId, Long movieId, LocalDateTime now);
} 