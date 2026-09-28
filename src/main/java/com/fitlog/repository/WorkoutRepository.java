package com.fitlog.repository;

import com.fitlog.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA Repository for Workout entity.
 */
@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findByUserId(Long userId);

    List<Workout> findByUserIdAndWorkoutDate(Long userId, LocalDate workoutDate);

    List<Workout> findByUserIdAndWorkoutDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    List<Workout> findByUserIdAndWorkoutDateBetweenOrderByWorkoutDateAsc(Long userId, LocalDate startDate, LocalDate endDate);
}
