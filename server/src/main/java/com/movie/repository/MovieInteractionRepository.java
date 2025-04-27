package com.movie.repository;

import com.movie.entity.MovieInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieInteractionRepository extends JpaRepository<MovieInteraction, Long> {
    Optional<MovieInteraction> findByUserIdAndMovieId(Long userId, Long movieId);
} 