package com.fitlog.repository;

import com.fitlog.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Goal entity.
 */
@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    Optional<Goal> findByUserIdAndWeekStartDate(Long userId, LocalDate weekStartDate);

    List<Goal> findByUserId(Long userId);

    List<Goal> findByUserIdOrderByWeekStartDateDesc(Long userId);
}
