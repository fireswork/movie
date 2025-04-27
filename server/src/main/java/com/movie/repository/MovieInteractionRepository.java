package com.movie.repository;

import com.movie.entity.MovieInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieInteractionRepository extends JpaRepository<MovieInteraction, Long> {
    Optional<MovieInteraction> findByUserIdAndMovieId(Long userId, Long movieId);
    List<MovieInteraction> findByUserId(Long userId);
} 